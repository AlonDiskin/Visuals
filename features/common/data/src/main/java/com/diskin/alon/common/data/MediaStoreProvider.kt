package com.diskin.alon.common.data

import android.content.ContentResolver
import android.net.Uri
import io.reactivex.Observable

abstract class MediaStoreProvider<T : Any>(
    private val uri: Uri,
    private val contentResolver: ContentResolver
) {

    fun getAll(): Observable<List<T>> {
        return RxContentProvider.create(
            this::fetchFromStore,
            contentResolver,
            uri
        )
    }

    abstract fun fetchFromStore(): List<T>
}