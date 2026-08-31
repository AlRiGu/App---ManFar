package com.example.data.repository

import com.example.data.local.dao.BarbershopDao
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.data.local.entity.ClientUserEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.mapper.toEntity
import com.example.data.mapper.toModel
import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.model.ClientUserModel
import com.example.shared.model.NotificationModel
import com.example.shared.model.ServiceModel
import com.example.shared.repository.IBarbershopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BarbershopRepository(private val dao: BarbershopDao) : IBarbershopRepository {

    // Services
    val allServicesEntities: Flow<List<ServiceEntity>> = dao.getAllServices()
    val activeServicesEntities: Flow<List<ServiceEntity>> = dao.getActiveServices()

    override val allServices: Flow<List<ServiceModel>> = allServicesEntities.map { it.map { s -> s.toModel() } }
    override val activeServices: Flow<List<ServiceModel>> = activeServicesEntities.map { it.map { s -> s.toModel() } }

    override suspend fun insertService(service: ServiceModel): Long = dao.insertService(service.toEntity())
    override suspend fun updateService(service: ServiceModel) = dao.updateService(service.toEntity())
    override suspend fun deleteService(service: ServiceModel) = dao.deleteService(service.toEntity())

    suspend fun insertService(service: ServiceEntity): Long = dao.insertService(service)
    suspend fun updateService(service: ServiceEntity) = dao.updateService(service)
    suspend fun deleteService(service: ServiceEntity) = dao.deleteService(service)
    suspend fun deleteServiceById(id: Long) = dao.deleteServiceById(id)

    // Appointments
    val allAppointmentsEntities: Flow<List<AppointmentEntity>> = dao.getAllAppointments()
    override val allAppointments: Flow<List<AppointmentModel>> = allAppointmentsEntities.map { it.map { a -> a.toModel() } }

    fun getAppointmentsForClient(email: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsForClient(email)
    fun getAppointmentsForDate(date: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsForDate(date)

    override suspend fun bookAppointment(appointment: AppointmentModel): Long {
        return bookAppointment(appointment.toEntity())
    }

    suspend fun bookAppointment(appointment: AppointmentEntity): Long {
        val id = dao.insertAppointment(appointment)
        dao.insertNotification(
            NotificationEntity(
                title = "Nueva Reserva Confirmada ✂️",
                message = "Cita reservada para ${appointment.serviceName} el ${appointment.date} a las ${appointment.timeSlot}.",
                appointmentId = id,
                type = "BOOKING"
            )
        )
        return id
    }

    override suspend fun updateAppointmentStatus(id: Long, status: String, clientName: String, date: String, time: String) {
        dao.updateAppointmentStatus(id, status)
        val title = when (status) {
            "CONFIRMED" -> "¡Cita Confirmada por ManFar! ✅"
            "COMPLETED" -> "¡Cita Completada! ⭐"
            "CANCELLED" -> "Cita Cancelada ❌"
            else -> "Actualización de Cita 🔔"
        }
        val message = when (status) {
            "CONFIRMED" -> "Tu cita el $date a las $time para $clientName ha sido confirmada."
            "COMPLETED" -> "Gracias por tu visita a ManFar Barbershop. ¡Esperamos verte pronto!"
            "CANCELLED" -> "La cita programada para el $date a las $time ha sido cancelada."
            else -> "Estado de tu cita: $status"
        }
        dao.insertNotification(
            NotificationEntity(
                title = title,
                message = message,
                appointmentId = id,
                type = if (status == "CONFIRMED") "CONFIRMATION" else if (status == "CANCELLED") "CANCELLATION" else "SYSTEM"
            )
        )
    }

    override suspend fun cancelAppointment(id: Long, clientName: String, date: String, time: String) {
        updateAppointmentStatus(id, "CANCELLED", clientName, date, time)
    }

    // Availability Blocks
    val allBlocksEntities: Flow<List<AvailabilityBlockEntity>> = dao.getAllBlocks()
    override val allBlocks: Flow<List<AvailabilityBlockModel>> = allBlocksEntities.map { it.map { b -> b.toModel() } }

    fun getBlocksForDate(date: String): Flow<List<AvailabilityBlockEntity>> = dao.getBlocksForDate(date)

    override suspend fun insertBlock(block: AvailabilityBlockModel): Long = dao.insertBlock(block.toEntity())
    override suspend fun deleteBlock(block: AvailabilityBlockModel) = dao.deleteBlockById(block.id)

    suspend fun insertBlock(block: AvailabilityBlockEntity): Long = dao.insertBlock(block)
    suspend fun deleteBlock(block: AvailabilityBlockEntity) = dao.deleteBlockById(block.id)
    suspend fun deleteBlockById(id: Long) = dao.deleteBlockById(id)

    // Clients / Users
    val allClientsEntities: Flow<List<ClientUserEntity>> = dao.getAllClients()
    override val allClients: Flow<List<ClientUserModel>> = allClientsEntities.map { it.map { u -> u.toModel() } }

    suspend fun getUserByEmail(email: String): ClientUserEntity? = dao.getUserByEmail(email)
    override suspend fun insertUser(user: ClientUserModel): Long = dao.insertUser(user.toEntity())
    override suspend fun updateUser(user: ClientUserModel) = dao.updateUser(user.toEntity())

    suspend fun insertUser(user: ClientUserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: ClientUserEntity) = dao.updateUser(user)

    // Notifications
    val allNotificationsEntities: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    override val allNotifications: Flow<List<NotificationModel>> = allNotificationsEntities.map { it.map { n -> n.toModel() } }

    override suspend fun insertNotification(notification: NotificationModel): Long = dao.insertNotification(notification.toEntity())
    override suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()

    suspend fun insertNotification(notification: NotificationEntity) = dao.insertNotification(notification)
    suspend fun deleteNotificationById(id: Long) = dao.deleteNotificationById(id)
}
