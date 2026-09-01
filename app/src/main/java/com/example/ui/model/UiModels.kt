package com.example.ui.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.GoldAmberGlow
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

data class Barber(
    val id: Int,
    val name: String,
    val title: String,
    val specialty: String,
    val rating: Double,
    val reviewsCount: Int,
    val badgeColor: Color = GoldPrimary
)

val AvailableBarbers = listOf(
    Barber(
        id = 1,
        name = "Manuel",
        title = "Master Barber & Estilista ManFar",
        specialty = "Cortes Clásicos, Degradados VIP & Diseño de Barba",
        rating = 5.0,
        reviewsCount = 240,
        badgeColor = GoldLight
    )
)

data class DayIncomePoint(
    val date: String,
    val dayLabel: String,
    val income: Double,
    val count: Int
)

data class ServiceRevenueShare(
    val serviceName: String,
    val category: String,
    val revenue: Double,
    val percentage: Float,
    val bookingsCount: Int,
    val color: Color
)

data class BusinessKpis(
    val totalRevenue: Double = 0.0,
    val completedAppointmentsCount: Int = 0,
    val averageTicket: Double = 0.0,
    val totalClientsCount: Int = 0,
    val todayAppointmentsCount: Int = 0,
    val projectedRevenue: Double = 0.0
)

data class TimeSlotItem(
    val time: String,
    val isAvailable: Boolean,
    val unavailableReason: String = ""
)
