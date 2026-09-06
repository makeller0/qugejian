package com.example.data

import android.content.Context
import com.example.parser.ParcelParser
import com.example.widget.ParcelWidgetUpdater
import kotlinx.coroutines.flow.Flow

class ParcelRepository(
    private val context: Context,
    private val dao: ParcelDao
) {
    val allParcels: Flow<List<ParcelItem>> = dao.getAllParcels()
    val pendingParcels: Flow<List<ParcelItem>> = dao.getPendingParcels()
    val pickedUpParcels: Flow<List<ParcelItem>> = dao.getPickedUpParcels()
    val pendingCount: Flow<Int> = dao.getPendingCount()

    suspend fun insert(item: ParcelItem): Long {
        val id = dao.insert(item)
        ParcelWidgetUpdater.updateWidgets(context)
        return id
    }

    suspend fun update(item: ParcelItem) {
        dao.update(item)
        ParcelWidgetUpdater.updateWidgets(context)
    }

    suspend fun delete(item: ParcelItem) {
        dao.delete(item)
        ParcelWidgetUpdater.updateWidgets(context)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
        ParcelWidgetUpdater.updateWidgets(context)
    }

    suspend fun markAsPickedUp(id: Long) {
        dao.updatePickupStatus(id, isPickedUp = true, pickedUpAt = System.currentTimeMillis())
        ParcelWidgetUpdater.updateWidgets(context)
    }

    suspend fun markAsPending(id: Long) {
        dao.updatePickupStatus(id, isPickedUp = false, pickedUpAt = null)
        ParcelWidgetUpdater.updateWidgets(context)
    }

    suspend fun clearAllPickedUp() {
        dao.clearAllPickedUp()
        ParcelWidgetUpdater.updateWidgets(context)
    }

    /**
     * Parses SMS text and inserts parcel if valid.
     * Checks for duplicates by pickup code + tracking number.
     * Returns pair of (isSuccess, parcelItem).
     */
    suspend fun importFromSms(smsText: String): Pair<Boolean, ParcelItem?> {
        val parsed = ParcelParser.parse(smsText)
        if (!parsed.isValid) {
            return Pair(false, null)
        }

        // Check if existing parcel exists
        val existing = dao.findExisting(parsed.pickupCode, parsed.trackingNumber)
        if (existing != null) {
            // Already stored
            return Pair(true, existing)
        }

        val item = ParcelItem(
            pickupCode = parsed.pickupCode,
            trackingNumber = parsed.trackingNumber,
            courierName = parsed.courierName,
            location = parsed.location,
            rawSms = parsed.rawText,
            isPickedUp = false,
            createdAt = System.currentTimeMillis()
        )
        val newId = dao.insert(item)
        ParcelWidgetUpdater.updateWidgets(context)
        return Pair(true, item.copy(id = newId))
    }
}
