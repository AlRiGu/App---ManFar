package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderGold
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.time.LocalDate

@Composable
fun AdminAvailabilityScreen(
    blocks: List<AvailabilityBlockEntity>,
    onAddBlock: (AvailabilityBlockEntity) -> Unit,
    onDeleteBlock: (AvailabilityBlockEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingBlock by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        .testTag("btn_availability_back")
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
                        text = "Gestión de Disponibilidad",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bloqueo de días y horarios no laborables",
                        color = GoldPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Info Card
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderGold)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldLight.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LockClock, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Los horarios o fechas bloqueadas no estarán disponibles para reservas de clientes en la app.",
                            color = TextSilver,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Blocks List
            if (blocks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No hay bloqueos activos", color = TextSilver, fontSize = 14.sp)
                        Text("Todos los horarios habituales están abiertos", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(blocks, key = { it.id }) { block ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("block_item_${block.id}"),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(StatusCancelled.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val isAllDayBlock = block.blockType == "FULL_DAY"
                                        Icon(
                                            imageVector = if (isAllDayBlock) Icons.Default.CalendarMonth else Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = StatusCancelled,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        val isAllDayBlock = block.blockType == "FULL_DAY"
                                        Text(
                                            text = block.reason.ifBlank { if (isAllDayBlock) "Día Completo Bloqueado" else "Horario Bloqueado" },
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isAllDayBlock) "Fecha: ${block.date} (Todo el día)" else "Fecha: ${block.date} • ${block.startTime} - ${block.endTime}",
                                            color = GoldPrimary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteBlock(block) },
                                    modifier = Modifier.testTag("btn_delete_block_${block.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Desbloquear",
                                        tint = StatusCancelled,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Block FAB
        FloatingActionButton(
            onClick = { isAddingBlock = true },
            containerColor = GoldLight,
            contentColor = DarkObsidian,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_block")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Bloquear Horario")
        }

        // Add Block Dialog
        if (isAddingBlock) {
            var blockDate by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
            var blockReason by remember { mutableStateOf("") }
            var isAllDay by remember { mutableStateOf(false) }
            var startTime by remember { mutableStateOf("14:00") }
            var endTime by remember { mutableStateOf("15:30") }

            AlertDialog(
                onDismissRequest = { isAddingBlock = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "Bloquear Fecha u Horario",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = blockDate,
                            onValueChange = { blockDate = it },
                            label = { Text("Fecha (YYYY-MM-DD)") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isAllDay,
                                onCheckedChange = { isAllDay = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GoldLight,
                                    checkmarkColor = DarkObsidian,
                                    uncheckedColor = TextSilver
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bloquear Todo el Día", color = TextWhite, fontSize = 13.sp)
                        }

                        if (!isAllDay) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { startTime = it },
                                    label = { Text("Desde (HH:mm)") },
                                    colors = getTextFieldColors(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
                                    label = { Text("Hasta (HH:mm)") },
                                    colors = getTextFieldColors(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = blockReason,
                            onValueChange = { blockReason = it },
                            label = { Text("Motivo del bloqueo (Ej: Feriado, Almuerzo)") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newBlock = AvailabilityBlockEntity(
                                blockType = if (isAllDay) "FULL_DAY" else "TIME_RANGE",
                                date = blockDate.ifBlank { LocalDate.now().toString() },
                                startTime = if (isAllDay) "" else startTime,
                                endTime = if (isAllDay) "" else endTime,
                                reason = blockReason.ifBlank { if (isAllDay) "Día No Laborable" else "Horario Reservado/Bloqueado" }
                            )
                            onAddBlock(newBlock)
                            isAddingBlock = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Crear Bloqueo", color = DarkObsidian, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddingBlock = false }) {
                        Text("Cancelar", color = TextSilver)
                    }
                }
            )
        }
    }
}
