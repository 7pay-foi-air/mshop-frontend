package hr.foi.air.mshop.viewmodels.userManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.core.repository.IUserRepository
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.ws.repository.UserRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val userRepository: IUserRepository = UserRepo()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _deletedUserIds = MutableStateFlow<Set<String>>(emptySet())

    private val _userToEdit = MutableStateFlow<User?>(null)

    val userToEdit: StateFlow<User?> = _userToEdit.asStateFlow()
    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredUsers: StateFlow<List<User>> = combine(
        _searchQuery,
        _refreshTrigger.flatMapLatest { userRepository.getAllUsers() },
        _deletedUserIds
    ) { query, users, deletedIds ->
        val usersToShow = users.filter { it.uuidUser !in deletedIds }
        if (query.isBlank()) {
            usersToShow
        } else {
            usersToShow.filter {
                        it.firstName.contains(query, ignoreCase = true) ||
                        it.lastName.contains(query, ignoreCase = true) ||
                        it.username.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun refreshUsers() {
        _refreshTrigger.value++
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onStartEditUser(user: User) {
        _userToEdit.value = user
    }

    fun onFinishEditUser() {
        _userToEdit.value = null
    }

    fun onDeleteUser(user: User) {
        val userId = user.uuidUser ?: return
        viewModelScope.launch {
            val result = userRepository.deleteUser(userId)
            if (result.isSuccess) {
                _deletedUserIds.value += userId
                AppMessageManager.show("Korisnik uspješno obrisan.", AppMessageType.SUCCESS)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Greška pri brisanju."
                AppMessageManager.show(errorMsg, AppMessageType.ERROR)
            }
        }
    }
}