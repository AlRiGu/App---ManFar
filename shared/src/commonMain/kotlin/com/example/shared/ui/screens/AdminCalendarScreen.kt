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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AppointmentModel
import com.example.shared.ui.components.AppointmentCard
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.util.generateBookingDays

@Composable
fun AdminCalendarScreen(
    appointments: List<AppointmentModel>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    onConfirmAppointment: (AppointmentModel) -> Unit,
    onCompleteAppointment: (AppointmentModel) -> Unit,
    onCancelAppointment: (AppointmentModel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatusFilter by remember { mutableStateOf("Todas") }
    val statusFilters = listOf("Todas", "Pendientes", "Confirmadas", "Completadas", "Canceladas")

    val bookingDays = remember { generateBookingDays(selectedDate.ifBlank { "2026-08-31" }) }

    // Filter appointments for the selected date
    val dayAppointments = appointments.filter { it.date == selectedDate }
    val filteredAppointments = when (selectedStatusFilter) {
        "Pendientes" -> dayAppointments.filter { it.status == "PENDING" }
        "Confirmadas" -> dayAppointments.filter { it.status == "CONFIRMED" }
        "Completadas" -> dayAppointments.filter { it.status == "COMPLETED" }
        "Canceladas" -> dayAppointments.filter { it.status == "CANCELLED" }
        else -> dayAppointments
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = GoldLight
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Agenda de Citas",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Control de turnos y estado diario",
                    color = GoldPrimary,
                    fontSize = 12.sp
                )
            }
        }

        // Horizontal Date Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bookingDays.forEach { opt ->
                val isSelected = opt.isoDate == selectedDate
                val count = appointments.count { it.date == opt.isoDate && it.status != "CANCELLED" }

                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GoldLight else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) GoldLight else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectDate(opt.isoDate) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = opt.dayOfWeek,
                            color = if (isSelected) DarkObsidian else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = opt.dayNumber,
                            color = if (isSelected) DarkObsidian else TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DarkObsidian else GoldLight)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Status Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statusFilters.forEach { filter ->
                val isSelected = selectedStatusFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatusFilter = filter },
                    label = { Text(filter, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldLight,
                        selectedLabelColor = DarkObsidian,
                        containerColor = DarkSurface,
                        labelColor = TextSilver
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = DarkBorder,
                        selectedBorderColor = GoldLight
                    )
                )
            }
        }

        // Summary of selected day
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedDate • ${filteredAppointments.size} cita(s)",
                color = TextSilver,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            val totalRevenueDay = dayAppointments
                .filter { it.status == "COMPLETED" || it.status == "CONFIRMED" }
                .sumOf { it.servicePrice }

            Text(
                text = "Total día: €${totalRevenueDay.toInt()}",
                color = GoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Appointments List
        if (filteredAppointments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay citas para este día o filtro",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Selecciona otra fecha para ver los turnos",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredAppointments, key = { it.id }) { appt ->
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
}
