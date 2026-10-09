package com.example.shared.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.logic.BarbershopCoreLogic
import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailableBarbers
import com.example.shared.model.Barber
import com.example.shared.model.ClientUserModel
import com.example.shared.model.NotificationModel
import com.example.shared.model.ServiceModel
import com.example.shared.ui.components.NotificationsCenterDialog
import com.example.shared.ui.components.ToastNotificationBanner
import com.example.shared.ui.screens.AuthScreen
import com.example.shared.ui.screens.BookingFlowScreen
import com.example.shared.ui.screens.ClientHomeScreen
import com.example.shared.ui.screens.ClientMyAppointmentsScreen
import com.example.shared.ui.screens.ClientProfileScreen
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.ManFarBarbershopTheme
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver

val DefaultSeedServices = listOf(
    ServiceModel(
        id = 1,
        name = "Corte Clásico & Peinado",
        category = "Corte",
        price = 18.0,
        durationMinutes = 35,
        description = "Corte tradicional a tijera o máquina con acabado profesional y peinado con pomada mate.",
        iconName = "content_cut"
    ),
    ServiceModel(
        id = 2,
        name = "Degradado Skin Fade VIP",
        category = "Corte",
        price = 22.0,
        durationMinutes = 45,
        description = "Degradado a piel pulido con shaver, perfilado de patillas, texturizado superior y peinado.",
        iconName = "content_cut"
    ),
    ServiceModel(
        id = 3,
        name = "Ritual Barba & Toalla Caliente",
        category = "Barba",
        price = 16.0,
        durationMinutes = 30,
        description = "Tratamiento de vapor, aceites esenciales nutritivos, toallas calientes y afeitado a navaja.",
        iconName = "face"
    ),
    ServiceModel(
        id = 4,
        name = "Combo Royal: Corte + Barba",
        category = "Combos",
        price = 32.0,
        durationMinutes = 60,
        description = "Experiencia completa: Corte degrade o tijera + ritual de barba completo con toalla y masaje.",
        iconName = "auto_awesome"
    ),
    ServiceModel(
        id = 5,
        name = "Diseño de Cejas & Perfilado",
        category = "Tratamientos",
        price = 8.0,
        durationMinutes = 15,
        description = "Limpieza de cejas con navaja y pinzas para un acabado simétrico y pulido.",
        iconName = "spa"
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
                    name = "Alejandro Barber",
                    email = "cliente@manfar.com",
                    phone = "+34 612 345 678",
                    role = "CLIENT",
                    avatarInitials = "AB"
                )
            )
        }
        var activeTab by remember { mutableStateOf("CLIENT_HOME") }
        var services by remember { mutableStateOf(DefaultSeedServices) }
        var selectedService by remember { mutableStateOf<ServiceModel?>(null) }
        var selectedBarber by remember { mutableStateOf<Barber>(AvailableBarbers.first()) }
        var selectedDate by remember { mutableStateOf("2026-08-31") }
        var selectedTime by remember { mutableStateOf<String?>("11:15") }
        var bookingNotes by remember { mutableStateOf("") }
        var showNotificationsDialog by remember { mutableStateOf(false) }
        var activeNotificationToast by remember { mutableStateOf<NotificationModel?>(null) }

        var appointments by remember {
            mutableStateOf(
                listOf(
                    AppointmentModel(
                        id = 101,
                        clientName = "Alejandro Barber",
                        clientEmail = "cliente@manfar.com",
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
                        clientName = "Alejandro Barber",
                        clientEmail = "cliente@manfar.com",
                        clientPhone = "+34 612 345 678",
                        barberName = "Manuel",
                        serviceId = 3,
                        serviceName = "Ritual Barba & Toalla Caliente",
                        servicePrice = 16.0,
                        date = "2026-08-25",
                        timeSlot = "12:00",
                        status = "COMPLETED",
                        notes = "Aceite de eucalipto"
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

        val availableSlots = remember(selectedDate, appointments) {
            BarbershopCoreLogic.computeAvailableTimeSlots(
                selectedDate = selectedDate,
                appointments = appointments,
                blocks = emptyList()
            )
        }

        if (!isAuthenticated) {
            AuthScreen(
                onLoginSuccess = {
                    isAuthenticated = true
                },
                onQuickRoleSelect = { _, email, name ->
                    currentUser = currentUser.copy(email = email, name = name)
                    isAuthenticated = true
                },
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            )
        } else {
            Scaffold(
                containerColor = DarkObsidian,
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = TextSilver,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                    ) {
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
                },
                floatingActionButton = {
                    if (activeTab != "CLIENT_BOOKING") {
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
                                upcomingAppointments = appointments,
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
                                appointments = appointments,
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
                                    activeNotificationToast = NotificationModel(
                                        id = 996,
                                        title = "Modo Administrador",
                                        message = "Disponible en el panel central."
                                    )
                                },
                                onLogout = {
                                    isAuthenticated = false
                                    activeTab = "CLIENT_HOME"
                                }
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
