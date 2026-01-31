package hr.foi.air.mshop.core.room.dao

data class ConversationPreview(
    val conversationId: Long,
    val lastText: String?,
    val lastAt: Long?
)