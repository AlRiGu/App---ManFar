package com.example.shared.util

data class BookingDateOption(
    val isoDate: String,
    val dayOfWeek: String,
    val dayNumber: String,
    val monthName: String,
    val fullLabel: String
)

// Generate 14 days starting from a reference date or current base
fun generateBookingDays(startDateIso: String = "2026-08-31"): List<BookingDateOption> {
    val dayNames = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    val monthNames = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

    // Parse start date: YYYY-MM-DD
    val parts = startDateIso.split("-")
    var year = parts.getOrNull(0)?.toIntOrNull() ?: 2026
    var month = parts.getOrNull(1)?.toIntOrNull() ?: 8
    var day = parts.getOrNull(2)?.toIntOrNull() ?: 31

    val daysInMonth = mapOf(
        1 to 31, 2 to 28, 3 to 31, 4 to 30, 5 to 31, 6 to 30,
        7 to 31, 8 to 31, 9 to 30, 10 to 31, 11 to 30, 12 to 31
    )

    fun isLeapYear(y: Int) = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

    fun maxDays(m: Int, y: Int): Int {
        return if (m == 2 && isLeapYear(y)) 29 else daysInMonth[m] ?: 30
    }

    // Sakamoto's algorithm for day of week (0 = Sunday, 1 = Monday, etc.)
    fun dayOfWeekIndex(y: Int, m: Int, d: Int): Int {
        val t = listOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        val adjustedYear = if (m < 3) y - 1 else y
        return (adjustedYear + adjustedYear / 4 - adjustedYear / 100 + adjustedYear / 400 + t[m - 1] + d) % 7
    }

    val result = mutableListOf<BookingDateOption>()
    for (i in 0 until 14) {
        val yStr = year.toString()
        val mStr = month.toString().padStart(2, '0')
        val dStr = day.toString().padStart(2, '0')
        val iso = "$yStr-$mStr-$dStr"

        val dowIdx = dayOfWeekIndex(year, month, day)
        val dow = dayNames[dowIdx]
        val mon = monthNames[(month - 1).coerceIn(0, 11)]
        val label = "$dow $dStr $mon"

        result.add(
            BookingDateOption(
                isoDate = iso,
                dayOfWeek = dow,
                dayNumber = dStr,
                monthName = mon,
                fullLabel = label
            )
        )

        // Advance 1 day
        day++
        if (day > maxDays(month, year)) {
            day = 1
            month++
            if (month > 12) {
                month = 1
                year++
            }
        }
    }
    return result
}
