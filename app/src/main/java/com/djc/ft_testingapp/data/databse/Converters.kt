package com.djc.ft_testingapp.data.databse

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromFloatArray(values: FloatArray?): String? {
        // Convierte el array a una cadena separada por comas
        return values?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toFloatArray(data: String?): FloatArray? {
        // Si la cadena es nula o vacía, se retorna null (o puedes retornar un array vacío, según tu lógica)
        if (data.isNullOrEmpty()) return null
        // Separamos la cadena en una lista de cadenas y convertimos cada una a Float
        return data.split(",").map { it.toFloat() }.toFloatArray()
    }
}