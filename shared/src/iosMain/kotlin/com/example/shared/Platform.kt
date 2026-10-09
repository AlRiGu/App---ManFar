package com.example.shared

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class IOSPlatform : Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
