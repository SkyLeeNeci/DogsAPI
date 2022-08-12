package karpenko.test.dogsapi.view

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import karpenko.test.dogsapi.databinding.FragmentDogsListBinding
import karpenko.test.dogsapi.viewmodel.DogsListViewModel
import kotlinx.android.synthetic.main.fragment_dogs_list.*
import java.lang.RuntimeException

class DogsListFragment : Fragment() {

    private val viewModel: DogsListViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[DogsListViewModel::class.java]
    }
    private val dogsListAdapter = DogsListAdapter()

    private var _binding: FragmentDogsListBinding? = null
    private val binding: FragmentDogsListBinding
        get() = _binding ?: throw RuntimeException("FragmentDogsListBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
        binding.dogsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }
        observeViewModel()
        onRefreshAction()
    }

    private fun observeViewModel() {
        viewModel.listOfDogs.observe(viewLifecycleOwner) {
            it?.let {
                binding.dogsRecyclerView.visibility = View.VISIBLE
                dogsListAdapter.dogsList = it
            }
        }
        viewModel.dogsLoadError.observe(viewLifecycleOwner) {
            it?.let {
                binding.errorTV.visibility = if (it) View.VISIBLE else View.GONE
            }

        }
        viewModel.loading.observe(viewLifecycleOwner){
            it?.let {
                binding.mainProgressBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it){
                    errorTV.visibility = View.GONE
                    dogsRecyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun onRefreshAction(){

        binding.refreshLayoutMain.setOnRefreshListener {
            binding.errorTV.visibility = View.GONE
            binding.dogsRecyclerView.visibility = View.GONE
            binding.mainProgressBar.visibility = View.VISIBLE
            viewModel.refreshBypassCache()
            binding.refreshLayoutMain.isRefreshing = false
        }

    }


}