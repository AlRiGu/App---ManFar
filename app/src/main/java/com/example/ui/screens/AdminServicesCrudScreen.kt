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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ServiceEntity
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

@Composable
fun AdminServicesCrudScreen(
    services: List<ServiceEntity>,
    onSaveService: (ServiceEntity) -> Unit,
    onDeleteService: (ServiceEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingService by remember { mutableStateOf<ServiceEntity?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<ServiceEntity?>(null) }

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
                        .testTag("btn_services_back")
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
                        text = "${services.size} servicios en catálogo",
                        color = GoldPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Services list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(services, key = { it.id }) { service ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_service_card_${service.id}"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = service.name,
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(GoldDark.copy(alpha = 0.3f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = service.category,
                                                color = GoldPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${service.durationMinutes} min",
                                            color = TextSilver,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "€${String.format(java.util.Locale.US, "%.2f", service.price)}",
                                    color = GoldLight,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = service.description,
                                color = TextSilver,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (service.isActive) "Activo" else "Inactivo",
                                        color = if (service.isActive) GoldLight else TextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = service.isActive,
                                        onCheckedChange = { checked ->
                                            onSaveService(service.copy(isActive = checked))
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = DarkObsidian,
                                            checkedTrackColor = GoldLight,
                                            uncheckedTrackColor = DarkSurface
                                        )
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { editingService = service },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("btn_edit_service_${service.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = GoldLight, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { serviceToDelete = service },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("btn_delete_service_${service.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusCancelled, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Service Floating Action Button
        FloatingActionButton(
            onClick = { isAddingNew = true },
            containerColor = GoldLight,
            contentColor = DarkObsidian,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_service")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Servicio")
        }

        // Service Form Dialog (Add / Edit)
        if (isAddingNew || editingService != null) {
            val isEdit = editingService != null
            val currentService = editingService ?: ServiceEntity(
                name = "",
                category = "Cortes",
                price = 15.0,
                durationMinutes = 30,
                description = "",
                iconName = "content_cut"
            )

            var formName by remember { mutableStateOf(currentService.name) }
            var formCategory by remember { mutableStateOf(currentService.category) }
            var formPrice by remember { mutableStateOf(currentService.price.toString()) }
            var formDuration by remember { mutableStateOf(currentService.durationMinutes.toString()) }
            var formDescription by remember { mutableStateOf(currentService.description) }

            AlertDialog(
                onDismissRequest = {
                    isAddingNew = false
                    editingService = null
                },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = if (isEdit) "Editar Servicio" else "Nuevo Servicio",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = formName,
                            onValueChange = { formName = it },
                            label = { Text("Nombre del Servicio") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = formCategory,
                            onValueChange = { formCategory = it },
                            label = { Text("Categoría (Cortes, Barba, Combos, Tratamientos)") },
                            colors = getTextFieldColors(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = formPrice,
                                onValueChange = { formPrice = it },
                                label = { Text("Precio (€)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = getTextFieldColors(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = formDuration,
                                onValueChange = { formDuration = it },
                                label = { Text("Duración (min)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = getTextFieldColors(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = formDescription,
                            onValueChange = { formDescription = it },
                            label = { Text("Descripción") },
                            colors = getTextFieldColors(),
                            maxLines = 3,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val priceVal = formPrice.toDoubleOrNull() ?: currentService.price
                            val durationVal = formDuration.toIntOrNull() ?: currentService.durationMinutes
                            val updated = currentService.copy(
                                name = formName.ifBlank { "Servicio Sin Nombre" },
                                category = formCategory.ifBlank { "Cortes" },
                                price = priceVal,
                                durationMinutes = durationVal,
                                description = formDescription
                            )
                            onSaveService(updated)
                            isAddingNew = false
                            editingService = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Guardar", color = DarkObsidian, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isAddingNew = false
                        editingService = null
                    }) {
                        Text("Cancelar", color = TextSilver)
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        serviceToDelete?.let { targetService ->
            AlertDialog(
                onDismissRequest = { serviceToDelete = null },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "¿Eliminar Servicio?",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de eliminar el servicio '${targetService.name}'? Los clientes ya no podrán agendarlo.",
                        color = TextSilver,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteService(targetService)
                            serviceToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled),
                        shape = RoundedCornerShape(8.dp)
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
