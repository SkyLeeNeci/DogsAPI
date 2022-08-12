package karpenko.test.dogsapi.view

import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import karpenko.test.dogsapi.R
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
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.settingsMenuItem -> {
                        findNavController().navigate(R.id.action_dogsListFragment_to_settingsFragment)
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        _binding = FragmentDogsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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