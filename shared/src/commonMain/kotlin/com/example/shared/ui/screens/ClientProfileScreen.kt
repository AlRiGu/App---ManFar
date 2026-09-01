package com.example.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.model.ClientUserModel
import com.example.shared.ui.theme.DarkBorder
import com.example.shared.ui.theme.DarkBorderGold
import com.example.shared.ui.theme.DarkObsidian
import com.example.shared.ui.theme.DarkSurface
import com.example.shared.ui.theme.DarkSurfaceElevated
import com.example.shared.ui.theme.GoldAmberGlow
import com.example.shared.ui.theme.GoldDark
import com.example.shared.ui.theme.GoldLight
import com.example.shared.ui.theme.StatusCancelled
import com.example.shared.ui.theme.TextMuted
import com.example.shared.ui.theme.TextSilver
import com.example.shared.ui.theme.TextWhite
import com.example.shared.ui.theme.getTextFieldColors

@Composable
fun ClientProfileScreen(
    user: ClientUserModel,
    onUpdateProfile: (ClientUserModel) -> Unit,
    onSwitchToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(user) { mutableStateOf(user.name) }
    var phone by remember(user) { mutableStateOf(user.phone) }
    var preferredBarber by remember(user) { mutableStateOf(user.preferredBarber) }
    var isEditing by remember { mutableStateOf(false) }
    var pushNotificationsEnabled by remember { mutableStateOf(true) }

    val barbersList = listOf("Manuel")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Mi Perfil & Ajustes",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Avatar Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(GoldLight, GoldDark)))
                        .border(2.dp, GoldLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.avatarInitials,
                        color = DarkObsidian,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.name,
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.email,
                    color = TextSilver,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Visitas", color = TextMuted, fontSize = 11.sp)
                        Text(text = "8", color = GoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Nivel", color = TextMuted, fontSize = 11.sp)
                        Text(text = "VIP Gold", color = GoldAmberGlow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Favorito", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Degradado", color = GoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Personal Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Datos Personales",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEditing) "Cancelar" else "Editar",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isEditing = !isEditing }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
                        colors = getTextFieldColors(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono de Contacto") },
                        colors = getTextFieldColors(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onUpdateProfile(user.copy(name = name, phone = phone, preferredBarber = preferredBarber))
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios", color = DarkObsidian, fontWeight = FontWeight.Bold)
                    }
                } else {
                    ProfileFieldRow(icon = Icons.Default.Person, label = "Nombre", value = user.name)
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileFieldRow(icon = Icons.Default.Email, label = "Correo", value = user.email)
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileFieldRow(icon = Icons.Default.Call, label = "Teléfono", value = user.phone.ifBlank { "No registrado" })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferred Barber Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Barbero Preferido",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Se preseleccionará automáticamente al agendar",
                    color = TextSilver,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                barbersList.forEach { barber ->
                    val isSelected = preferredBarber == barber
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GoldLight.copy(alpha = 0.15f) else DarkSurface)
                            .border(1.dp, if (isSelected) GoldLight else DarkBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                preferredBarber = barber
                                onUpdateProfile(user.copy(preferredBarber = barber))
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = barber, color = if (isSelected) GoldLight else TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications & Alerts
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Notificaciones y Avisos",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Recordatorios de Citas Push", color = TextWhite, fontSize = 13.sp)
                        Text(text = "Recibir alerta 2 horas antes de tu turno", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = pushNotificationsEnabled,
                        onCheckedChange = { pushNotificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkObsidian,
                            checkedTrackColor = GoldLight,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Switch to Admin Panel Button
        Button(
            onClick = onSwitchToAdmin,
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmberGlow.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAmberGlow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Entrar a Panel de Administración", color = GoldAmberGlow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Logout
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusCancelled.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = StatusCancelled, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = StatusCancelled, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
fun ProfileFieldRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, color = TextMuted, fontSize = 11.sp)
            Text(text = value, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
