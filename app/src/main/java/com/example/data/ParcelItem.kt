package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a parcel pickup record.
 */
@Entity(tableName = "parcels")
data class ParcelItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pickupCode: String,          // e.g. "3-2-104", "849201", "A-12-8"
    val trackingNumber: String = "",  // e.g. "773123456789", "SF1234567890"
    val courierName: String = "快递包裹", // e.g. "菜鸟驿站", "丰巢快递柜", "顺丰速运"
    val location: String = "",       // e.g. "2号货架", "3号自提柜"
    val rawSms: String = "",         // Original copied SMS
    val isPickedUp: Boolean = false, // Status: true = 已取货, false = 未取货
    val createdAt: Long = System.currentTimeMillis(),
    val pickedUpAt: Long? = null
)
