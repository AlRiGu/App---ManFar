package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AppointmentEntity
import com.example.ui.components.AppointmentCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGold
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminCalendarScreen(
    appointments: List<AppointmentEntity>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    onConfirmAppointment: (AppointmentEntity) -> Unit,
    onCompleteAppointment: (AppointmentEntity) -> Unit,
    onCancelAppointment: (AppointmentEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatusFilter by remember { mutableStateOf("Todas") }
    val statusFilters = listOf("Todas", "Pendientes", "Confirmadas", "Completadas", "Canceladas")

    val days = remember {
        val list = mutableListOf<LocalDate>()
        val start = LocalDate.now().minusDays(3)
        for (i in 0..16) {
            list.add(start.plusDays(i.toLong()))
        }
        list
    }

    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEE", Locale("es", "ES")) }

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
                    .testTag("btn_calendar_back")
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
                    text = "Calendario de Citas",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestión de agenda diaria",
                    color = GoldPrimary,
                    fontSize = 12.sp
                )
            }
        }

        // Horizontal Date Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { dateObj ->
                val dateStr = dateObj.toString()
                val isSelected = selectedDate == dateStr
                val isToday = dateObj == LocalDate.now()
                val dayOfWeek = dateObj.format(dayFormatter).replaceFirstChar { it.uppercase() }
                val dayNum = dateObj.dayOfMonth.toString()

                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .height(78.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) GoldLight else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) GoldLight else if (isToday) GoldPrimary else DarkBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectDate(dateStr) }
                        .testTag("admin_date_tab_$dateStr")
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayOfWeek,
                            color = if (isSelected) DarkObsidian else TextSilver,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dayNum,
                            color = if (isSelected) DarkObsidian else TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isToday) {
                            Text(
                                text = "HOY",
                                color = if (isSelected) DarkObsidian else GoldPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statusFilters.forEach { statusTab ->
                val isSelected = selectedStatusFilter == statusTab
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatusFilter = statusTab },
                    label = {
                        Text(
                            text = statusTab,
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

        Spacer(modifier = Modifier.height(6.dp))

        // Selected Date Summary Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agenda del $selectedDate",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredAppointments.size} turnos encontrados",
                    color = GoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Appointments List
        if (filteredAppointments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No hay citas programadas para este día o estado",
                        color = TextSilver,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
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
