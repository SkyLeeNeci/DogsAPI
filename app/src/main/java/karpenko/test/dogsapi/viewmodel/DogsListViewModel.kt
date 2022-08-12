package karpenko.test.dogsapi.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import karpenko.test.dogsapi.model.DogBreed
import karpenko.test.dogsapi.model.DogDatabase
import karpenko.test.dogsapi.model.DogsApiService
import karpenko.test.dogsapi.util.NotificationHelper
import karpenko.test.dogsapi.util.SharedPreferencesHelper
import kotlinx.coroutines.launch

class DogsListViewModel(application: Application): BaseViewModel(application) {

    private val dogsApiService = DogsApiService()
    private val disposable = CompositeDisposable()

    private val sharedPrefHelper = SharedPreferencesHelper(getApplication())


    private val _listOfDogs = MutableLiveData<List<DogBreed>>()
    val listOfDogs: LiveData<List<DogBreed>>
        get() = _listOfDogs

    private val _dogsLoadError = MutableLiveData<Boolean>()
    val dogsLoadError: LiveData<Boolean>
    get() = _dogsLoadError

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading


    fun refresh(){
        val updateTime = sharedPrefHelper.getLastUpdateTime()
        if (updateTime != 0L && updateTime != null && System.nanoTime()-updateTime < REFRESH_TIME){
            fetchFromDatabase()
        }else fetchFromRemote()

    }

    private fun fetchFromRemote(){
        _loading.value = true
        disposable.add(dogsApiService.apiService.getDogs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>(){
                override fun onSuccess(dogList: List<DogBreed>) {
                    storeDogLocally(dogList)
                    Toast.makeText(getApplication(), "fetchFromRemote", Toast.LENGTH_SHORT).show()
                    NotificationHelper(getApplication()).createNotification()
                }

                override fun onError(e: Throwable) {
                    _dogsLoadError.value = true
                    _loading.value = false
                    Log.d("DogsListViewModel Fetch data error", "${e.printStackTrace()}")
                }

            }))
    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromDatabase(){
        _loading.value = true
        launch {
            val dogs = DogDatabase.getInstance(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "fetchFromDatabase", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dogsRetrieved(dogList: List<DogBreed>){
        _listOfDogs.value = dogList
        _dogsLoadError.value = false
        _loading.value = false
    }

    private fun storeDogLocally(list: List<DogBreed>){
        launch {
            val dao = DogDatabase.getInstance(getApplication()).dogDao()
            dao.deleteAllDogs()
            val insertRes = dao.insertAllDogs(*list.toTypedArray())
            var i = 0
            while (i< list.size){
                list[i].uuid = insertRes[i].toInt()
                ++i
            }
            dogsRetrieved(list)
        }
        sharedPrefHelper.updateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    companion object{
        private const val REFRESH_TIME = 10*1000*1000*1000L
    }

}