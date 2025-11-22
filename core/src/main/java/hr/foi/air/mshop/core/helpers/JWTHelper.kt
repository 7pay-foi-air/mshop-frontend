package hr.foi.air.mshop.core.helpers

import com.auth0.android.jwt.JWT

object JWTHelper {
    fun getOrgUuidFromToken(token: String): String? {
        val jwt = JWT(token)
        return jwt.getClaim("org_id").asString()
    }
}