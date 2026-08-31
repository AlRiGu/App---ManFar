package com.example.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.ui.theme.StatusCancelled
import com.example.shared.ui.theme.StatusCompleted
import com.example.shared.ui.theme.StatusConfirmed
import com.example.shared.ui.theme.StatusPending

@Composable
fun AppointmentStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (status) {
        "CONFIRMED" -> Quad(
            StatusConfirmed.copy(alpha = 0.15f),
            StatusConfirmed,
            Icons.Default.CheckCircle,
            "Confirmada"
        )
        "PENDING" -> Quad(
            StatusPending.copy(alpha = 0.15f),
            StatusPending,
            Icons.Default.HourglassEmpty,
            "Pendiente"
        )
        "COMPLETED" -> Quad(
            StatusCompleted.copy(alpha = 0.15f),
            StatusCompleted,
            Icons.Default.TaskAlt,
            "Completada"
        )
        "CANCELLED" -> Quad(
            StatusCancelled.copy(alpha = 0.15f),
            StatusCancelled,
            Icons.Default.Cancel,
            "Cancelada"
        )
        else -> Quad(
            Color.Gray.copy(alpha = 0.15f),
            Color.Gray,
            Icons.Default.HourglassEmpty,
            status
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
