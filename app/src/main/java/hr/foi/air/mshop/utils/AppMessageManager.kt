package hr.foi.air.mshop.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppMessageManager {

    private val _message = MutableStateFlow<UiMessage?>(null)
    val message: StateFlow<UiMessage?> = _message

    fun show(text: String, type: AppMessageType) {
        _message.value = UiMessage(text, type)
    }

    fun clear() {
        _message.value = null
    }
}