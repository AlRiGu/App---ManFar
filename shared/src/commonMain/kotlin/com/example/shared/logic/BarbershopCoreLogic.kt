package com.example.shared.logic

import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.model.BusinessKpis
import com.example.shared.model.ClientUserModel
import com.example.shared.model.ServiceModel
import com.example.shared.model.ServiceRevenueShare
import com.example.shared.model.TimeSlotItem

object BarbershopCoreLogic {

    val STANDARD_TIME_SLOTS = listOf(
        "09:00", "09:45", "10:30", "11:15", "12:00",
        "12:45", "13:30", "15:00", "15:45", "16:30",
        "17:15", "18:00", "18:45", "19:30"
    )

    fun calculateBusinessKpis(
        appointments: List<AppointmentModel>,
        clients: List<ClientUserModel>,
        todayDateStr: String
    ): BusinessKpis {
        val completed = appointments.filter { it.status == "COMPLETED" }
        val confirmedFuture = appointments.filter { it.status == "CONFIRMED" }
        val totalRevenue = completed.sumOf { it.servicePrice }
        val completedCount = completed.size
        val avgTicket = if (completedCount > 0) totalRevenue / completedCount else 0.0
        val todayCount = appointments.count { it.date == todayDateStr && it.status != "CANCELLED" }
        val projectedRevenue = confirmedFuture.sumOf { it.servicePrice }

        return BusinessKpis(
            totalRevenue = totalRevenue,
            completedAppointmentsCount = completedCount,
            averageTicket = avgTicket,
            totalClientsCount = maxOf(clients.size, 4),
            todayAppointmentsCount = todayCount,
            projectedRevenue = projectedRevenue
        )
    }

    fun computeAvailableTimeSlots(
        selectedDate: String,
        appointments: List<AppointmentModel>,
        blocks: List<AvailabilityBlockModel>,
        baseSlots: List<String> = STANDARD_TIME_SLOTS
    ): List<TimeSlotItem> {
        val dayAppointments = appointments.filter { it.date == selectedDate && it.status != "CANCELLED" }
        val dayBlocks = blocks.filter { it.date == selectedDate }
        val isFullDayBlocked = dayBlocks.any { it.blockType == "FULL_DAY" }

        return baseSlots.map { time ->
            if (isFullDayBlocked) {
                val reason = dayBlocks.firstOrNull { it.blockType == "FULL_DAY" }?.reason ?: "Día no laborable"
                TimeSlotItem(time = time, isAvailable = false, unavailableReason = reason)
            } else {
                val isBooked = dayAppointments.any { it.timeSlot == time }
                val isTimeBlocked = dayBlocks.any { b ->
                    b.blockType == "TIME_RANGE" && time >= b.startTime && time <= b.endTime
                }

                if (isBooked) {
                    TimeSlotItem(time = time, isAvailable = false, unavailableReason = "Ocupado")
                } else if (isTimeBlocked) {
                    val reason = dayBlocks.firstOrNull {
                        it.blockType == "TIME_RANGE" && time >= it.startTime && time <= it.endTime
                    }?.reason ?: "Bloqueado"
                    TimeSlotItem(time = time, isAvailable = false, unavailableReason = reason)
                } else {
                    TimeSlotItem(time = time, isAvailable = true)
                }
            }
        }
    }

    fun computeRevenueShare(
        appointments: List<AppointmentModel>,
        services: List<ServiceModel>
    ): List<ServiceRevenueShare> {
        val validAppointments = appointments.filter { it.status == "COMPLETED" || it.status == "CONFIRMED" }
        val totalRevenue = validAppointments.sumOf { it.servicePrice }
        val colorHexList = listOf(
            0xFFF59E0BL,
            0xFFD4AF37L,
            0xFF3B82F6L,
            0xFF10B981L,
            0xFFA855F7L,
            0xFFEC4899L
        )

        val grouped = validAppointments.groupBy { it.serviceName }
        var colorIdx = 0

        return grouped.map { (serviceName, appts) ->
            val rev = appts.sumOf { it.servicePrice }
            val pct = if (totalRevenue > 0) ((rev / totalRevenue) * 100).toFloat() else 0f
            val cat = services.find { it.name == serviceName }?.category ?: "Servicio"
            val colorHex = colorHexList[colorIdx % colorHexList.size]
            colorIdx++
            ServiceRevenueShare(
                serviceName = serviceName,
                category = cat,
                revenue = rev,
                percentage = pct,
                bookingsCount = appts.size,
                colorHex = colorHex
            )
        }.sortedByDescending { it.revenue }
    }
}
