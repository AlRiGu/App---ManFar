package com.example.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailableBarbers
import com.example.shared.model.ClientUserModel
import com.example.shared.model.ServiceModel
import com.example.shared.ui.components.AppointmentStatusBadge
import com.example.shared.ui.components.ServiceCard
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldAmberGlow
import com.example.shared.ui.theme.GoldDark
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.util.formatPrice

@Composable
fun ClientHomeScreen(
    user: ClientUserModel,
    activeServices: List<ServiceModel>,
    upcomingAppointments: List<AppointmentModel>,
    onServiceSelect: (ServiceModel) -> Unit,
    onViewAllAppointments: () -> Unit,
    onOpenNotifications: () -> Unit,
    onStartBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Cortes", "Barba", "Combos", "Tratamientos")

    val filteredServices = if (selectedCategory == "Todos") {
        activeServices
    } else {
        activeServices.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val nextAppointment = upcomingAppointments
        .filter { it.status == "CONFIRMED" || it.status == "PENDING" }
        .minByOrNull { it.date + " " + it.timeSlot }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Header Section
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(GoldLight, GoldDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.avatarInitials,
                                color = DarkObsidian,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hola, ${user.name.substringBefore(" ")} 👋",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ManFar Barbershop",
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Next Appointment Highlight Card (if any)
        if (nextAppointment != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tu Próxima Cita",
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ver historial",
                            color = TextSilver,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onViewAllAppointments() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderGold)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = nextAppointment.serviceName,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                AppointmentStatusBadge(status = nextAppointment.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceElevated)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = GoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = nextAppointment.date,
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = GoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = nextAppointment.timeSlot,
                                        color = GoldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "€${nextAppointment.servicePrice.formatPrice()}",
                                    color = GoldLight,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Barbero: ${nextAppointment.barberName}",
                                color = TextSilver,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }



        // 4. Service Categories Chips
        item {
            Column(modifier = Modifier.padding(top = 22.dp, start = 20.dp, end = 20.dp)) {
                Text(
                    text = "Servicios Disponibles",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Selecciona un servicio para agendar tu cita",
                    color = TextSilver,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldLight,
                                selectedLabelColor = DarkObsidian,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSilver
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) GoldLight else DarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // 5. Services List
        items(filteredServices, key = { it.id }) { service ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                ServiceCard(
                    service = service,
                    isSelected = false,
                    onBookClick = {
                        onServiceSelect(service)
                    }
                )
            }
        }
    }
}
