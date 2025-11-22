package hr.foi.air.mshop.core.helpers

import com.auth0.android.jwt.JWT

object JWTHelper {
    fun getUserIdFromToken(token: String): String? {
        val jwt = JWT(token)
        return jwt.getClaim("user_id").asString()
    }

    fun getRoleFromToken(token: String): String? {
        val jwt = JWT(token)
        return jwt.getClaim("role").asString()
    }

    fun getOrgUuidFromToken(token: String): String? {
        val jwt = JWT(token)
        return jwt.getClaim("org_id").asString()
    }
}