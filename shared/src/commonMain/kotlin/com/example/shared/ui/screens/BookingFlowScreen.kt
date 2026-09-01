package com.example.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AvailableBarbers
import com.example.shared.model.Barber
import com.example.shared.model.ClientUserModel
import com.example.shared.model.ServiceModel
import com.example.shared.model.TimeSlotItem
import com.example.shared.ui.components.ServiceCard
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldAmberGlow
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.ui.theme.getTextFieldColors
import com.example.shared.util.formatPrice
import com.example.shared.util.generateBookingDays

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingFlowScreen(
    services: List<ServiceModel>,
    selectedService: ServiceModel?,
    selectedBarber: Barber = AvailableBarbers.first(),
    selectedDate: String,
    selectedTime: String?,
    notes: String,
    availableSlots: List<TimeSlotItem>,
    user: ClientUserModel,
    onSelectService: (ServiceModel) -> Unit,
    onSelectBarber: ((Barber) -> Unit)? = null,
    onSelectDate: (String) -> Unit,
    onSelectTime: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirmBooking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(if (selectedService != null) 2 else 1) }
    val daysList = remember(selectedDate) { generateBookingDays(selectedDate.ifBlank { "2026-08-31" }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentStep > 1) {
                        currentStep--
                    } else {
                        onBack()
                    }
                },
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
                    text = "Agendar Cita",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Paso $currentStep de 3: ${
                        when (currentStep) {
                            1 -> "Seleccionar Servicio"
                            2 -> "Fecha y Horario"
                            else -> "Confirmar Reserva"
                        }
                    }",
                    color = GoldPrimary,
                    fontSize = 12.sp
                )
            }
        }

        // Progress Line (3 Steps)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (step in 1..3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (step <= currentStep) GoldLight else DarkSurfaceElevated)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content per step
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                1 -> {
                    // STEP 1: Select Service
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "¿Qué servicio deseas realizarte?",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        services.forEach { service ->
                            ServiceCard(
                                service = service,
                                isSelected = selectedService?.id == service.id,
                                onBookClick = {
                                    onSelectService(service)
                                    currentStep = 2
                                },
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }

                2 -> {
                    // STEP 2: Date & Time Picker
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Elige la Fecha",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontal Date Scroll
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            daysList.forEach { option ->
                                val dateStr = option.isoDate
                                val isSelected = selectedDate == dateStr

                                Box(
                                    modifier = Modifier
                                        .width(66.dp)
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) GoldLight else DarkSurfaceElevated)
                                        .border(1.dp, if (isSelected) GoldLight else DarkBorder, RoundedCornerShape(14.dp))
                                        .clickable { onSelectDate(dateStr) }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = option.dayOfWeek,
                                            color = if (isSelected) DarkObsidian else TextSilver,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = option.dayNumber,
                                            color = if (isSelected) DarkObsidian else TextWhite,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Horarios Disponibles para $selectedDate",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Atendido por Manuel • Espacios de 45 min",
                            color = GoldPrimary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Time slots grid
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            availableSlots.forEach { slot ->
                                val isSelected = selectedTime == slot.time

                                Box(
                                    modifier = Modifier
                                        .width(96.dp)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                !slot.isAvailable -> DarkSurface.copy(alpha = 0.5f)
                                                isSelected -> GoldLight
                                                else -> DarkSurfaceElevated
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            when {
                                                !slot.isAvailable -> DarkBorder.copy(alpha = 0.4f)
                                                isSelected -> GoldLight
                                                else -> DarkBorder
                                            },
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = slot.isAvailable) {
                                            onSelectTime(slot.time)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = slot.time,
                                            color = when {
                                                !slot.isAvailable -> TextMuted
                                                isSelected -> DarkObsidian
                                                else -> TextWhite
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        if (!slot.isAvailable) {
                                            Text(
                                                text = slot.unavailableReason.take(8),
                                                color = Color(0xFFEF4444),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = { currentStep = 3 },
                            enabled = selectedTime != null,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Revisar y Confirmar", color = DarkObsidian, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // STEP 3: Confirmation & Notes
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Resumen de tu Cita",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderGold)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                // Service
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Servicio:", color = TextSilver, fontSize = 13.sp)
                                    Text(
                                        text = selectedService?.name ?: "Corte",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Barber (Manuel)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Barbero:", color = TextSilver, fontSize = 13.sp)
                                    Text(
                                        text = selectedBarber.name,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Date & Time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Fecha & Hora:", color = TextSilver, fontSize = 13.sp)
                                    Text(
                                        text = "$selectedDate • $selectedTime",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Client
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Cliente:", color = TextSilver, fontSize = 13.sp)
                                    Text(
                                        text = user.name,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(DarkBorder)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Total Price
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total a Pagar:", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "€${(selectedService?.price ?: 0.0).formatPrice()}",
                                        color = GoldLight,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Special Notes Field
                        OutlinedTextField(
                            value = notes,
                            onValueChange = onNotesChange,
                            label = { Text("Instrucciones o notas especiales (Opcional)") },
                            placeholder = { Text("Ej: Toalla caliente, barba perfilada cuadrada...") },
                            leadingIcon = {
                                Icon(Icons.Default.Note, contentDescription = null, tint = GoldLight)
                            },
                            colors = getTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onConfirmBooking,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DarkObsidian)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar Cita Ahora", color = DarkObsidian, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
