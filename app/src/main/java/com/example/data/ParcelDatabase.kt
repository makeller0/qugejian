package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParcelItem::class], version = 1, exportSchema = false)
abstract class ParcelDatabase : RoomDatabase() {
    abstract fun parcelDao(): ParcelDao

    companion object {
        @Volatile
        private var INSTANCE: ParcelDatabase? = null

        fun getDatabase(context: Context): ParcelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParcelDatabase::class.java,
                    "parcel_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
