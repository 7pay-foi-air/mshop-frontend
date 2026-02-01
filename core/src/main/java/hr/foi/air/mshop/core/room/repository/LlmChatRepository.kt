package hr.foi.air.mshop.core.room.repository

import hr.foi.air.mshop.core.room.dao.LLmChatDao
import hr.foi.air.mshop.core.room.entity.ConversationEntity
import hr.foi.air.mshop.core.room.entity.MessageEntity
import hr.foi.air.mshop.core.room.entity.Sender

class LlmChatRepository(private val dao: LLmChatDao) {

    suspend fun createConversation(userId: String): Long =  dao.insertConversation(ConversationEntity(userId = userId))

    suspend fun insertUser(cid: Long, text: String): Long =
        dao.insertMessage(
            MessageEntity(
                conversationId = cid,
                sender = Sender.User,
                text = text
            )
        )

    suspend fun insertBot(cid: Long, text: String): Long =
        dao.insertMessage(
            MessageEntity(
                conversationId = cid,
                sender = Sender.Bot,
                text = text
            )
        )

    suspend fun getMessages(cid: Long) = dao.getMessages(cid)

    suspend fun getConversationPreviews(userId: String) = dao.getConversationPreviews(userId)

    suspend fun deleteConversation(cid: Long) = dao.deleteConversation(cid)
}