package hr.foi.air.mshop.core.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hr.foi.air.mshop.core.room.converters.LlmChatConverter
import hr.foi.air.mshop.core.room.dao.LLmChatDao
import hr.foi.air.mshop.core.room.entity.ConversationEntity
import hr.foi.air.mshop.core.room.entity.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 2
)
@TypeConverters(LlmChatConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun llmChatDao(): LLmChatDao
}