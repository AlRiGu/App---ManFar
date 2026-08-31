package com.example.shared.repository

import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.model.ClientUserModel
import com.example.shared.model.NotificationModel
import com.example.shared.model.ServiceModel
import kotlinx.coroutines.flow.Flow

interface IBarbershopRepository {
    val allServices: Flow<List<ServiceModel>>
    val activeServices: Flow<List<ServiceModel>>
    val allAppointments: Flow<List<AppointmentModel>>
    val allBlocks: Flow<List<AvailabilityBlockModel>>
    val allClients: Flow<List<ClientUserModel>>
    val allNotifications: Flow<List<NotificationModel>>

    suspend fun insertService(service: ServiceModel): Long
    suspend fun updateService(service: ServiceModel)
    suspend fun deleteService(service: ServiceModel)

    suspend fun bookAppointment(appointment: AppointmentModel): Long
    suspend fun updateAppointmentStatus(id: Long, status: String, clientName: String, date: String, time: String)
    suspend fun cancelAppointment(id: Long, clientName: String, date: String, time: String)

    suspend fun insertBlock(block: AvailabilityBlockModel): Long
    suspend fun deleteBlock(block: AvailabilityBlockModel)

    suspend fun insertUser(user: ClientUserModel): Long
    suspend fun updateUser(user: ClientUserModel)

    suspend fun insertNotification(notification: NotificationModel): Long
    suspend fun markAllNotificationsAsRead()
}
