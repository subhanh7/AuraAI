package com.example.auraai.data.local.room.converter

import androidx.room.TypeConverter
import com.example.auraai.domain.model.MessageMeta
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MessageMetaConverter {
    @TypeConverter
    fun fromMessageMeta(value: MessageMeta?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMessageMeta(value: String?): MessageMeta? {
        return value?.let { Json.decodeFromString(it) }
    }
}
