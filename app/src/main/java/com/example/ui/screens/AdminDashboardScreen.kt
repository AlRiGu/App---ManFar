package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AppointmentEntity
import com.example.ui.components.AppointmentCard
import com.example.ui.components.DailyIncomeChartCard
import com.example.ui.components.KpiMetricsRow
import com.example.ui.components.ServiceDistributionCard
import com.example.ui.model.BusinessKpis
import com.example.ui.model.DayIncomePoint
import com.example.ui.model.ServiceRevenueShare
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGold
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAmberGlow
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusConfirmed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun AdminDashboardScreen(
    kpis: BusinessKpis,
    incomeByDay: List<DayIncomePoint>,
    serviceRevenueDistribution: List<ServiceRevenueShare>,
    recentAppointments: List<AppointmentEntity>,
    onConfirmAppointment: (AppointmentEntity) -> Unit,
    onCompleteAppointment: (AppointmentEntity) -> Unit,
    onCancelAppointment: (AppointmentEntity) -> Unit,
    onSendBroadcastReminders: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onNavigateToClients: () -> Unit,
    onSwitchToClient: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Admin Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GoldAmberGlow, GoldDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = DarkObsidian,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panel de Control",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ManFar Barbershop • Administración",
                                color = GoldPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onSwitchToClient,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_switch_to_client")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Cliente", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // 2. Metric KPI Cards (Horizontal Scroll)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Métricas del Negocio",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                KpiMetricsRow(kpis = kpis)
            }
        }

        // 3. Quick Admin Navigation Grid
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                Text(
                    text = "Accesos Rápidos de Gestión",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminShortcutCard(
                        title = "Calendario",
                        subtitle = "Vista por día",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = GoldLight,
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )
                    AdminShortcutCard(
                        title = "Servicios",
                        subtitle = "Gestión CRUD",
                        icon = Icons.Default.ContentCut,
                        accentColor = GoldAmberGlow,
                        onClick = onNavigateToServices,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminShortcutCard(
                        title = "Disponibilidad",
                        subtitle = "Bloquear horarios",
                        icon = Icons.Default.Block,
                        accentColor = Color(0xFFF59E0B),
                        onClick = onNavigateToAvailability,
                        modifier = Modifier.weight(1f)
                    )
                    AdminShortcutCard(
                        title = "Clientes",
                        subtitle = "Directorio y ventas",
                        icon = Icons.Default.People,
                        accentColor = StatusCompleted,
                        onClick = onNavigateToClients,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Send Broadcast Push Reminders Button
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = GoldAmberGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recordatorios Push Automáticos",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Enviar alerta de confirmación a todos los clientes agendados",
                                color = TextSilver,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onSendBroadcastReminders,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_broadcast_reminders")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = DarkObsidian, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enviar", color = DarkObsidian, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Interactive Daily Income Chart Card
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                DailyIncomeChartCard(data = incomeByDay)
            }
        }

        // 6. Service Revenue Share Card
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                ServiceDistributionCard(distribution = serviceRevenueDistribution)
            }
        }

        // 7. Recent Appointments Header
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Citas Recientes",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ver Calendario",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToCalendar() }
                            .testTag("btn_view_calendar_text")
                    )
                }
            }
        }

        // 8. Recent Appointments List
        items(recentAppointments.take(5), key = { it.id }) { appt ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                AppointmentCard(
                    appointment = appt,
                    isAdminView = true,
                    onConfirm = { onConfirmAppointment(appt) },
                    onComplete = { onCompleteAppointment(appt) },
                    onCancel = { onCancelAppointment(appt) }
                )
            }
        }
    }
}

@Composable
fun AdminShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSilver,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
