package id.antasari.mentalityapp.data.local // Sesuaikan dengan nama package kamu

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    // Mengubah List<String> jadi JSON String (Supaya bisa masuk Database)
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    // Mengubah JSON String balik jadi List<String> (Supaya bisa dibaca Aplikasi)
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}