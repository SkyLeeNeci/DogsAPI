package karpenko.test.dogsapi.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {

    @Insert
    suspend fun insertAllDogs(vararg dogs: DogBreed): List<Long>

    @Query("select * from dogbreed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query("select * from dogbreed where uuid = :uuid")
    suspend fun getDog(uuid: Int): DogBreed

    @Query("Delete from dogbreed")
    suspend fun deleteAllDogs()
}