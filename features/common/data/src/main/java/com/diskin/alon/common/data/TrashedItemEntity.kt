package com.diskin.alon.common.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trashed_items")
data class TrashedItemEntity(
    @PrimaryKey val uri: String,
    val type: TrashedEntityType
)