package hr.foi.air.mshop.core.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hr.foi.air.mshop.core.room.entity.ConversationEntity
import hr.foi.air.mshop.core.room.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LLmChatDao {

    // Conversations
    @Insert
    suspend fun insertConversation(c: ConversationEntity): Long

    @Query("DELETE FROM conversations WHERE id = :cid")
    suspend fun deleteConversation(cid: Long)

    // Messages
    @Insert
    suspend fun insertMessage(m: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE conversationId = :cid ORDER BY createdAt ASC")
    suspend fun getMessages(cid: Long): List<MessageEntity>

    @Query("""
        SELECT c.id AS conversationId,
               m.text AS lastText,
               m.createdAt AS lastAt
        FROM conversations c
        JOIN messages m ON m.id = (
          SELECT id FROM messages
          WHERE conversationId = c.id
          ORDER BY createdAt DESC
          LIMIT 1
        )
        WHERE c.userId = :userId
        ORDER BY lastAt DESC
    """)
    suspend fun getConversationPreviews(userId: String): List<ConversationPreview>
}