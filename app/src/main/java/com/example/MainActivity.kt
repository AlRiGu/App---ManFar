package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.mapper.toApp
import com.example.data.mapper.toEntity
import com.example.data.mapper.toModel
import com.example.data.mapper.toShared
import com.example.data.repository.BarbershopRepository
import com.example.ui.components.NotificationsCenterDialog
import com.example.ui.components.ToastNotificationBanner
import com.example.ui.model.AvailableBarbers
import com.example.ui.screens.AdminAvailabilityScreen
import com.example.ui.screens.AdminCalendarScreen
import com.example.ui.screens.AdminClientsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminServicesCrudScreen
import com.example.shared.ui.screens.AuthScreen
import com.example.shared.ui.screens.BookingFlowScreen
import com.example.shared.ui.screens.ClientHomeScreen
import com.example.shared.ui.screens.ClientMyAppointmentsScreen
import com.example.shared.ui.screens.ClientProfileScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGold
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAmberGlow
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ManFarBarbershopTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.BarbershopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManFarBarbershopTheme {
                val repository = remember { BarbershopRepository() }

                val viewModel: BarbershopViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return BarbershopViewModel(repository) as T
                        }
                    }
                )

                ManFarAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ManFarAppRoot(viewModel: BarbershopViewModel) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val toastNotification by viewModel.activeNotificationToast.collectAsState()
    val authLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    val allServices by viewModel.allServices.collectAsState()
    val activeServices by viewModel.activeServices.collectAsState()
    val userAppointments by viewModel.userAppointments.collectAsState()
    val allAppointments by viewModel.allAppointments.collectAsState()
    val availabilityBlocks by viewModel.availabilityBlocks.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val businessKpis by viewModel.businessKpis.collectAsState()
    val incomeByDay by viewModel.incomeByDay.collectAsState()
    val serviceDistribution by viewModel.serviceRevenueDistribution.collectAsState()

    val selectedService by viewModel.selectedService.collectAsState()
    val selectedBarber by viewModel.selectedBarber.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val bookingNotes by viewModel.bookingNotes.collectAsState()
    val availableSlots by viewModel.availableSlots.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showBookingSuccessDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (!isLoggedIn) {
            AuthScreen(
                onLoginSuccess = { /* ViewModel already handles login state */ },
                onQuickRoleSelect = { role, email, name ->
                    viewModel.loginQuick(role, email, name)
                },
                onEmailPasswordAuth = { email, password, isRegister, name ->
                    viewModel.authenticateWithEmailPassword(email, password, isRegister, name)
                },
                onGoogleSignInClick = {
                    viewModel.authenticateWithGoogle(context)
                },
                externalErrorMessage = authError,
                isLoading = authLoading
            )
        } else {
            Scaffold(
                containerColor = DarkObsidian,
                bottomBar = {
                    ManFarBottomNavigation(
                        currentRole = currentRole,
                        currentTab = currentTab,
                        onTabSelected = { newTab ->
                            viewModel.setTab(newTab)
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        "CLIENT_HOME" -> {
                            ClientHomeScreen(
                                user = currentUser.toModel(),
                                activeServices = activeServices.map { it.toModel() },
                                upcomingAppointments = userAppointments.map { it.toModel() },
                                onServiceSelect = { serviceModel ->
                                    viewModel.selectService(serviceModel.toEntity())
                                    viewModel.setTab("CLIENT_BOOKING")
                                },
                                onViewAllAppointments = {
                                    viewModel.setTab("CLIENT_APPOINTMENTS")
                                },
                                onOpenNotifications = {
                                    showNotificationsDialog = true
                                },
                                onStartBooking = {
                                    viewModel.setTab("CLIENT_BOOKING")
                                }
                            )
                        }

                        "CLIENT_BOOKING" -> {
                            BookingFlowScreen(
                                services = activeServices.map { it.toModel() },
                                selectedService = selectedService?.toModel(),
                                selectedBarber = selectedBarber.toShared(),
                                selectedDate = selectedDate,
                                selectedTime = selectedTime,
                                notes = bookingNotes,
                                availableSlots = availableSlots.map { it.toShared() },
                                user = currentUser.toModel(),
                                onSelectService = { serviceModel -> viewModel.selectService(serviceModel.toEntity()) },
                                onSelectBarber = { barberModel -> viewModel.selectBarber(barberModel.toApp()) },
                                onSelectDate = { viewModel.selectDate(it) },
                                onSelectTime = { viewModel.selectTime(it) },
                                onNotesChange = { viewModel.setBookingNotes(it) },
                                onConfirmBooking = {
                                    viewModel.confirmBooking {
                                        showBookingSuccessDialog = true
                                    }
                                },
                                onBack = {
                                    viewModel.setTab("CLIENT_HOME")
                                }
                            )
                        }

                        "CLIENT_APPOINTMENTS" -> {
                            ClientMyAppointmentsScreen(
                                appointments = userAppointments.map { it.toModel() },
                                onCancelAppointment = { apptModel ->
                                    viewModel.cancelAppointment(apptModel.toEntity())
                                },
                                onBookNewClick = {
                                    viewModel.setTab("CLIENT_BOOKING")
                                }
                            )
                        }

                        "CLIENT_PROFILE" -> {
                            ClientProfileScreen(
                                user = currentUser.toModel(),
                                onUpdateProfile = { updatedModel ->
                                    viewModel.updateUserProfile(updatedModel.toEntity())
                                },
                                onSwitchToAdmin = {
                                    viewModel.switchRole("ADMIN")
                                },
                                onLogout = {
                                    viewModel.logout()
                                }
                            )
                        }

                        "ADMIN_DASHBOARD" -> {
                            AdminDashboardScreen(
                                kpis = businessKpis,
                                incomeByDay = incomeByDay,
                                serviceRevenueDistribution = serviceDistribution,
                                recentAppointments = allAppointments,
                                onConfirmAppointment = { viewModel.confirmAppointment(it) },
                                onCompleteAppointment = { viewModel.completeAppointment(it) },
                                onCancelAppointment = { viewModel.cancelAppointment(it) },
                                onSendBroadcastReminders = { viewModel.sendBroadcastPushReminders() },
                                onNavigateToCalendar = { viewModel.setTab("ADMIN_CALENDAR") },
                                onNavigateToServices = { viewModel.setTab("ADMIN_SERVICES") },
                                onNavigateToAvailability = { viewModel.setTab("ADMIN_AVAILABILITY") },
                                onNavigateToClients = { viewModel.setTab("ADMIN_CLIENTS") },
                                onSwitchToClient = { viewModel.switchRole("CLIENT") }
                            )
                        }

                        "ADMIN_CALENDAR" -> {
                            AdminCalendarScreen(
                                appointments = allAppointments,
                                selectedDate = selectedDate,
                                onSelectDate = { viewModel.selectDate(it) },
                                onConfirmAppointment = { viewModel.confirmAppointment(it) },
                                onCompleteAppointment = { viewModel.completeAppointment(it) },
                                onCancelAppointment = { viewModel.cancelAppointment(it) },
                                onBack = { viewModel.setTab("ADMIN_DASHBOARD") }
                            )
                        }

                        "ADMIN_SERVICES" -> {
                            AdminServicesCrudScreen(
                                services = allServices,
                                onSaveService = { viewModel.saveService(it) },
                                onDeleteService = { viewModel.deleteService(it) },
                                onBack = { viewModel.setTab("ADMIN_DASHBOARD") }
                            )
                        }

                        "ADMIN_AVAILABILITY" -> {
                            AdminAvailabilityScreen(
                                blocks = availabilityBlocks,
                                onAddBlock = { viewModel.addAvailabilityBlock(it) },
                                onDeleteBlock = { viewModel.deleteAvailabilityBlock(it) },
                                onBack = { viewModel.setTab("ADMIN_DASHBOARD") }
                            )
                        }

                        "ADMIN_CLIENTS" -> {
                            AdminClientsScreen(
                                clients = clients,
                                allAppointments = allAppointments,
                                onBack = { viewModel.setTab("ADMIN_DASHBOARD") }
                            )
                        }
                    }
                }
            }
        }

        // Top In-App Toast Notification Banner
        ToastNotificationBanner(
            notification = toastNotification,
            onDismiss = { viewModel.dismissToast() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Notifications Center Dialog
        if (showNotificationsDialog) {
            NotificationsCenterDialog(
                notifications = notifications,
                onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
                onDismiss = { showNotificationsDialog = false }
            )
        }

        // Booking Success Dialog
        if (showBookingSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showBookingSuccessDialog = false
                    viewModel.setTab("CLIENT_APPOINTMENTS")
                },
                containerColor = DarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Cita Agendada con Éxito!",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Text(
                        text = "Tu reserva para ${selectedService?.name ?: "Servicio"} con ${selectedBarber.name} el día $selectedDate a las $selectedTime ha sido registrada. Hemos enviado un recordatorio a tus notificaciones.",
                        color = TextSilver,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBookingSuccessDialog = false
                            viewModel.setTab("CLIENT_APPOINTMENTS")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ver Mis Citas", color = DarkObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun ManFarBottomNavigation(
    currentRole: String,
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
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
        if (currentRole == "CLIENT") {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = currentTab == "CLIENT_HOME",
                testTag = "nav_client_home",
                onClick = { onTabSelected("CLIENT_HOME") }
            )
            BottomNavItem(
                icon = Icons.Default.ContentCut,
                label = "Reservar",
                isSelected = currentTab == "CLIENT_BOOKING",
                testTag = "nav_client_booking",
                onClick = { onTabSelected("CLIENT_BOOKING") }
            )
            BottomNavItem(
                icon = Icons.Default.CalendarToday,
                label = "Mis Citas",
                isSelected = currentTab == "CLIENT_APPOINTMENTS",
                testTag = "nav_client_appointments",
                onClick = { onTabSelected("CLIENT_APPOINTMENTS") }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                isSelected = currentTab == "CLIENT_PROFILE",
                testTag = "nav_client_profile",
                onClick = { onTabSelected("CLIENT_PROFILE") }
            )
        } else {
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                isSelected = currentTab == "ADMIN_DASHBOARD",
                testTag = "nav_admin_dashboard",
                onClick = { onTabSelected("ADMIN_DASHBOARD") }
            )
            BottomNavItem(
                icon = Icons.Default.CalendarMonth,
                label = "Calendario",
                isSelected = currentTab == "ADMIN_CALENDAR",
                testTag = "nav_admin_calendar",
                onClick = { onTabSelected("ADMIN_CALENDAR") }
            )
            BottomNavItem(
                icon = Icons.Default.ContentCut,
                label = "Servicios",
                isSelected = currentTab == "ADMIN_SERVICES",
                testTag = "nav_admin_services",
                onClick = { onTabSelected("ADMIN_SERVICES") }
            )
            BottomNavItem(
                icon = Icons.Default.Block,
                label = "Horarios",
                isSelected = currentTab == "ADMIN_AVAILABILITY",
                testTag = "nav_admin_availability",
                onClick = { onTabSelected("ADMIN_AVAILABILITY") }
            )
            BottomNavItem(
                icon = Icons.Default.People,
                label = "Clientes",
                isSelected = currentTab == "ADMIN_CLIENTS",
                testTag = "nav_admin_clients",
                onClick = { onTabSelected("ADMIN_CLIENTS") }
            )
        }
    }
}

@Composable
fun androidx.compose.foundation.layout.RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DarkObsidian,
            selectedTextColor = GoldLight,
            indicatorColor = GoldLight,
            unselectedIconColor = TextSilver,
            unselectedTextColor = TextMuted
        ),
        modifier = Modifier
            .testTag(testTag)
            .padding(vertical = 4.dp)
    )
}
