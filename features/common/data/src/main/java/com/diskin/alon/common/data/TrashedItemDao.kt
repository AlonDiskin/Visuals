package com.diskin.alon.common.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface TrashedItemDao {

    @Query("SELECT * FROM trashed_items")
    fun getAll(): Observable<List<TrashedItemEntity>>

    @Insert
    fun insert(vararg item: TrashedItemEntity): Completable

    @Delete
    fun delete(vararg itemEntity: TrashedItemEntity): Completable
}