package com.diskin.alon.visuals.videos.featuretesting

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diskin.alon.common.data.Converters
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity

@Database(entities = [TrashedItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TestDatabase : RoomDatabase(){
    abstract fun trashedDao(): TrashedItemDao
}