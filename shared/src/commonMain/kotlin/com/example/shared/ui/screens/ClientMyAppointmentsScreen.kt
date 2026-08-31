package com.example.shared.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AppointmentModel
import com.example.shared.ui.components.AppointmentCard
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.StatusCancelled
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite

@Composable
fun ClientMyAppointmentsScreen(
    appointments: List<AppointmentModel>,
    onCancelAppointment: (AppointmentModel) -> Unit,
    onBookNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filterTabs = listOf("Todas", "Próximas", "Completadas", "Canceladas")

    var appointmentToCancel by remember { mutableStateOf<AppointmentModel?>(null) }

    val filteredList = when (selectedFilter) {
        "Próximas" -> appointments.filter { it.status == "CONFIRMED" || it.status == "PENDING" }
        "Completadas" -> appointments.filter { it.status == "COMPLETED" }
        "Canceladas" -> appointments.filter { it.status == "CANCELLED" }
        else -> appointments
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mis Citas",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Historial y próximas reservas",
                color = TextSilver,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = selectedFilter == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = tab },
                        label = {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No tienes citas en este estado",
                            color = TextSilver,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBookNewClick,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Agendar Nueva Cita", color = DarkObsidian, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            isAdminView = false,
                            onCancel = {
                                appointmentToCancel = appt
                            }
                        )
                    }
                }
            }
        }

        // Cancel Confirmation Dialog
        appointmentToCancel?.let { targetAppt ->
            AlertDialog(
                onDismissRequest = { appointmentToCancel = null },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "¿Cancelar esta Cita?",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de cancelar tu reserva para '${targetAppt.serviceName}' el día ${targetAppt.date} a las ${targetAppt.timeSlot}?",
                        color = TextSilver,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCancelAppointment(targetAppt)
                            appointmentToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sí, Cancelar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { appointmentToCancel = null }) {
                        Text("Mantener Cita", color = TextSilver)
                    }
                }
            )
        }
    }
}
