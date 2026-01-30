package hr.foi.air.mshop.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.toHrCurrency(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("hr", "HR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(this)
}