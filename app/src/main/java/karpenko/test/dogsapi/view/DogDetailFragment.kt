package karpenko.test.dogsapi.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import karpenko.test.dogsapi.databinding.FragmentDogDetailBinding
import karpenko.test.dogsapi.model.DogPalette
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailBinding.inflate(inflater, container, false)
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


    private fun observeViewModel(){
        viewModel.dogInfoLV.observe(viewLifecycleOwner){ dog ->
            dog?.let {
                binding.dog = dog
                it.imageUrl?.let {
                    paletteBackground(it)
                }
            }
        }

    }

    private fun paletteBackground(url: String){
        Glide.with(this).asBitmap().load(url)
            .into(object : CustomTarget<Bitmap>(){
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


}