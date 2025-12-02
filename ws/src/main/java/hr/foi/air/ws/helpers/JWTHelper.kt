package hr.foi.air.ws.helpers

import com.auth0.android.jwt.JWT

object JWTHelper {
    fun getUserIdFromToken(token: String?): String? {
        val jwt = token?.let { JWT(it) }
        return jwt?.getClaim("user_id")?.asString()
    }

    fun getRoleFromToken(token: String?): String? {
        val jwt = token?.let { JWT(it) }
        return jwt?.getClaim("role")?.asString()
    }

    fun getOrgUuidFromToken(token: String?): String? {
        val jwt = token?.let { JWT(it) }
        return jwt?.getClaim("org_id")?.asString()
    }
}