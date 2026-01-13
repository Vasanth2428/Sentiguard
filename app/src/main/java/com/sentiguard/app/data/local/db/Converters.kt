package com.sentiguard.app.data.local.db

import androidx.room.TypeConverter
import com.sentiguard.app.domain.model.EventType
import com.sentiguard.app.domain.model.RiskLevel
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromRiskLevel(value: RiskLevel): String {
        return value.name
    }

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel {
        return try {
            RiskLevel.valueOf(value)
        } catch (e: Exception) {
            RiskLevel.SAFE // Fallback
        }
    }

    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return try {
            EventType.valueOf(value)
        } catch (e: Exception) {
            EventType.USER_ALERT // Fallback
        }
    }

    @TypeConverter
    fun fromMap(value: Map<String, String>): String {
        return JSONObject(value).toString()
    }

    @TypeConverter
    fun toMap(value: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (value.isEmpty()) return map
        try {
            val jsonObject = JSONObject(value)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = jsonObject.getString(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }
}
