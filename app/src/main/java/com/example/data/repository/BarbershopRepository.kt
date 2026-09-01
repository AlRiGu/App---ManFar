package com.example.data.repository

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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BarbershopRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : IBarbershopRepository {

    private val servicesCollection = firestore.collection("services")
    private val appointmentsCollection = firestore.collection("appointments")
    private val availabilityCollection = firestore.collection("availability")
    private val clientsCollection = firestore.collection("clients")
    private val notificationsCollection = firestore.collection("notifications")

    init {
        // Seed default catalog data in Firestore if empty
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultDataIfEmpty()
        }
    }

    // ==========================================
    // 1. SERVICES (Firestore collection: services)
    // ==========================================
    override val allServices: Flow<List<ServiceModel>> = callbackFlow {
        val registration = servicesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { docToServiceModel(it) }
                    .sortedBy { it.id }
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    override val activeServices: Flow<List<ServiceModel>> = allServices.map { list ->
        list.filter { it.isActive }
    }

    val allServicesEntities: Flow<List<ServiceEntity>> = allServices.map { list ->
        list.map { it.toEntity() }
    }

    val activeServicesEntities: Flow<List<ServiceEntity>> = activeServices.map { list ->
        list.map { it.toEntity() }
    }

    override suspend fun insertService(service: ServiceModel): Long {
        val id = if (service.id > 0) service.id else System.currentTimeMillis()
        val data = mapOf(
            "id" to id,
            "name" to service.name,
            "category" to service.category,
            "price" to service.price,
            "durationMinutes" to service.durationMinutes,
            "description" to service.description,
            "iconName" to service.iconName,
            "isActive" to service.isActive
        )
        servicesCollection.document(id.toString()).set(data, SetOptions.merge()).await()
        return id
    }

    override suspend fun updateService(service: ServiceModel) {
        val id = service.id
        val data = mapOf(
            "id" to id,
            "name" to service.name,
            "category" to service.category,
            "price" to service.price,
            "durationMinutes" to service.durationMinutes,
            "description" to service.description,
            "iconName" to service.iconName,
            "isActive" to service.isActive
        )
        servicesCollection.document(id.toString()).set(data, SetOptions.merge()).await()
    }

    override suspend fun deleteService(service: ServiceModel) {
        servicesCollection.document(service.id.toString()).delete().await()
    }

    suspend fun insertService(service: ServiceEntity): Long = insertService(service.toModel())
    suspend fun updateService(service: ServiceEntity) = updateService(service.toModel())
    suspend fun deleteService(service: ServiceEntity) = deleteService(service.toModel())
    suspend fun deleteServiceById(id: Long) {
        servicesCollection.document(id.toString()).delete().await()
    }

    // ==========================================
    // 2. APPOINTMENTS (Firestore collection: appointments)
    // ==========================================
    override val allAppointments: Flow<List<AppointmentModel>> = callbackFlow {
        val registration = appointmentsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { docToAppointmentModel(it) }
                        .sortedWith(compareBy<AppointmentModel> { it.date }.thenBy { it.timeSlot })
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }

    val allAppointmentsEntities: Flow<List<AppointmentEntity>> = allAppointments.map { list ->
        list.map { it.toEntity() }
    }

    fun getAppointmentsForClient(email: String): Flow<List<AppointmentEntity>> = allAppointmentsEntities.map { list ->
        list.filter { it.clientEmail.equals(email, ignoreCase = true) }
    }

    fun getAppointmentsForDate(date: String): Flow<List<AppointmentEntity>> = allAppointmentsEntities.map { list ->
        list.filter { it.date == date }
    }

    override suspend fun bookAppointment(appointment: AppointmentModel): Long {
        val id = if (appointment.id > 0) appointment.id else System.currentTimeMillis()
        val createdAt = if (appointment.createdAt > 0) appointment.createdAt else System.currentTimeMillis()
        val data = mapOf(
            "id" to id,
            "clientName" to appointment.clientName,
            "clientEmail" to appointment.clientEmail,
            "clientPhone" to appointment.clientPhone,
            "barberName" to appointment.barberName,
            "serviceId" to appointment.serviceId,
            "serviceName" to appointment.serviceName,
            "servicePrice" to appointment.servicePrice,
            "date" to appointment.date,
            "timeSlot" to appointment.timeSlot,
            "status" to appointment.status,
            "notes" to appointment.notes,
            "createdAt" to createdAt
        )
        appointmentsCollection.document(id.toString()).set(data, SetOptions.merge()).await()

        // Also register client if not existing
        insertUser(
            ClientUserModel(
                name = appointment.clientName,
                email = appointment.clientEmail,
                phone = appointment.clientPhone,
                role = "CLIENT"
            )
        )

        // Add real-time notification
        insertNotification(
            NotificationModel(
                title = "Nueva Reserva Confirmada ✂️",
                message = "Cita reservada para ${appointment.serviceName} el ${appointment.date} a las ${appointment.timeSlot}.",
                appointmentId = id,
                type = "BOOKING"
            )
        )
        return id
    }

    suspend fun bookAppointment(appointment: AppointmentEntity): Long = bookAppointment(appointment.toModel())

    override suspend fun updateAppointmentStatus(id: Long, status: String, clientName: String, date: String, time: String) {
        appointmentsCollection.document(id.toString()).update("status", status).await()

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
        insertNotification(
            NotificationModel(
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

    // ==========================================
    // 3. AVAILABILITY BLOCKS (Firestore collection: availability)
    // ==========================================
    override val allBlocks: Flow<List<AvailabilityBlockModel>> = callbackFlow {
        val registration = availabilityCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { docToAvailabilityBlockModel(it) }
                    .sortedBy { it.date }
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    val allBlocksEntities: Flow<List<AvailabilityBlockEntity>> = allBlocks.map { list ->
        list.map { it.toEntity() }
    }

    fun getBlocksForDate(date: String): Flow<List<AvailabilityBlockEntity>> = allBlocksEntities.map { list ->
        list.filter { it.date == date }
    }

    override suspend fun insertBlock(block: AvailabilityBlockModel): Long {
        val id = if (block.id > 0) block.id else System.currentTimeMillis()
        val data = mapOf(
            "id" to id,
            "blockType" to block.blockType,
            "date" to block.date,
            "startTime" to block.startTime,
            "endTime" to block.endTime,
            "reason" to block.reason
        )
        availabilityCollection.document(id.toString()).set(data, SetOptions.merge()).await()
        return id
    }

    override suspend fun deleteBlock(block: AvailabilityBlockModel) {
        availabilityCollection.document(block.id.toString()).delete().await()
    }

    suspend fun insertBlock(block: AvailabilityBlockEntity): Long = insertBlock(block.toModel())
    suspend fun deleteBlock(block: AvailabilityBlockEntity) = deleteBlock(block.toModel())
    suspend fun deleteBlockById(id: Long) {
        availabilityCollection.document(id.toString()).delete().await()
    }

    // ==========================================
    // 4. CLIENTS / USERS (Firestore collection: clients)
    // ==========================================
    override val allClients: Flow<List<ClientUserModel>> = callbackFlow {
        val registration = clientsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { docToClientUserModel(it) }
                    .sortedBy { it.name }
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    val allClientsEntities: Flow<List<ClientUserEntity>> = allClients.map { list ->
        list.map { it.toEntity() }
    }

    suspend fun getUserByEmail(email: String): ClientUserEntity? {
        val docId = emailToDocId(email)
        val snapshot = clientsCollection.document(docId).get().await()
        return if (snapshot.exists()) {
            docToClientUserModel(snapshot)?.toEntity()
        } else {
            null
        }
    }

    override suspend fun insertUser(user: ClientUserModel): Long {
        val id = if (user.id > 0) user.id else System.currentTimeMillis()
        val docId = emailToDocId(user.email)
        val data = mapOf(
            "id" to id,
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "role" to user.role,
            "preferredBarber" to user.preferredBarber,
            "avatarInitials" to (user.avatarInitials.ifBlank { getInitials(user.name) }),
            "notes" to user.notes,
            "createdAt" to (if (user.createdAt > 0) user.createdAt else System.currentTimeMillis())
        )
        clientsCollection.document(docId).set(data, SetOptions.merge()).await()
        return id
    }

    override suspend fun updateUser(user: ClientUserModel) {
        insertUser(user)
    }

    suspend fun insertUser(user: ClientUserEntity): Long = insertUser(user.toModel())
    suspend fun updateUser(user: ClientUserEntity) = updateUser(user.toModel())

    // ==========================================
    // 5. NOTIFICATIONS (Firestore collection: notifications)
    // ==========================================
    override val allNotifications: Flow<List<NotificationModel>> = callbackFlow {
        val registration = notificationsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { docToNotificationModel(it) }
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }

    val allNotificationsEntities: Flow<List<NotificationEntity>> = allNotifications.map { list ->
        list.map { it.toEntity() }
    }

    override suspend fun insertNotification(notification: NotificationModel): Long {
        val id = if (notification.id > 0) notification.id else System.currentTimeMillis()
        val timestamp = if (notification.timestamp > 0) notification.timestamp else System.currentTimeMillis()
        val data = mutableMapOf<String, Any>(
            "id" to id,
            "title" to notification.title,
            "message" to notification.message,
            "timestamp" to timestamp,
            "isRead" to notification.isRead,
            "type" to notification.type
        )
        notification.appointmentId?.let { data["appointmentId"] = it }
        notificationsCollection.document(id.toString()).set(data, SetOptions.merge()).await()
        return id
    }

    override suspend fun markAllNotificationsAsRead() {
        val snapshot = notificationsCollection.whereEqualTo("isRead", false).get().await()
        for (doc in snapshot.documents) {
            doc.reference.update("isRead", true)
        }
    }

    suspend fun insertNotification(notification: NotificationEntity) = insertNotification(notification.toModel())
    suspend fun deleteNotificationById(id: Long) {
        notificationsCollection.document(id.toString()).delete().await()
    }

    // ==========================================
    // DOCUMENT PARSERS & HELPERS
    // ==========================================
    private fun docToServiceModel(doc: DocumentSnapshot): ServiceModel? {
        val name = doc.getString("name") ?: return null
        val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
        val category = doc.getString("category") ?: "Cabello"
        val price = doc.getDouble("price") ?: (doc.getLong("price")?.toDouble() ?: 0.0)
        val durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 30
        val description = doc.getString("description") ?: ""
        val iconName = doc.getString("iconName") ?: "content_cut"
        val isActive = doc.getBoolean("isActive") ?: true

        return ServiceModel(
            id = id,
            name = name,
            category = category,
            price = price,
            durationMinutes = durationMinutes,
            description = description,
            iconName = iconName,
            isActive = isActive
        )
    }

    private fun docToAppointmentModel(doc: DocumentSnapshot): AppointmentModel? {
        val clientName = doc.getString("clientName") ?: return null
        val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
        val clientEmail = doc.getString("clientEmail") ?: ""
        val clientPhone = doc.getString("clientPhone") ?: ""
        val barberName = doc.getString("barberName") ?: "Manuel"
        val serviceId = doc.getLong("serviceId") ?: 1L
        val serviceName = doc.getString("serviceName") ?: "Servicio"
        val servicePrice = doc.getDouble("servicePrice") ?: (doc.getLong("servicePrice")?.toDouble() ?: 0.0)
        val date = doc.getString("date") ?: ""
        val timeSlot = doc.getString("timeSlot") ?: ""
        val status = doc.getString("status") ?: "CONFIRMED"
        val notes = doc.getString("notes") ?: ""
        val createdAt = doc.getLong("createdAt") ?: 0L

        return AppointmentModel(
            id = id,
            clientName = clientName,
            clientEmail = clientEmail,
            clientPhone = clientPhone,
            barberName = barberName,
            serviceId = serviceId,
            serviceName = serviceName,
            servicePrice = servicePrice,
            date = date,
            timeSlot = timeSlot,
            status = status,
            notes = notes,
            createdAt = createdAt
        )
    }

    private fun docToAvailabilityBlockModel(doc: DocumentSnapshot): AvailabilityBlockModel? {
        val date = doc.getString("date") ?: return null
        val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
        val blockType = doc.getString("blockType") ?: "TIME_RANGE"
        val startTime = doc.getString("startTime") ?: ""
        val endTime = doc.getString("endTime") ?: ""
        val reason = doc.getString("reason") ?: "Bloqueado"

        return AvailabilityBlockModel(
            id = id,
            blockType = blockType,
            date = date,
            startTime = startTime,
            endTime = endTime,
            reason = reason
        )
    }

    private fun docToClientUserModel(doc: DocumentSnapshot): ClientUserModel? {
        val name = doc.getString("name") ?: return null
        val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
        val email = doc.getString("email") ?: doc.id
        val phone = doc.getString("phone") ?: ""
        val role = doc.getString("role") ?: "CLIENT"
        val preferredBarber = doc.getString("preferredBarber") ?: "Cualquiera"
        val avatarInitials = doc.getString("avatarInitials") ?: getInitials(name)
        val notes = doc.getString("notes") ?: ""
        val createdAt = doc.getLong("createdAt") ?: 0L

        return ClientUserModel(
            id = id,
            name = name,
            email = email,
            phone = phone,
            role = role,
            preferredBarber = preferredBarber,
            avatarInitials = avatarInitials,
            notes = notes,
            createdAt = createdAt
        )
    }

    private fun docToNotificationModel(doc: DocumentSnapshot): NotificationModel? {
        val title = doc.getString("title") ?: return null
        val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
        val message = doc.getString("message") ?: ""
        val timestamp = doc.getLong("timestamp") ?: 0L
        val isRead = doc.getBoolean("isRead") ?: false
        val appointmentId = doc.getLong("appointmentId")
        val type = doc.getString("type") ?: "SYSTEM"

        return NotificationModel(
            id = id,
            title = title,
            message = message,
            timestamp = timestamp,
            isRead = isRead,
            appointmentId = appointmentId,
            type = type
        )
    }

    private fun emailToDocId(email: String): String {
        return email.trim().lowercase().replace(".", "_").replace("@", "_at_")
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "MF"
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        try {
            val servicesSnapshot = servicesCollection.limit(1).get().await()
            if (servicesSnapshot.isEmpty) {
                val defaultServices = listOf(
                    ServiceModel(
                        id = 1,
                        name = "Corte Clásico ManFar",
                        category = "Cabello",
                        price = 18.0,
                        durationMinutes = 30,
                        description = "Corte a tijera o máquina tradicional con lavado aromático y peinado final.",
                        iconName = "content_cut",
                        isActive = true
                    ),
                    ServiceModel(
                        id = 2,
                        name = "Degradado VIP / Skin Fade",
                        category = "Cabello",
                        price = 22.0,
                        durationMinutes = 45,
                        description = "Fade milimétrico rasurado a navaja con sombreado perfecto y fijación premium.",
                        iconName = "content_cut",
                        isActive = true
                    ),
                    ServiceModel(
                        id = 3,
                        name = "Arreglo de Barba & Perfilado",
                        category = "Barba",
                        price = 14.0,
                        durationMinutes = 25,
                        description = "Recorte con degradado, perfilado a navaja y tratamiento de aceites esenciales.",
                        iconName = "face",
                        isActive = true
                    ),
                    ServiceModel(
                        id = 4,
                        name = "Combo Total: Corte + Barba + Ritual",
                        category = "Combos",
                        price = 32.0,
                        durationMinutes = 60,
                        description = "La experiencia completa de ManFar: corte estilizado, toalla caliente y barba VIP.",
                        iconName = "star",
                        isActive = true
                    ),
                    ServiceModel(
                        id = 5,
                        name = "Tratamiento Capilar & Masaje",
                        category = "Cuidado",
                        price = 16.0,
                        durationMinutes = 20,
                        description = "Exfoliación de cuero cabelludo, mascarilla fortalecedora y masaje relajante.",
                        iconName = "spa",
                        isActive = true
                    ),
                    ServiceModel(
                        id = 6,
                        name = "Diseño de Cejas & Navaja",
                        category = "Detalle",
                        price = 8.0,
                        durationMinutes = 15,
                        description = "Limpieza geométrica y definición de cejas con navaja barbera.",
                        iconName = "remove_red_eye",
                        isActive = true
                    )
                )

                for (service in defaultServices) {
                    insertService(service)
                }
            }

            val clientsSnapshot = clientsCollection.limit(1).get().await()
            if (clientsSnapshot.isEmpty) {
                insertUser(
                    ClientUserModel(
                        id = 1,
                        name = "Carlos Mendoza",
                        email = "carlos.mendoza@gmail.com",
                        phone = "+34 612 345 678",
                        role = "CLIENT",
                        preferredBarber = "Manuel",
                        avatarInitials = "CM"
                    )
                )
                insertUser(
                    ClientUserModel(
                        id = 99,
                        name = "Admin ManFar",
                        email = "admin@manfarbarbershop.com",
                        phone = "+34 910 000 111",
                        role = "ADMIN",
                        preferredBarber = "Todos",
                        avatarInitials = "MF"
                    )
                )
            }
        } catch (_: Exception) {
            // Seeding failsafe
        }
    }
}
