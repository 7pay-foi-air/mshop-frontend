package hr.foi.air.mshop.core.room.converters

import androidx.room.TypeConverter
import hr.foi.air.mshop.core.room.entity.Sender

class LlmChatConverter {
    @TypeConverter
    fun senderToString(s: Sender) = s.name
    @TypeConverter fun stringToSender(s: String) = Sender.valueOf(s)
}