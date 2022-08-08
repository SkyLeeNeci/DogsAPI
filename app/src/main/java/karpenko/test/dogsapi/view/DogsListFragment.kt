package karpenko.test.dogsapi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import karpenko.test.dogsapi.databinding.FragmentDogsListBinding
import java.lang.RuntimeException

class DogsListFragment : Fragment() {

    private var _binding: FragmentDogsListBinding? = null
    private val binding: FragmentDogsListBinding
        get() = _binding ?: throw RuntimeException("FragmentDogsListBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsListBinding.inflate(inflater, container,false)
        return binding.root
    }
}