package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val barberName: String,
    val serviceId: Long,
    val serviceName: String,
    val servicePrice: Double,
    val date: String, // "YYYY-MM-DD"
    val timeSlot: String, // "10:00"
    val status: String = "CONFIRMED", // "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
