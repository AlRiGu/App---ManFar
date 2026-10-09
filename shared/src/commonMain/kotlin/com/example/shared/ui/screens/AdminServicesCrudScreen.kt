package com.example.shared.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.ServiceModel
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldDark
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.StatusCancelled
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.ui.theme.getTextFieldColors

@Composable
fun AdminServicesCrudScreen(
    services: List<ServiceModel>,
    onSaveService: (ServiceModel) -> Unit,
    onDeleteService: (ServiceModel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingService by remember { mutableStateOf<ServiceModel?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<ServiceModel?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                        text = "Gestión de Servicios",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Catálogo, precios y duraciones del salón",
                        color = GoldPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${services.size} servicios registrados",
                    color = TextSilver,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${services.count { it.isActive }} activos",
                    color = GoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Services List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(services, key = { it.id }) { service ->
                    ServiceAdminItemCard(
                        service = service,
                        onEdit = { editingService = service },
                        onDelete = { serviceToDelete = service },
                        onToggleActive = { updated ->
                            onSaveService(updated)
                        }
                    )
                }
            }
        }

        // FAB to add new service
        FloatingActionButton(
            onClick = { isAddingNew = true },
            containerColor = GoldLight,
            contentColor = DarkObsidian,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Servicio")
        }

        // Add / Edit Dialog
        if (isAddingNew || editingService != null) {
            val isEdit = editingService != null
            val initial = editingService ?: ServiceModel(
                name = "",
                category = "Cortes",
                price = 20.0,
                durationMinutes = 30,
                description = ""
            )

            ServiceFormDialog(
                title = if (isEdit) "Editar Servicio" else "Nuevo Servicio",
                service = initial,
                onDismiss = {
                    isAddingNew = false
                    editingService = null
                },
                onSave = { saved ->
                    onSaveService(saved)
                    isAddingNew = false
                    editingService = null
                }
            )
        }

        // Delete Confirmation Dialog
        serviceToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { serviceToDelete = null },
                containerColor = DarkSurfaceElevated,
                title = {
                    Text("Eliminar Servicio", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar '${target.name}' del catálogo? Esta acción no se puede deshacer.",
                        color = TextSilver
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteService(target)
                            serviceToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { serviceToDelete = null }) {
                        Text("Cancelar", color = TextSilver)
                    }
                }
            )
        }
    }
}

@Composable
fun ServiceAdminItemCard(
    service: ServiceModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: (ServiceModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (service.isActive) DarkBorder else DarkBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (service.isActive) GoldDark.copy(alpha = 0.2f) else DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = null,
                    tint = if (service.isActive) GoldLight else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.name,
                        color = if (service.isActive) TextWhite else TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.durationMinutes} min • ${service.category}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "€${service.price.toInt()}",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = GoldLight, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusCancelled, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ServiceFormDialog(
    title: String,
    service: ServiceModel,
    onDismiss: () -> Unit,
    onSave: (ServiceModel) -> Unit
) {
    var name by remember { mutableStateOf(service.name) }
    var category by remember { mutableStateOf(service.category) }
    var priceStr by remember { mutableStateOf(service.price.toString()) }
    var durationStr by remember { mutableStateOf(service.durationMinutes.toString()) }
    var description by remember { mutableStateOf(service.description) }
    var isActive by remember { mutableStateOf(service.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(text = title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Servicio") },
                    colors = getTextFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (Cortes, Barba, etc.)") },
                    colors = getTextFieldColors(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Precio (€)") },
                        colors = getTextFieldColors(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it },
                        label = { Text("Minutos") },
                        colors = getTextFieldColors(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    colors = getTextFieldColors(),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activo en reservas", color = TextWhite, fontSize = 13.sp)
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkObsidian,
                            checkedTrackColor = GoldLight
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 20.0
                    val d = durationStr.toIntOrNull() ?: 30
                    onSave(
                        service.copy(
                            name = name.ifBlank { "Servicio" },
                            category = category.ifBlank { "General" },
                            price = p,
                            durationMinutes = d,
                            description = description,
                            isActive = isActive
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldLight)
            ) {
                Text("Guardar", color = DarkObsidian, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSilver)
            }
        }
    )
}
