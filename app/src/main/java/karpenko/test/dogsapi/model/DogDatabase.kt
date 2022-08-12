package karpenko.test.dogsapi.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DogBreed::class], version = 1)
abstract class DogDatabase: RoomDatabase() {

    abstract fun dogDao(): DogDao

    companion object{
        @Volatile private var db: DogDatabase? = null
        private const val DB_NAME = "mainDB"
        private val LOCK = Any()
        fun getInstance(context: Context): DogDatabase{
            synchronized(LOCK){
                db?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    DogDatabase::class.java,
                    DB_NAME
                ).build()
                db = instance
                return instance
            }

        }

    }

}