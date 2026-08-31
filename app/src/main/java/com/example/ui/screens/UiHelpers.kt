package com.example.ui.screens

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldLight,
    unfocusedBorderColor = DarkBorder,
    focusedLabelColor = GoldLight,
    unfocusedLabelColor = TextSilver,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = GoldLight,
    focusedContainerColor = DarkSurfaceElevated,
    unfocusedContainerColor = DarkSurfaceElevated
)
