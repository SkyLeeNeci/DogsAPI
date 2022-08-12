package karpenko.test.dogsapi.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import karpenko.test.dogsapi.model.DogBreed
import karpenko.test.dogsapi.model.DogDatabase
import kotlinx.coroutines.launch
import java.util.*

class DogItemViewModel(application: Application ) : BaseViewModel(application) {

    private val _dogInfoLV = MutableLiveData<DogBreed>()
    val dogInfoLV: LiveData<DogBreed>
        get() = _dogInfoLV

    fun fetch(uuid: Int) {
        launch {
            val dog = DogDatabase.getInstance(getApplication()).dogDao().getDog(uuid)
            _dogInfoLV.value = dog
        }
    }

}