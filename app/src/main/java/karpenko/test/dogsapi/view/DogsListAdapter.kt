package karpenko.test.dogsapi.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import karpenko.test.dogsapi.R
import karpenko.test.dogsapi.databinding.ItemDogBinding
import karpenko.test.dogsapi.model.DogBreed
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter: RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener {

    var dogsList = listOf<DogBreed>()
        set(value) {
            field = value
            notifyDataSetChanged()
           //TODO(" Replace with diff utils and list adapter")
        }
    class DogViewHolder(var view: ItemDogBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = DataBindingUtil.inflate<ItemDogBinding>(
            LayoutInflater.from(parent.context), R.layout.item_dog, parent, false)
        return DogViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dogItem = dogsList[position]
        holder.view.dog = dogItem
        holder.view.onDogClickListener = this
    }

    override fun getItemCount(): Int {
        return dogsList.size
    }

    override fun onDogClick(view: View) {
        super.onDogClick(view)
        val  uuId = view.dogId.text.toString().toInt()
        val action = DogsListFragmentDirections.actionDogsListFragmentToDogDetailFragment()
        action.uuid = uuId
        view.findNavController().navigate(action)
    }

}