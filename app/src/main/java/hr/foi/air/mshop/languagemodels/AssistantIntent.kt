package hr.foi.air.mshop.languagemodels

enum class AssistantIntent(
    val intent: String,
    val requiresLogin: Boolean = false,
    val requiresAdmin: Boolean = false,
    val isCritical: Boolean = false,
    val cancellationText: String? = null,
    val requiresLoginMessage: String? = null,
    val requiresAdminMessage: String? = null,
    val defaultUserFriendlyMessage: String? = null
) {
    VIEW_TRANSACTIONS(
        "VIEW_TRANSACTIONS",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli vidjeti popis transakcija. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za pregled transakcija. 🧾"
    ),

    VIEW_TRANSACTIONS_LAST(
        "VIEW_TRANSACTIONS_LAST",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli pregledati transakcije. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za pregled transakcija i primijenio tražene filtre. 🧾"
    ),

    VIEW_TRANSACTIONS_RANGE(
        "VIEW_TRANSACTIONS_RANGE",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli pregledati transakcije. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za pregled transakcija i primijenio tražene filtre. 🧾"
    ),

    MANAGE_USERS(
        "MANAGE_USERS",
        requiresLogin = true,
        requiresAdmin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli upravljati korisnicima. ⚠️",
        requiresAdminMessage = "Morate imati administratorske ovlasti kako bi mogli upravljati korisnicima. 🔒",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za upravljanje korisnicima. 👩‍💼‍👨‍💼"
    ),

    MANAGE_ITEMS(
        "MANAGE_ITEMS",
        requiresLogin = true,
        requiresAdmin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli upravljati artiklima. ⚠️",
        requiresAdminMessage = "Morate imati administratorske ovlasti kako bi mogli upravljati artiklima. 🔒",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za upravljanje artiklima. 📦"
    ),

    EDIT_PROFILE(
        "EDIT_PROFILE",
        requiresLogin = true,
        requiresLoginMessage = "Morate biti prijavljeni kako biste mogli uređivati svoj korisnički račun. ⚠️",
        defaultUserFriendlyMessage = "Prebacio sam Vas na stranicu za uređivativanje svog korisničkog računa. 🧑‍💼"
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
        defaultUserFriendlyMessage = null,
        requiresLogin = true,
        requiresLoginMessage = "Kako biste vidjeli lokaciju Vašeg koda za oporavak, morate unijeti vaše korisničko ime u zaslonu za prijavu -> otići na Zaboravili ste lozinku? -> otići na Zaboravili ste kod za oporavak? te odgovoriti na 3 sigurnosna pitanja sa vlastitim odgovorima. 😊"

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
