package hr.foi.air.mshop.languagemodels

enum class AssistantIntent(
    val intent: String,
    val requiresLogin: Boolean = false,
    val isCritical: Boolean = false,
    val cancellationText: String? = null,
    val requiresLoginMessage: String? = null,
    val defaultUserFriendlyMessage: String? = null
) {
    VIEW_TRANSACTIONS(
        "VIEW_TRANSACTIONS",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli vidjeti popis transakcija. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za pregled transakcija. 🧾"
    ),
    VIEW_TRANSACTIONS_PERIOD(
        "VIEW_TRANSACTIONS_PERIOD",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli vidjeti popis transakcija. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za pregled transakcija i primijenio tražene filtre. 🧾"
    ),
    NEW_TRANSACTION(
        "NEW_TRANSACTION",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli inicirati novu transakciju. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za iniciranje nove transakcije. 🧾"
    ),
    LOGOUT(
        "LOGOUT",
        requiresLogin = true,
        isCritical = true,
        cancellationText = "Odjava otkazana ❌",
        requiresLoginMessage = "Niste prijavljeni pa Vas ne mogu odjaviti. ⚠️",
        defaultUserFriendlyMessage = "Pokrenuo sam proces odjave 🚪"
    ),
    WANTS_INFO("WANTS_INFO", defaultUserFriendlyMessage = null),
    RECOVERY_HINT_GET(
        "RECOVERY_HINT_GET",
        requiresLogin = false,
        defaultUserFriendlyMessage = "Provjeravam Vašu zabilješku o lokaciji koda... 🔍"
    ),
    UNKNOWN("UNKNOWN", defaultUserFriendlyMessage = "Nažalost nisam u potpunosti razumio Vaš zahtjev. 😅"),
    ERROR("LLM_ERROR", defaultUserFriendlyMessage = "❌ Greška u vezi s AI servisom.\nProvjerite vezu i pokušajte ponovno.")
    ;

    companion object {
        fun fromIntent(intent: String?): AssistantIntent {
            if (intent == null) return UNKNOWN
            return values().firstOrNull { it.intent == intent } ?: UNKNOWN
        }

        val allIds: List<String> by lazy { values().map { it.intent } }
        val availableIntentsString: String by lazy { allIds.joinToString(", ") { "\"$it\"" } }
    }
}
