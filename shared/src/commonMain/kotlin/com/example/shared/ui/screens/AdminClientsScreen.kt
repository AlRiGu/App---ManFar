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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.AppointmentModel
import com.example.shared.model.ClientUserModel
import com.example.shared.ui.components.AppointmentStatusBadge
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldAmberGlow
import com.example.shared.ui.theme.GoldDark
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.GoldPrimary
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.ui.theme.getTextFieldColors

@Composable
fun AdminClientsScreen(
    clients: List<ClientUserModel>,
    allAppointments: List<AppointmentModel>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedClientForDetails by remember { mutableStateOf<ClientUserModel?>(null) }

    val filteredClients = clients.filter { client ->
        client.name.contains(searchQuery, ignoreCase = true) ||
                client.email.contains(searchQuery, ignoreCase = true) ||
                client.phone.contains(searchQuery, ignoreCase = true)
    }

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
                        text = "Directorio de Clientes",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Historial, fidelidad y datos de contacto",
                        color = GoldPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre, correo o teléfono...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GoldLight)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextSilver)
                            }
                        }
                    },
                    colors = getTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Clients List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredClients, key = { it.id }) { client ->
                    val clientAppointments = allAppointments.filter {
                        it.clientEmail.equals(client.email, ignoreCase = true)
                    }
                    val totalSpent = clientAppointments
                        .filter { it.status == "COMPLETED" }
                        .sumOf { it.servicePrice }

                    ClientAdminCard(
                        client = client,
                        appointmentsCount = clientAppointments.size,
                        totalSpent = totalSpent,
                        onClick = { selectedClientForDetails = client }
                    )
                }
            }
        }

        // Client Details Dialog
        selectedClientForDetails?.let { client ->
            val history = allAppointments.filter { it.clientEmail.equals(client.email, ignoreCase = true) }
            val completed = history.count { it.status == "COMPLETED" }

            AlertDialog(
                onDismissRequest = { selectedClientForDetails = null },
                containerColor = DarkSurfaceElevated,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GoldAmberGlow, GoldDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(client.avatarInitials, color = DarkObsidian, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(client.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(client.role, color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(client.email, color = TextSilver, fontSize = 12.sp)
                        }
                        if (client.phone.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(client.phone, color = TextSilver, fontSize = 12.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Barbero preferido: ${client.preferredBarber}", color = TextSilver, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Citas Totales", color = TextMuted, fontSize = 11.sp)
                                    Text("${history.size}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Completadas", color = TextMuted, fontSize = 11.sp)
                                    Text("$completed", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedClientForDetails = null }) {
                        Text("Cerrar", color = GoldLight, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun ClientAdminCard(
    client: ClientUserModel,
    appointmentsCount: Int,
    totalSpent: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(GoldLight, GoldDark))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.avatarInitials,
                    color = DarkObsidian,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = client.name,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (client.role == "ADMIN") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldAmberGlow.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("ADMIN", color = GoldAmberGlow, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = client.email,
                    color = TextSilver,
                    fontSize = 11.sp
                )
                if (client.phone.isNotBlank()) {
                    Text(
                        text = client.phone,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "€${totalSpent.toInt()}",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$appointmentsCount citas",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
