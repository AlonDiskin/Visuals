package com.diskin.alon.common.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromIntToTrashedType(value: Int): TrashedEntityType {
        return TrashedEntityType.values()[value]
    }

    @TypeConverter
    fun trashedTypeToInt(type: TrashedEntityType): Int {
        return type.ordinal
    }
}