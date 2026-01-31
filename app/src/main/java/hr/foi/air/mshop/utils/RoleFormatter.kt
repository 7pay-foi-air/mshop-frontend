package hr.foi.air.mshop.utils

fun userRoleToHrLabel(role: String?): String =
    when (role) {
        "owner" -> "Vlasnik"
        "cashier" -> "Blagajnik"
        "admin" -> "Administrator"
        else -> "Nepoznata uloga"
    }