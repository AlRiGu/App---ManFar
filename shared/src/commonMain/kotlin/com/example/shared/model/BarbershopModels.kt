package com.example.shared.model

data class Barber(
    val id: Int,
    val name: String,
    val title: String,
    val specialty: String,
    val rating: Double,
    val reviewsCount: Int,
    val badgeColorHex: Long = 0xFFD4AF37 // Gold
)

val DefaultAvailableBarbers = listOf(
    Barber(
        id = 1,
        name = "Manuel",
        title = "Master Barber & Estilista ManFar",
        specialty = "Cortes Clásicos, Degradados VIP & Diseño de Barba",
        rating = 5.0,
        reviewsCount = 240,
        badgeColorHex = 0xFFD4AF37
    )
)

val AvailableBarbers = DefaultAvailableBarbers

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
    val colorHex: Long = 0xFFD4AF37
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

data class ServiceModel(
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val durationMinutes: Int,
    val description: String,
    val iconName: String = "content_cut",
    val isActive: Boolean = true
)

data class AppointmentModel(
    val id: Long = 0,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val barberName: String,
    val serviceId: Long,
    val serviceName: String,
    val servicePrice: Double,
    val date: String,
    val timeSlot: String,
    val status: String = "CONFIRMED",
    val notes: String = "",
    val createdAt: Long = 0L
)

data class AvailabilityBlockModel(
    val id: Long = 0,
    val blockType: String = "TIME_RANGE",
    val date: String,
    val startTime: String = "",
    val endTime: String = "",
    val reason: String = "Bloqueado"
)

data class ClientUserModel(
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "CLIENT",
    val preferredBarber: String = "Cualquiera",
    val avatarInitials: String = "MB",
    val notes: String = "",
    val createdAt: Long = 0L
)

data class NotificationModel(
    val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val appointmentId: Long? = null,
    val type: String = "SYSTEM"
)

