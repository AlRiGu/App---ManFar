package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "availability_blocks")
data class AvailabilityBlockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val blockType: String, // "FULL_DAY" or "TIME_RANGE"
    val date: String, // "YYYY-MM-DD"
    val startTime: String = "", // e.g. "14:00"
    val endTime: String = "", // e.g. "16:00"
    val reason: String = "No disponible"
)
