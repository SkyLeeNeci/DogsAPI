package karpenko.test.dogsapi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import karpenko.test.dogsapi.R
import karpenko.test.dogsapi.databinding.FragmentDogDetailBinding
import karpenko.test.dogsapi.databinding.FragmentDogsListBinding
import java.lang.RuntimeException

class DogDetailFragment : Fragment() {

    private var _binding: FragmentDogDetailBinding? = null
    private val binding: FragmentDogDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDogDetailBinding = null")

    private val args by navArgs<DogDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dogInfoFragment.text = args.dogUuid.toString()
    }
}