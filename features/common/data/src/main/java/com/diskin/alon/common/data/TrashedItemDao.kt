package com.diskin.alon.common.data

import androidx.room.*
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

    @Query("DELETE FROM trashed_items WHERE uri = :uri")
    fun deleteByUri(uri: String)

    @Transaction
    fun deleteAllByUri(uris: List<String>) { uris.forEach { deleteByUri(it) } }
}