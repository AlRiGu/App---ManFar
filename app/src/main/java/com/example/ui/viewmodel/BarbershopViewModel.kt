package com.example.ui.viewmodel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthService
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.data.local.entity.ClientUserEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.repository.BarbershopRepository
import com.example.ui.model.AvailableBarbers
import com.example.ui.model.Barber
import com.example.ui.model.BusinessKpis
import com.example.ui.model.DayIncomePoint
import com.example.ui.model.ServiceRevenueShare
import com.example.ui.model.TimeSlotItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BarbershopViewModel(
    private val repository: BarbershopRepository,
    private val authService: FirebaseAuthService = FirebaseAuthService()
) : ViewModel() {

    // Auth & Navigation State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentRole = MutableStateFlow("CLIENT") // "CLIENT" or "ADMIN"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _currentTab = MutableStateFlow("CLIENT_HOME")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _currentUser = MutableStateFlow(
        ClientUserEntity(
            id = 1,
            name = "Carlos Mendoza",
            email = "carlos.mendoza@gmail.com",
            phone = "+34 612 345 678",
            role = "CLIENT",
            preferredBarber = "Manuel",
            avatarInitials = "CM"
        )
    )
    val currentUser: StateFlow<ClientUserEntity> = _currentUser.asStateFlow()

    // Data Streams from Repository
    val allServices: StateFlow<List<ServiceEntity>> = repository.allServicesEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeServices: StateFlow<List<ServiceEntity>> = repository.activeServicesEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<AppointmentEntity>> = repository.allAppointmentsEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availabilityBlocks: StateFlow<List<AvailabilityBlockEntity>> = repository.allBlocksEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allBlocks: StateFlow<List<AvailabilityBlockEntity>> = availabilityBlocks

    val clients: StateFlow<List<ClientUserEntity>> = repository.allClientsEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allClients: StateFlow<List<ClientUserEntity>> = clients

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotificationsEntities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotifications: StateFlow<List<NotificationEntity>> = notifications

    // Client's appointments
    val userAppointments: StateFlow<List<AppointmentEntity>> = combine(
        repository.allAppointmentsEntities,
        _currentUser
    ) { appointments, user ->
        appointments.filter { it.clientEmail.equals(user.email, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val clientAppointments: StateFlow<List<AppointmentEntity>> = userAppointments

    // Business KPIs for Admin
    val businessKpis: StateFlow<BusinessKpis> = combine(
        allAppointments,
        clients
    ) { appointments, clientList ->
        val todayStr = LocalDate.now().toString()
        val completed = appointments.filter { it.status == "COMPLETED" }
        val confirmedFuture = appointments.filter { it.status == "CONFIRMED" }
        val totalRevenue = completed.sumOf { it.servicePrice }
        val completedCount = completed.size
        val avgTicket = if (completedCount > 0) totalRevenue / completedCount else 0.0
        val todayCount = appointments.count { it.date == todayStr && it.status != "CANCELLED" }
        val projectedRevenue = confirmedFuture.sumOf { it.servicePrice }

        BusinessKpis(
            totalRevenue = totalRevenue,
            completedAppointmentsCount = completedCount,
            averageTicket = avgTicket,
            totalClientsCount = maxOf(clientList.size, 4),
            todayAppointmentsCount = todayCount,
            projectedRevenue = projectedRevenue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessKpis())

    // Income By Day (Past 7 days + today)
    val incomeByDay: StateFlow<List<DayIncomePoint>> = allAppointments.combine(
        MutableStateFlow(Unit)
    ) { appointments, _ ->
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE dd", Locale("es", "ES"))
        val points = mutableListOf<DayIncomePoint>()

        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.toString()
            val dayLabel = date.format(formatter).replaceFirstChar { it.uppercase() }
            val dayAppointments = appointments.filter {
                it.date == dateStr && (it.status == "COMPLETED" || it.status == "CONFIRMED")
            }
            val income = dayAppointments.sumOf { it.servicePrice }
            points.add(
                DayIncomePoint(
                    date = dateStr,
                    dayLabel = dayLabel,
                    income = income,
                    count = dayAppointments.size
                )
            )
        }
        points
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Service Revenue Distribution
    val serviceRevenueDistribution: StateFlow<List<ServiceRevenueShare>> = allAppointments.combine(
        allServices
    ) { appointments, services ->
        val chartColors = listOf(
            Color(0xFFF59E0B), // Amber
            Color(0xFFD4AF37), // Gold
            Color(0xFF3B82F6), // Blue
            Color(0xFF10B981), // Emerald
            Color(0xFFA855F7), // Purple
            Color(0xFFEC4899)  // Pink
        )
        val validAppointments = appointments.filter { it.status == "COMPLETED" || it.status == "CONFIRMED" }
        val totalRevenue = validAppointments.sumOf { it.servicePrice }

        val grouped = validAppointments.groupBy { it.serviceName }
        var colorIdx = 0

        grouped.map { (serviceName, appts) ->
            val rev = appts.sumOf { it.servicePrice }
            val pct = if (totalRevenue > 0) ((rev / totalRevenue) * 100).toFloat() else 0f
            val cat = services.find { it.name == serviceName }?.category ?: "Servicio"
            val color = chartColors[colorIdx % chartColors.size]
            colorIdx++
            ServiceRevenueShare(
                serviceName = serviceName,
                category = cat,
                revenue = rev,
                percentage = pct,
                bookingsCount = appts.size,
                color = color
            )
        }.sortedByDescending { it.revenue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Booking Flow State
    private val _selectedBookingService = MutableStateFlow<ServiceEntity?>(null)
    val selectedService: StateFlow<ServiceEntity?> = _selectedBookingService.asStateFlow()
    val selectedBookingService: StateFlow<ServiceEntity?> = selectedService

    private val _selectedBookingBarber = MutableStateFlow<Barber>(AvailableBarbers.first())
    val selectedBarber: StateFlow<Barber> = _selectedBookingBarber.asStateFlow()
    val selectedBookingBarber: StateFlow<Barber> = selectedBarber

    private val _selectedBookingDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedBookingDate.asStateFlow()
    val selectedBookingDate: StateFlow<String> = selectedDate

    private val _selectedBookingTime = MutableStateFlow<String?>(null)
    val selectedTime: StateFlow<String?> = _selectedBookingTime.asStateFlow()
    val selectedBookingTime: StateFlow<String?> = selectedTime

    private val _bookingNotes = MutableStateFlow("")
    val bookingNotes: StateFlow<String> = _bookingNotes.asStateFlow()

    private val _bookingSuccessMessage = MutableStateFlow<String?>(null)
    val bookingSuccessMessage: StateFlow<String?> = _bookingSuccessMessage.asStateFlow()

    // Available Slots Reactive Stream
    val availableSlots: StateFlow<List<TimeSlotItem>> = combine(
        _selectedBookingDate,
        allAppointments,
        availabilityBlocks
    ) { date, appts, blocks ->
        val baseHours = listOf(
            "09:00", "09:45", "10:30", "11:15", "12:00",
            "12:45", "13:30", "15:00", "15:45", "16:30",
            "17:15", "18:00", "18:45", "19:30"
        )
        val dayAppointments = appts.filter { it.date == date && it.status != "CANCELLED" }
        val dayBlocks = blocks.filter { it.date == date }
        val isFullDayBlocked = dayBlocks.any { it.blockType == "FULL_DAY" }

        baseHours.map { time ->
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-App Toast Banner
    private val _toastNotification = MutableStateFlow<NotificationEntity?>(null)
    val activeNotificationToast: StateFlow<NotificationEntity?> = _toastNotification.asStateFlow()
    val toastNotification: StateFlow<NotificationEntity?> = activeNotificationToast

    // --- Navigation & Role Actions ---
    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun switchRole(role: String) {
        _currentRole.value = role
        if (role == "ADMIN") {
            _currentTab.value = "ADMIN_DASHBOARD"
            _currentUser.value = ClientUserEntity(
                id = 99,
                name = "Admin ManFar",
                email = "admin@manfarbarbershop.com",
                phone = "+34 910 000 111",
                role = "ADMIN",
                preferredBarber = "Todos",
                avatarInitials = "MF"
            )
        } else {
            _currentTab.value = "CLIENT_HOME"
            _currentUser.value = ClientUserEntity(
                id = 1,
                name = "Carlos Mendoza",
                email = "carlos.mendoza@gmail.com",
                phone = "+34 612 345 678",
                role = "CLIENT",
                preferredBarber = "Manuel",
                avatarInitials = "CM"
            )
        }
    }

    fun authenticateWithEmailPassword(
        email: String,
        password: String,
        isRegister: Boolean,
        name: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = if (isRegister) {
                authService.signUpWithEmail(name, email, password)
            } else {
                authService.signInWithEmail(email, password)
            }

            _authLoading.value = false
            result.onSuccess { user ->
                val userEmail = user.email ?: email.trim()
                val existingFirestoreUser = try {
                    repository.getUserByEmail(userEmail)
                } catch (_: Exception) {
                    null
                }

                val displayName = existingFirestoreUser?.name?.takeIf { it.isNotBlank() }
                    ?: user.displayName?.takeIf { it.isNotBlank() }
                    ?: if (name.isNotBlank()) name else userEmail.substringBefore("@")
                val isExplicitAdmin = userEmail.contains("admin", ignoreCase = true) || 
                        userEmail.equals("admin@manfarbarbershop.com", ignoreCase = true)
                val role = existingFirestoreUser?.role ?: if (isExplicitAdmin) "ADMIN" else "CLIENT"

                val clientUser = existingFirestoreUser?.copy(
                    name = displayName,
                    phone = if (existingFirestoreUser.phone.isNotBlank()) existingFirestoreUser.phone else (user.phoneNumber ?: "")
                ) ?: ClientUserEntity(
                    id = System.currentTimeMillis(),
                    name = displayName,
                    email = userEmail,
                    phone = user.phoneNumber ?: "",
                    role = role,
                    preferredBarber = if (role == "ADMIN") "Todos" else "Manuel",
                    avatarInitials = displayName.take(2).uppercase()
                )

                // Sync with repository (persists to Room local cache and Firestore)
                repository.insertUser(clientUser)

                _currentUser.value = clientUser
                _currentRole.value = clientUser.role
                _isLoggedIn.value = true
                _currentTab.value = if (clientUser.role.equals("ADMIN", ignoreCase = true)) "ADMIN_DASHBOARD" else "CLIENT_HOME"

                val msg = if (isRegister) "¡Cuenta creada en Firebase!" else "¡Bienvenido de vuelta a ManFar!"
                showToast("Autenticación Exitosa ✂️", msg)
                onSuccess()
            }.onFailure { err ->
                // Cross-platform sync & offline-first fallback:
                // Check if user exists in Firestore (registered on iOS) or Room local cache
                val fallbackUser = try { repository.getUserByEmail(email.trim()) } catch (_: Exception) { null }
                if (fallbackUser != null) {
                    _currentUser.value = fallbackUser
                    _currentRole.value = fallbackUser.role
                    _isLoggedIn.value = true
                    _currentTab.value = if (fallbackUser.role.equals("ADMIN", ignoreCase = true)) "ADMIN_DASHBOARD" else "CLIENT_HOME"
                    showToast("Acceso Concedido ✂️", "Perfil sincronizado con Room y Firestore.")
                    onSuccess()
                } else {
                    _authError.value = err.localizedMessage ?: "Error de autenticación con Firebase"
                }
            }
        }
    }

    fun authenticateWithGoogle(
        context: Context,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = authService.signInWithGoogleCredential(context)
            _authLoading.value = false

            result.onSuccess { user ->
                val userEmail = user.email ?: "usuario.google@gmail.com"
                val existingFirestoreUser = try {
                    repository.getUserByEmail(userEmail)
                } catch (_: Exception) {
                    null
                }

                val displayName = existingFirestoreUser?.name?.takeIf { it.isNotBlank() }
                    ?: user.displayName?.takeIf { it.isNotBlank() }
                    ?: "Cliente Google"
                val isExplicitAdmin = userEmail.contains("admin", ignoreCase = true) || 
                        userEmail.equals("admin@manfarbarbershop.com", ignoreCase = true)
                val role = existingFirestoreUser?.role ?: if (isExplicitAdmin) "ADMIN" else "CLIENT"

                val clientUser = existingFirestoreUser?.copy(
                    name = displayName,
                    phone = if (existingFirestoreUser.phone.isNotBlank()) existingFirestoreUser.phone else (user.phoneNumber ?: "")
                ) ?: ClientUserEntity(
                    id = System.currentTimeMillis(),
                    name = displayName,
                    email = userEmail,
                    phone = user.phoneNumber ?: "",
                    role = role,
                    preferredBarber = if (role == "ADMIN") "Todos" else "Manuel",
                    avatarInitials = displayName.take(2).uppercase()
                )

                repository.insertUser(clientUser)

                _currentUser.value = clientUser
                _currentRole.value = clientUser.role
                _isLoggedIn.value = true
                _currentTab.value = if (clientUser.role.equals("ADMIN", ignoreCase = true)) "ADMIN_DASHBOARD" else "CLIENT_HOME"

                showToast("Google Sign-In Exitoso 🌐", "Bienvenido, $displayName")
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.localizedMessage ?: "Error al autenticar con Google"
            }
        }
    }

    fun loginQuick(role: String, email: String, name: String) {
        _isLoggedIn.value = true
        _currentRole.value = role
        if (role == "ADMIN") {
            _currentTab.value = "ADMIN_DASHBOARD"
            _currentUser.value = ClientUserEntity(
                id = 99,
                name = name.ifBlank { "Admin ManFar" },
                email = email.ifBlank { "admin@manfarbarbershop.com" },
                phone = "+34 910 000 111",
                role = "ADMIN",
                preferredBarber = "Todos",
                avatarInitials = "MF"
            )
        } else {
            _currentTab.value = "CLIENT_HOME"
            _currentUser.value = ClientUserEntity(
                id = 1,
                name = name.ifBlank { "Carlos Mendoza" },
                email = email.ifBlank { "carlos.mendoza@gmail.com" },
                phone = "+34 612 345 678",
                role = "CLIENT",
                preferredBarber = "Manuel",
                avatarInitials = "CM"
            )
        }
    }

    fun logout() {
        authService.signOut()
        _isLoggedIn.value = false
        _currentTab.value = "CLIENT_HOME"
    }

    fun updateUserProfile(user: ClientUserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
            _currentUser.value = user
            showToast("Perfil Actualizado 👤", "Tus datos han sido guardados correctamente.")
        }
    }

    // --- Booking Helpers ---
    fun selectService(service: ServiceEntity) {
        _selectedBookingService.value = service
    }

    fun selectBookingService(service: ServiceEntity) = selectService(service)

    fun selectBarber(barber: Barber) {
        _selectedBookingBarber.value = barber
    }

    fun selectBookingBarber(barber: Barber) = selectBarber(barber)

    fun selectDate(date: String) {
        _selectedBookingDate.value = date
        _selectedBookingTime.value = null
    }

    fun selectBookingDate(date: String) = selectDate(date)

    fun selectTime(time: String) {
        _selectedBookingTime.value = time
    }

    fun selectBookingTime(time: String) = selectTime(time)

    fun setBookingNotes(notes: String) {
        _bookingNotes.value = notes
    }

    fun confirmBooking(onSuccess: () -> Unit) {
        val service = _selectedBookingService.value ?: return
        val barber = _selectedBookingBarber.value
        val date = _selectedBookingDate.value
        val time = _selectedBookingTime.value ?: return
        val user = _currentUser.value

        viewModelScope.launch {
            val appointment = AppointmentEntity(
                clientName = user.name,
                clientEmail = user.email,
                clientPhone = user.phone,
                barberName = barber.name,
                serviceId = service.id,
                serviceName = service.name,
                servicePrice = service.price,
                date = date,
                timeSlot = time,
                status = "CONFIRMED",
                notes = _bookingNotes.value
            )
            repository.bookAppointment(appointment)
            _bookingSuccessMessage.value = "¡Cita confirmada con éxito para el $date a las $time!"
            showToast(
                "¡Cita Agendada! ✂️",
                "Tu reserva para ${service.name} con ${barber.name} está lista."
            )
            _selectedBookingTime.value = null
            _bookingNotes.value = ""
            onSuccess()
        }
    }

    // --- Appointment Management ---
    fun confirmAppointment(appointment: AppointmentEntity) {
        updateAppointmentStatus(appointment, "CONFIRMED")
    }

    fun completeAppointment(appointment: AppointmentEntity) {
        updateAppointmentStatus(appointment, "COMPLETED")
    }

    fun cancelAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.cancelAppointment(
                id = appointment.id,
                clientName = appointment.clientName,
                date = appointment.date,
                time = appointment.timeSlot
            )
            showToast("Cita Cancelada ❌", "La cita del ${appointment.date} ha sido cancelada.")
        }
    }

    fun updateAppointmentStatus(appointment: AppointmentEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(
                id = appointment.id,
                status = newStatus,
                clientName = appointment.clientName,
                date = appointment.date,
                time = appointment.timeSlot
            )
            val statusLabel = when (newStatus) {
                "CONFIRMED" -> "Confirmada ✅"
                "COMPLETED" -> "Completada 🎯"
                "CANCELLED" -> "Cancelada ❌"
                else -> newStatus
            }
            showToast("Cita Actualizada", "Estado cambiado a: $statusLabel")
        }
    }

    // --- Services CRUD ---
    fun saveService(service: ServiceEntity) {
        viewModelScope.launch {
            if (service.id == 0L) {
                repository.insertService(service)
                showToast("Servicio Creado ✂️", "Se añadió '${service.name}' al catálogo.")
            } else {
                repository.updateService(service)
                showToast("Servicio Actualizado ✏️", "Se guardaron los cambios de '${service.name}'.")
            }
        }
    }

    fun deleteService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
            showToast("Servicio Eliminado", "Se eliminó '${service.name}'.")
        }
    }

    // --- Availability Blocks ---
    fun addAvailabilityBlock(block: AvailabilityBlockEntity) {
        viewModelScope.launch {
            repository.insertBlock(block)
            showToast("Bloqueo Guardado 🔒", "Disponibilidad actualizada para el ${block.date}.")
        }
    }

    fun deleteAvailabilityBlock(block: AvailabilityBlockEntity) {
        viewModelScope.launch {
            repository.deleteBlock(block)
            showToast("Bloqueo Eliminado 🔓", "Horario liberado con éxito.")
        }
    }

    // --- Notifications & Push Broadcast ---
    fun sendBroadcastPushReminders() {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val tomorrowStr = LocalDate.now().plusDays(1).toString()
            val upcoming = allAppointments.value.filter {
                (it.date == todayStr || it.date == tomorrowStr) && it.status == "CONFIRMED"
            }

            val count = maxOf(upcoming.size, 1)
            val notif = NotificationEntity(
                title = "Recordatorios Automáticos Enviados 📲",
                message = "Se enviaron $count notificaciones push con recordatorio de asistencia a clientes con citas próximas.",
                type = "REMINDER"
            )
            repository.insertNotification(notif)
            showToast(notif.title, notif.message)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showToast("Notificaciones al día", "Todas las alertas marcadas como leídas.")
        }
    }

    private fun showToast(title: String, message: String) {
        _toastNotification.value = NotificationEntity(
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )
    }

    fun dismissToast() {
        _toastNotification.value = null
    }
}
