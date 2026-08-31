package com.example.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.example.shared.ui.SharedApp
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    SharedApp()
}
