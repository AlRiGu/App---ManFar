package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class ClientUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "CLIENT", // "CLIENT" or "ADMIN"
    val preferredBarber: String = "Cualquiera",
    val avatarInitials: String = "MB",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
