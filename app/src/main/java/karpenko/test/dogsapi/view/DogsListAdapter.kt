package karpenko.test.dogsapi.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import karpenko.test.dogsapi.R
import karpenko.test.dogsapi.model.DogBreed
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(): RecyclerView.Adapter<DogsListAdapter.DogViewHolder>() {

    var dogsList = listOf<DogBreed>()
        set(value) {
            field = value
            notifyDataSetChanged()
            TODO("Replace with diff utils and list adapter")
        }

    class DogViewHolder(var view: View): RecyclerView.ViewHolder(view){
        val dogName = view.dogNameTV
        val dogLifespan = view.dogLifespanTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dogItem = dogsList[position]
        holder.dogName.text = dogItem.dogBreed
        holder.dogLifespan.text = dogItem.lifeSpan


    }

    override fun getItemCount(): Int {
        return dogsList.size
    }


}