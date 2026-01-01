package hr.foi.air.mshop.core.repository

import android.content.Context
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.core.models.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getAllUsers(): Flow<List<User>>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun addUser(user: User, context: Context): Result<String>
    suspend fun updateUser(user: User, context: Context): Result<String>
}