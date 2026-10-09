package com.example.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.logic.BarbershopCoreLogic
import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailableBarbers
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.model.Barber
import com.example.shared.model.BusinessKpis
import com.example.shared.model.ClientUserModel
import com.example.shared.model.DayIncomePoint
import com.example.shared.model.NotificationModel
import com.example.shared.model.ServiceModel
import com.example.shared.model.ServiceRevenueShare
import com.example.shared.ui.components.NotificationsCenterDialog
import com.example.shared.ui.components.ToastNotificationBanner
import com.example.shared.ui.screens.AdminAvailabilityScreen
import com.example.shared.ui.screens.AdminCalendarScreen
import com.example.shared.ui.screens.AdminClientsScreen
import com.example.shared.ui.screens.AdminDashboardScreen
import com.example.shared.ui.screens.AdminServicesCrudScreen
import com.example.shared.ui.screens.AuthScreen
import com.example.shared.ui.screens.BookingFlowScreen
import com.example.shared.ui.screens.ClientHomeScreen
import com.example.shared.ui.screens.ClientMyAppointmentsScreen
import com.example.shared.ui.screens.ClientProfileScreen
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.ManFarBarbershopTheme
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver

val DefaultSeedServices = listOf(
    ServiceModel(
        id = 1,
        name = "Corte Clásico & Peinado",
        category = "Cortes",
        price = 18.0,
        durationMinutes = 35,
        description = "Corte tradicional a tijera o máquina con acabado profesional y peinado con pomada mate.",
        iconName = "content_cut",
        isActive = true
    ),
    ServiceModel(
        id = 2,
        name = "Degradado Skin Fade VIP",
        category = "Cortes",
        price = 22.0,
        durationMinutes = 45,
        description = "Degradado a piel pulido con shaver, perfilado de patillas, texturizado superior y peinado.",
        iconName = "content_cut",
        isActive = true
    ),
    ServiceModel(
        id = 3,
        name = "Ritual Barba & Toalla Caliente",
        category = "Barba",
        price = 16.0,
        durationMinutes = 30,
        description = "Tratamiento de vapor, aceites esenciales nutritivos, toallas calientes y afeitado a navaja.",
        iconName = "face",
        isActive = true
    ),
    ServiceModel(
        id = 4,
        name = "Combo Royal: Corte + Barba",
        category = "Combos",
        price = 32.0,
        durationMinutes = 60,
        description = "Experiencia completa: Corte degrade o tijera + ritual de barba completo con toalla y masaje.",
        iconName = "auto_awesome",
        isActive = true
    ),
    ServiceModel(
        id = 5,
        name = "Diseño de Cejas & Perfilado",
        category = "Tratamientos",
        price = 8.0,
        durationMinutes = 15,
        description = "Limpieza de cejas con navaja y pinzas para un acabado simétrico y pulido.",
        iconName = "spa",
        isActive = true
    )
)

val DefaultSeedClients = listOf(
    ClientUserModel(
        id = 1,
        name = "Carlos Mendoza",
        email = "carlos.mendoza@gmail.com",
        phone = "+34 612 345 678",
        role = "CLIENT",
        preferredBarber = "Manuel",
        avatarInitials = "CM"
    ),
    ClientUserModel(
        id = 2,
        name = "Alejandro Vega",
        email = "alejandro.vega@hotmail.com",
        phone = "+34 622 987 654",
        role = "CLIENT",
        preferredBarber = "Manuel",
        avatarInitials = "AV"
    ),
    ClientUserModel(
        id = 3,
        name = "Javier Navarro",
        email = "j.navarro@outlook.com",
        phone = "+34 633 456 789",
        role = "CLIENT",
        preferredBarber = "Manuel",
        avatarInitials = "JN"
    ),
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

val DefaultSeedBlocks = listOf(
    AvailabilityBlockModel(
        id = 1,
        blockType = "TIME_RANGE",
        date = "2026-09-02",
        startTime = "14:00",
        endTime = "15:00",
        reason = "Descanso & Almuerzo"
    )
)

@Composable
fun SharedApp() {
    ManFarBarbershopTheme {
        var isAuthenticated by remember { mutableStateOf(false) }
        var currentUser by remember {
            mutableStateOf(
                ClientUserModel(
                    id = 1,
                    name = "Carlos Mendoza",
                    email = "carlos.mendoza@gmail.com",
                    phone = "+34 612 345 678",
                    role = "CLIENT",
                    avatarInitials = "CM"
                )
            )
        }
        var activeTab by remember { mutableStateOf("CLIENT_HOME") }
        var services by remember { mutableStateOf(DefaultSeedServices) }
        var selectedService by remember { mutableStateOf<ServiceModel?>(null) }
        var selectedBarber by remember { mutableStateOf<Barber>(AvailableBarbers.first()) }
        var selectedDate by remember { mutableStateOf("2026-09-02") }
        var selectedTime by remember { mutableStateOf<String?>("11:15") }
        var bookingNotes by remember { mutableStateOf("") }
        var showNotificationsDialog by remember { mutableStateOf(false) }
        var activeNotificationToast by remember { mutableStateOf<NotificationModel?>(null) }

        var clientsList by remember { mutableStateOf(DefaultSeedClients) }
        var availabilityBlocks by remember { mutableStateOf(DefaultSeedBlocks) }

        var appointments by remember {
            mutableStateOf(
                listOf(
                    AppointmentModel(
                        id = 101,
                        clientName = "Carlos Mendoza",
                        clientEmail = "carlos.mendoza@gmail.com",
                        clientPhone = "+34 612 345 678",
                        barberName = "Manuel",
                        serviceId = 2,
                        serviceName = "Degradado Skin Fade VIP",
                        servicePrice = 22.0,
                        date = "2026-09-02",
                        timeSlot = "16:30",
                        status = "CONFIRMED",
                        notes = "Corte bien bajo"
                    ),
                    AppointmentModel(
                        id = 102,
                        clientName = "Alejandro Vega",
                        clientEmail = "alejandro.vega@hotmail.com",
                        clientPhone = "+34 622 987 654",
                        barberName = "Manuel",
                        serviceId = 3,
                        serviceName = "Ritual Barba & Toalla Caliente",
                        servicePrice = 16.0,
                        date = "2026-09-02",
                        timeSlot = "12:00",
                        status = "COMPLETED",
                        notes = "Aceite de eucalipto"
                    ),
                    AppointmentModel(
                        id = 103,
                        clientName = "Javier Navarro",
                        clientEmail = "j.navarro@outlook.com",
                        clientPhone = "+34 633 456 789",
                        barberName = "Manuel",
                        serviceId = 4,
                        serviceName = "Combo Royal: Corte + Barba",
                        servicePrice = 32.0,
                        date = "2026-09-03",
                        timeSlot = "10:30",
                        status = "PENDING",
                        notes = "Primera vez en ManFar"
                    )
                )
            )
        }

        var notifications by remember {
            mutableStateOf(
                listOf(
                    NotificationModel(
                        id = 1,
                        title = "¡Bienvenido a ManFar!",
                        message = "Reserva tu próximo turno y disfruta de una atención exclusiva con Manuel.",
                        timestamp = 1756641600000L,
                        isRead = false
                    ),
                    NotificationModel(
                        id = 2,
                        title = "Cita Confirmada",
                        message = "Tu cita para 'Degradado Skin Fade VIP' ha sido agendada.",
                        timestamp = 1756642000000L,
                        isRead = false
                    )
                )
            )
        }

        val availableSlots = remember(selectedDate, appointments, availabilityBlocks) {
            BarbershopCoreLogic.computeAvailableTimeSlots(
                selectedDate = selectedDate,
                appointments = appointments,
                blocks = availabilityBlocks
            )
        }

        // Computed KPIs for Admin
        val kpis = remember(appointments, clientsList) {
            val completed = appointments.filter { it.status == "COMPLETED" }
            val confirmed = appointments.filter { it.status == "CONFIRMED" }
            val rev = completed.sumOf { it.servicePrice }
            val avg = if (completed.isNotEmpty()) rev / completed.size else 0.0
            BusinessKpis(
                totalRevenue = rev,
                completedAppointmentsCount = completed.size,
                averageTicket = avg,
                totalClientsCount = clientsList.size,
                todayAppointmentsCount = appointments.count { it.date == selectedDate && it.status != "CANCELLED" },
                projectedRevenue = confirmed.sumOf { it.servicePrice }
            )
        }

        val incomeByDay = remember(appointments) {
            val daysList = listOf(
                "2026-08-28" to "Vie 28",
                "2026-08-29" to "Sáb 29",
                "2026-08-30" to "Dom 30",
                "2026-08-31" to "Lun 31",
                "2026-09-01" to "Mar 01",
                "2026-09-02" to "Mié 02",
                "2026-09-03" to "Jue 03"
            )
            daysList.map { (dateIso, label) ->
                val dayAppts = appointments.filter {
                    it.date == dateIso && (it.status == "COMPLETED" || it.status == "CONFIRMED")
                }
                DayIncomePoint(
                    date = dateIso,
                    dayLabel = label,
                    income = dayAppts.sumOf { it.servicePrice },
                    count = dayAppts.size
                )
            }
        }

        val serviceRevenueDistribution = remember(appointments, services) {
            val valid = appointments.filter { it.status == "COMPLETED" || it.status == "CONFIRMED" }
            val totalRev = valid.sumOf { it.servicePrice }
            val grouped = valid.groupBy { it.serviceName }
            grouped.map { (name, list) ->
                val rev = list.sumOf { it.servicePrice }
                val pct = if (totalRev > 0) ((rev / totalRev) * 100).toFloat() else 0f
                val cat = services.find { it.name == name }?.category ?: "Servicio"
                ServiceRevenueShare(
                    serviceName = name,
                    category = cat,
                    revenue = rev,
                    percentage = pct,
                    bookingsCount = list.size,
                    colorHex = 0xFFD4AF37
                )
            }.sortedByDescending { it.revenue }
        }

        val isCurrentUserAdmin = currentUser.role.equals("ADMIN", ignoreCase = true) || 
                currentUser.email.contains("admin", ignoreCase = true)

        if (!isAuthenticated) {
            AuthScreen(
                onLoginSuccess = {
                    isAuthenticated = true
                    if (isCurrentUserAdmin) {
                        activeTab = "ADMIN_DASHBOARD"
                    } else {
                        activeTab = "CLIENT_HOME"
                    }
                },
                onQuickRoleSelect = { selectedRole, email, name ->
                    val resolvedRole = if (selectedRole.equals("ADMIN", ignoreCase = true) ||
                        email.contains("admin", ignoreCase = true)) "ADMIN" else "CLIENT"
                    val initials = name.trim().split(" ")
                        .filter { it.isNotBlank() }
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                        .take(2)
                        .ifBlank { if (resolvedRole == "ADMIN") "MF" else "CL" }

                    currentUser = currentUser.copy(
                        name = name,
                        email = email,
                        role = resolvedRole,
                        avatarInitials = initials
                    )
                    isAuthenticated = true
                    activeTab = if (resolvedRole == "ADMIN") "ADMIN_DASHBOARD" else "CLIENT_HOME"
                },
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            )
        } else {
            Scaffold(
                containerColor = DarkObsidian,
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkSurfaceElevated,
                        contentColor = GoldLight,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .border(1.dp, DarkBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        if (isCurrentUserAdmin) {
                            // Admin navigation tabs
                            NavigationBarItem(
                                selected = activeTab == "ADMIN_DASHBOARD",
                                onClick = { activeTab = "ADMIN_DASHBOARD" },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", modifier = Modifier.size(22.dp)) },
                                label = { Text("Dashboard", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "ADMIN_CALENDAR",
                                onClick = { activeTab = "ADMIN_CALENDAR" },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario", modifier = Modifier.size(22.dp)) },
                                label = { Text("Calendario", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "ADMIN_SERVICES",
                                onClick = { activeTab = "ADMIN_SERVICES" },
                                icon = { Icon(Icons.Default.ContentCut, contentDescription = "Servicios", modifier = Modifier.size(22.dp)) },
                                label = { Text("Servicios", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "ADMIN_AVAILABILITY",
                                onClick = { activeTab = "ADMIN_AVAILABILITY" },
                                icon = { Icon(Icons.Default.Block, contentDescription = "Horarios", modifier = Modifier.size(22.dp)) },
                                label = { Text("Horarios", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "ADMIN_CLIENTS",
                                onClick = { activeTab = "ADMIN_CLIENTS" },
                                icon = { Icon(Icons.Default.People, contentDescription = "Clientes", modifier = Modifier.size(22.dp)) },
                                label = { Text("Clientes", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            // Client navigation tabs
                            NavigationBarItem(
                                selected = activeTab == "CLIENT_HOME",
                                onClick = { activeTab = "CLIENT_HOME" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", modifier = Modifier.size(22.dp)) },
                                label = { Text("Inicio", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "CLIENT_APPOINTMENTS",
                                onClick = { activeTab = "CLIENT_APPOINTMENTS" },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Citas", modifier = Modifier.size(22.dp)) },
                                label = { Text("Mis Citas", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            NavigationBarItem(
                                selected = activeTab == "CLIENT_PROFILE",
                                onClick = { activeTab = "CLIENT_PROFILE" },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(22.dp)) },
                                label = { Text("Perfil", fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkObsidian,
                                    selectedTextColor = GoldLight,
                                    indicatorColor = GoldLight,
                                    unselectedIconColor = TextSilver,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (!isCurrentUserAdmin && activeTab != "CLIENT_BOOKING") {
                        FloatingActionButton(
                            onClick = {
                                selectedService = services.firstOrNull()
                                activeTab = "CLIENT_BOOKING"
                            },
                            containerColor = GoldLight,
                            contentColor = DarkObsidian,
                            elevation = FloatingActionButtonDefaults.elevation(6.dp),
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Reservar Cita")
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        "CLIENT_HOME" -> {
                            ClientHomeScreen(
                                user = currentUser,
                                activeServices = services,
                                upcomingAppointments = appointments.filter {
                                    it.clientEmail.equals(currentUser.email, ignoreCase = true)
                                },
                                onServiceSelect = { service ->
                                    selectedService = service
                                    activeTab = "CLIENT_BOOKING"
                                },
                                onViewAllAppointments = {
                                    activeTab = "CLIENT_APPOINTMENTS"
                                },
                                onOpenNotifications = {
                                    showNotificationsDialog = true
                                },
                                onStartBooking = {
                                    selectedService = services.firstOrNull()
                                    activeTab = "CLIENT_BOOKING"
                                }
                            )
                        }

                        "CLIENT_BOOKING" -> {
                            BookingFlowScreen(
                                services = services,
                                selectedService = selectedService,
                                selectedBarber = selectedBarber,
                                selectedDate = selectedDate,
                                selectedTime = selectedTime,
                                notes = bookingNotes,
                                availableSlots = availableSlots,
                                user = currentUser,
                                onSelectService = { selectedService = it },
                                onSelectBarber = { selectedBarber = it },
                                onSelectDate = { selectedDate = it },
                                onSelectTime = { selectedTime = it },
                                onNotesChange = { bookingNotes = it },
                                onConfirmBooking = {
                                    val newAppt = AppointmentModel(
                                        id = (appointments.maxOfOrNull { it.id } ?: 100) + 1,
                                        clientName = currentUser.name,
                                        clientEmail = currentUser.email,
                                        clientPhone = currentUser.phone,
                                        barberName = selectedBarber.name,
                                        serviceId = selectedService?.id ?: 1,
                                        serviceName = selectedService?.name ?: "Corte",
                                        servicePrice = selectedService?.price ?: 20.0,
                                        date = selectedDate,
                                        timeSlot = selectedTime ?: "10:00",
                                        notes = bookingNotes,
                                        status = "CONFIRMED"
                                    )
                                    appointments = listOf(newAppt) + appointments
                                    activeNotificationToast = NotificationModel(
                                        id = 999,
                                        title = "¡Cita Confirmada!",
                                        message = "$selectedDate • $selectedTime con ${selectedBarber.name}"
                                    )
                                    activeTab = "CLIENT_APPOINTMENTS"
                                },
                                onBack = {
                                    activeTab = "CLIENT_HOME"
                                }
                            )
                        }

                        "CLIENT_APPOINTMENTS" -> {
                            ClientMyAppointmentsScreen(
                                appointments = appointments.filter {
                                    it.clientEmail.equals(currentUser.email, ignoreCase = true)
                                },
                                onCancelAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "CANCELLED") else it
                                    }
                                    activeNotificationToast = NotificationModel(
                                        id = 998,
                                        title = "Cita Cancelada",
                                        message = "Tu reserva para ${target.serviceName} fue cancelada."
                                    )
                                },
                                onBookNewClick = {
                                    selectedService = services.firstOrNull()
                                    activeTab = "CLIENT_BOOKING"
                                }
                            )
                        }

                        "CLIENT_PROFILE" -> {
                            ClientProfileScreen(
                                user = currentUser,
                                onUpdateProfile = { updated ->
                                    currentUser = updated
                                    activeNotificationToast = NotificationModel(
                                        id = 997,
                                        title = "Perfil Actualizado",
                                        message = "Tus datos personales se han guardado."
                                    )
                                },
                                onSwitchToAdmin = {
                                    currentUser = currentUser.copy(
                                        role = "ADMIN",
                                        name = if (currentUser.name.isNotBlank()) currentUser.name else "Admin ManFar",
                                        email = if (currentUser.email.isNotBlank()) currentUser.email else "admin@manfarbarbershop.com"
                                    )
                                    activeTab = "ADMIN_DASHBOARD"
                                    activeNotificationToast = NotificationModel(
                                        id = 996,
                                        title = "Modo Administrador Activo ✂️",
                                        message = "Has ingresado al panel de gestión ManFar."
                                    )
                                },
                                onLogout = {
                                    isAuthenticated = false
                                    activeTab = "CLIENT_HOME"
                                }
                            )
                        }

                        "ADMIN_DASHBOARD" -> {
                            AdminDashboardScreen(
                                kpis = kpis,
                                incomeByDay = incomeByDay,
                                serviceRevenueDistribution = serviceRevenueDistribution,
                                recentAppointments = appointments,
                                onConfirmAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "CONFIRMED") else it
                                    }
                                    activeNotificationToast = NotificationModel(
                                        id = 901,
                                        title = "Cita Confirmada",
                                        message = "Cita de ${target.clientName} confirmada."
                                    )
                                },
                                onCompleteAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "COMPLETED") else it
                                    }
                                    activeNotificationToast = NotificationModel(
                                        id = 902,
                                        title = "Cita Completada",
                                        message = "Servicio completado. Ingreso registrado."
                                    )
                                },
                                onCancelAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "CANCELLED") else it
                                    }
                                    activeNotificationToast = NotificationModel(
                                        id = 903,
                                        title = "Cita Cancelada",
                                        message = "La cita fue cancelada."
                                    )
                                },
                                onSendBroadcastReminders = {
                                    activeNotificationToast = NotificationModel(
                                        id = 904,
                                        title = "Recordatorios Enviados 🔔",
                                        message = "Avisos push emitidos a los clientes agendados."
                                    )
                                },
                                onNavigateToCalendar = { activeTab = "ADMIN_CALENDAR" },
                                onNavigateToServices = { activeTab = "ADMIN_SERVICES" },
                                onNavigateToAvailability = { activeTab = "ADMIN_AVAILABILITY" },
                                onNavigateToClients = { activeTab = "ADMIN_CLIENTS" },
                                onSwitchToClient = {
                                    currentUser = currentUser.copy(role = "CLIENT")
                                    activeTab = "CLIENT_HOME"
                                }
                            )
                        }

                        "ADMIN_CALENDAR" -> {
                            AdminCalendarScreen(
                                appointments = appointments,
                                selectedDate = selectedDate,
                                onSelectDate = { selectedDate = it },
                                onConfirmAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "CONFIRMED") else it
                                    }
                                },
                                onCompleteAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "COMPLETED") else it
                                    }
                                },
                                onCancelAppointment = { target ->
                                    appointments = appointments.map {
                                        if (it.id == target.id) it.copy(status = "CANCELLED") else it
                                    }
                                },
                                onBack = { activeTab = "ADMIN_DASHBOARD" }
                            )
                        }

                        "ADMIN_SERVICES" -> {
                            AdminServicesCrudScreen(
                                services = services,
                                onSaveService = { saved ->
                                    val exists = services.any { it.id == saved.id }
                                    services = if (exists) {
                                        services.map { if (it.id == saved.id) saved else it }
                                    } else {
                                        val newId = (services.maxOfOrNull { it.id } ?: 0L) + 1L
                                        services + saved.copy(id = newId)
                                    }
                                    activeNotificationToast = NotificationModel(
                                        id = 905,
                                        title = "Catálogo Actualizado",
                                        message = "El servicio '${saved.name}' fue guardado."
                                    )
                                },
                                onDeleteService = { target ->
                                    services = services.filter { it.id != target.id }
                                    activeNotificationToast = NotificationModel(
                                        id = 906,
                                        title = "Servicio Eliminado",
                                        message = "Se eliminó '${target.name}' del catálogo."
                                    )
                                },
                                onBack = { activeTab = "ADMIN_DASHBOARD" }
                            )
                        }

                        "ADMIN_AVAILABILITY" -> {
                            AdminAvailabilityScreen(
                                blocks = availabilityBlocks,
                                onAddBlock = { newBlock ->
                                    availabilityBlocks = availabilityBlocks + newBlock
                                    activeNotificationToast = NotificationModel(
                                        id = 907,
                                        title = "Bloqueo Guardado",
                                        message = "Horario bloqueado para ${newBlock.date}."
                                    )
                                },
                                onDeleteBlock = { target ->
                                    availabilityBlocks = availabilityBlocks.filter { it.id != target.id }
                                    activeNotificationToast = NotificationModel(
                                        id = 908,
                                        title = "Bloqueo Eliminado",
                                        message = "Horario desbloqueado."
                                    )
                                },
                                onBack = { activeTab = "ADMIN_DASHBOARD" }
                            )
                        }

                        "ADMIN_CLIENTS" -> {
                            AdminClientsScreen(
                                clients = clientsList,
                                allAppointments = appointments,
                                onBack = { activeTab = "ADMIN_DASHBOARD" }
                            )
                        }
                    }

                    // Toast Notification overlay
                    ToastNotificationBanner(
                        notification = activeNotificationToast,
                        onDismiss = { activeNotificationToast = null },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    )

                    // Notifications Dialog
                    if (showNotificationsDialog) {
                        NotificationsCenterDialog(
                            notifications = notifications,
                            onDismiss = { showNotificationsDialog = false },
                            onMarkAllAsRead = {
                                notifications = notifications.map { it.copy(isRead = true) }
                            }
                        )
                    }
                }
            }
        }
    }
}
