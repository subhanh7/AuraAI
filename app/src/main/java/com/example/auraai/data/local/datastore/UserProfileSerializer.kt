package com.example.auraai.data.local.datastore

import androidx.datastore.core.Serializer
import com.example.auraai.domain.model.UserProfile
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserProfileSerializer : Serializer<UserProfile> {
    override val defaultValue: UserProfile = UserProfile()

    override suspend fun readFrom(input: InputStream): UserProfile {
        return try {
            Json.decodeFromString(
                deserializer = UserProfile.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserProfile, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = UserProfile.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
