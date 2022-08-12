package karpenko.test.dogsapi.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.http.GET

interface DogsApi{

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs(): Single<List<DogBreed>>

}