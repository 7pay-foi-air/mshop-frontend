package hr.foi.air.mshop.utils

import kotlin.random.Random
import java.util.Calendar

fun randomCardNumber(): String {
    return List(4) {
        Random.nextInt(1000, 9999)
    }.joinToString(" ")
}

fun randomExpiry(): String {
    val month = Random.nextInt(1, 13)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
    val year = Random.nextInt(currentYear + 1, currentYear + 6)

    return String.format("%02d/%02d", month, year)
}

fun randomCvc(): String {
    return Random.nextInt(100, 999).toString()
}
