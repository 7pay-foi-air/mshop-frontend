package hr.foi.air.mshop.repo

import hr.foi.air.ws.NetworkService
import hr.foi.air.ws.models.userManagement.AddUserRequest
import org.json.JSONObject

class UserRepo {
    private val api = NetworkService.accountApi

    suspend fun addUser(req: AddUserRequest): Result<String>{
        return try{
            val res = api.createUser(req)
            if(res.isSuccessful){
                Result.success(res.body()?.message ?: "Uspješno dodano")
            }else{
                val errorJson = res.errorBody()?.string()
                val msg = extractErrorMessage(errorJson)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(json: String?): String {
        if (json.isNullOrBlank()) return "Dogodila se greška"

        return try {
            val root = JSONObject(json)

            if (root.has("errors")) {
                val arr = root.getJSONArray("errors")
                if (arr.length() > 0) {
                    val firstError = arr.getJSONObject(0)
                    firstError.getString("message")
                } else "Dogodila se greška"
            }

            else if (root.has("error")) {
                root.getString("error")
            }
            else "Dogodila se greška"
        } catch (_: Exception) {
            "Dogodila se greška"
        }
    }
}