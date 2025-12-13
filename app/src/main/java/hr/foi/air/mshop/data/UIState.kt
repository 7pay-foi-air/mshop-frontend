package hr.foi.air.mshop.data

data class UIState(
    val loading: Boolean = false,
    val successMessage : String? = null,
    val errorMessage: String? = null
)