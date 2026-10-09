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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.StatusCancelled
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.ui.theme.getTextFieldColors

@Composable
fun AdminAvailabilityScreen(
    blocks: List<AvailabilityBlockModel>,
    onAddBlock: (AvailabilityBlockModel) -> Unit,
    onDeleteBlock: (AvailabilityBlockModel) -> Unit,
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

            // Summary info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${blocks.size} bloqueos activos",
                    color = TextSilver,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Protege descansos y feriados",
                    color = GoldLight,
                    fontSize = 12.sp
                )
            }

            if (blocks.isEmpty()) {
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
                            text = "No hay bloqueos configurados",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Pulsa el botón '+' para bloquear un día o franja horaria",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(blocks, key = { it.id }) { block ->
                        AvailabilityBlockItemCard(
                            block = block,
                            onDelete = { onDeleteBlock(block) }
                        )
                    }
                }
            }
        }

        // FAB to add new block
        FloatingActionButton(
            onClick = { isAddingBlock = true },
            containerColor = GoldLight,
            contentColor = DarkObsidian,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Bloqueo")
        }

        // Add Dialog
        if (isAddingBlock) {
            AddAvailabilityBlockDialog(
                onDismiss = { isAddingBlock = false },
                onAdd = { newBlock ->
                    onAddBlock(newBlock)
                    isAddingBlock = false
                }
            )
        }
    }
}

@Composable
fun AvailabilityBlockItemCard(
    block: AvailabilityBlockModel,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (block.blockType == "FULL_DAY") Color(0xFFEF4444).copy(alpha = 0.2f)
                        else Color(0xFFF59E0B).copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (block.blockType == "FULL_DAY") Icons.Default.Block else Icons.Default.LockClock,
                    contentDescription = null,
                    tint = if (block.blockType == "FULL_DAY") Color(0xFFEF4444) else Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (block.blockType == "FULL_DAY") "Día Completo Bloqueado" else "Franja Horaria (${block.startTime} - ${block.endTime})",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = block.date,
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (block.reason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Motivo: ${block.reason}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusCancelled, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AddAvailabilityBlockDialog(
    onDismiss: () -> Unit,
    onAdd: (AvailabilityBlockModel) -> Unit
) {
    var dateStr by remember { mutableStateOf("2026-09-03") }
    var isFullDay by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf("14:00") }
    var endTime by remember { mutableStateOf("16:00") }
    var reason by remember { mutableStateOf("Descanso / Mantenimiento") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("Bloquear Horario o Día", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    colors = getTextFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFullDay,
                        onCheckedChange = { isFullDay = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldLight,
                            checkmarkColor = DarkObsidian
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bloquear Día Completo", color = TextWhite, fontSize = 13.sp)
                }

                if (!isFullDay) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Hora Inicio (HH:mm)") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("Hora Fin (HH:mm)") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo del Bloqueo") },
                    colors = getTextFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        AvailabilityBlockModel(
                            id = System.currentTimeMillis(),
                            blockType = if (isFullDay) "FULL_DAY" else "TIME_RANGE",
                            date = dateStr,
                            startTime = if (isFullDay) "" else startTime,
                            endTime = if (isFullDay) "" else endTime,
                            reason = reason
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldLight)
            ) {
                Text("Bloquear", color = DarkObsidian, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSilver)
            }
        }
    )
}
