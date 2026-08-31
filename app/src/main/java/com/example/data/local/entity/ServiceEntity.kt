package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Cortes", "Barba", "Combos", "Tratamientos"
    val price: Double,
    val durationMinutes: Int,
    val description: String,
    val iconName: String = "content_cut",
    val isActive: Boolean = true
)
