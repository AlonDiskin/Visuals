package com.diskin.alon.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TrashedItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TestDatabase : RoomDatabase(){
    abstract fun trashedDao(): TrashedItemDao
}