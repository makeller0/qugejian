package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelDao {
    @Query("SELECT * FROM parcels ORDER BY isPickedUp ASC, createdAt DESC")
    fun getAllParcels(): Flow<List<ParcelItem>>

    @Query("SELECT * FROM parcels WHERE isPickedUp = 0 ORDER BY createdAt DESC")
    fun getPendingParcels(): Flow<List<ParcelItem>>

    @Query("SELECT * FROM parcels WHERE isPickedUp = 1 ORDER BY pickedUpAt DESC, createdAt DESC")
    fun getPickedUpParcels(): Flow<List<ParcelItem>>

    @Query("SELECT * FROM parcels WHERE isPickedUp = 0 ORDER BY createdAt DESC LIMIT 5")
    suspend fun getPendingListSync(): List<ParcelItem>

    @Query("SELECT COUNT(*) FROM parcels WHERE isPickedUp = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM parcels WHERE isPickedUp = 0")
    suspend fun getPendingCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parcel: ParcelItem): Long

    @Update
    suspend fun update(parcel: ParcelItem)

    @Delete
    suspend fun delete(parcel: ParcelItem)

    @Query("DELETE FROM parcels WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE parcels SET isPickedUp = :isPickedUp, pickedUpAt = :pickedUpAt WHERE id = :id")
    suspend fun updatePickupStatus(id: Long, isPickedUp: Boolean, pickedUpAt: Long?)

    @Query("DELETE FROM parcels WHERE isPickedUp = 1")
    suspend fun clearAllPickedUp()

    @Query("SELECT * FROM parcels WHERE pickupCode = :pickupCode AND (trackingNumber = :trackingNumber OR :trackingNumber = '') LIMIT 1")
    suspend fun findExisting(pickupCode: String, trackingNumber: String): ParcelItem?
}
