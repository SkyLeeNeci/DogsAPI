package karpenko.test.dogsapi.view

import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import karpenko.test.dogsapi.R
import karpenko.test.dogsapi.databinding.FragmentDogDetailBinding
import karpenko.test.dogsapi.databinding.SendSmsDialogBinding
import karpenko.test.dogsapi.model.DogBreed
import karpenko.test.dogsapi.model.DogPalette
import karpenko.test.dogsapi.model.SmsInfo
import karpenko.test.dogsapi.util.getProgressDrawable
import karpenko.test.dogsapi.util.loadImage
import karpenko.test.dogsapi.viewmodel.DogItemViewModel
import kotlinx.android.synthetic.main.fragment_dog_detail.*
import kotlinx.android.synthetic.main.fragment_dog_detail.dogNameTV
import kotlinx.android.synthetic.main.item_dog.*
import java.lang.RuntimeException

class DogDetailFragment : Fragment() {

    private var _binding: FragmentDogDetailBinding? = null
    private val binding: FragmentDogDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDogDetailBinding = null")

    private val viewModel: DogItemViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[DogItemViewModel::class.java]
    }

    private var dogUuid = 0
    private var _currentDog: DogBreed? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDogDetailBinding.inflate(inflater, container, false)
        inflateMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DogDetailFragmentArgs.fromBundle(it).uuid
        }
        viewModel.fetch(dogUuid)
        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.dogInfoLV.observe(viewLifecycleOwner) { dog ->
            _currentDog = dog
            dog?.let {
                binding.dog = dog
                it.imageUrl?.let {
                    paletteBackground(it)
                }
            }
        }

    }

    private fun paletteBackground(url: String) {
        Glide.with(this).asBitmap().load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate {
                        val color = it?.vibrantSwatch?.rgb ?: 0
                        val palette = DogPalette(color)
                        binding.palette = palette
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    private fun inflateMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_send_sms -> {
                        isSmsSending = true
                        (activity as MainActivity).checkSmsPermission()
                    }
                    R.id.action_share -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "*/*"
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                        intent.putExtra(Intent.EXTRA_STREAM, _currentDog?.imageUrl)
                        intent.putExtra(Intent.EXTRA_TEXT, "${_currentDog?.dogBreed} bred for ${_currentDog?.bredFor}")
                        startActivity(Intent.createChooser(intent, "Share with"))
                    }
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (isSmsSending && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${_currentDog?.dogBreed} bred for ${_currentDog?.bredFor}",
                    _currentDog?.imageUrl
                )
                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )
                AlertDialog.Builder(it).setView(dialogBinding.root)
                    .setPositiveButton("Send"){ _, _ ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }.setNegativeButton("Cancel"){_, _ ->

                    }.show()
                dialogBinding.smsInfo = smsInfo
                    }
            }
        }
    private fun sendSms(smsInfo: SmsInfo) {

        val intent = Intent(context, MainActivity:: class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = context?.getSystemService(SmsManager::class.java)
        sms?.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)

    }

    companion object {

        private var isSmsSending = false
    }

}