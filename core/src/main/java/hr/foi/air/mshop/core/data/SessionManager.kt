package hr.foi.air.mshop.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hr.foi.air.mshop.core.helpers.JWTHelper

object SessionManager {
    var currentUserId: String? by mutableStateOf(null)
        private set
    var currentUserRole: String? by mutableStateOf(null)
        private set
    var currentOrgId: String? by mutableStateOf(null)
        private set
    var accessToken: String? by mutableStateOf(null)
        private set

    fun startSession(token: String) {
        accessToken = token
        currentUserId = JWTHelper.getUserIdFromToken(token)
        currentUserRole = JWTHelper.getRoleFromToken(token)
        currentOrgId = JWTHelper.getOrgUuidFromToken(token)
    }

    fun endSession() {
        currentUserId = null
        currentUserRole = null
        currentOrgId = null
        accessToken = null
    }
}