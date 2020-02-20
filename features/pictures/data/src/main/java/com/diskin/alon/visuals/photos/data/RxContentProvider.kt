package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import io.reactivex.Observable

/**
 * Factory class the creates observables for platform content providers.
 */
object RxContentProvider {

    /**
     *  Creates an [Observable] that will emmit the current content of the provider and
     *  invoke [fetch] each time the content provider has an update.
     *
     *  @param T fetched content data type.
     *  @param fetch the function to invoke to retrieve content from provide.
     *  @param contentResolver application content resolver instance.
     *  @param contentUri content provider uri.
     */
    fun <T : Any> create(fetch: () -> T,
                         contentResolver: ContentResolver,
                         contentUri: Uri
    ): Observable<T> {
        return Observable.create<T> { emitter ->
            // Initialize content observer
            val contentObserver = object : ContentObserver(null) {

                override fun onChange(selfChange: Boolean) {
                    // Emit content down stream  when observer notified by provider
                    if (!emitter.isDisposed) {
                        emitter.onNext(fetch.invoke())
                    }
                }
            }

            // Register observer to content provider
            contentResolver.registerContentObserver(
                contentUri,
                true,
                contentObserver)

            // Unregister observer from content provider upon this observable cancellation
            emitter.setCancellable { contentResolver.unregisterContentObserver(contentObserver) }

            // Trigger initial content fetch
            contentResolver.notifyChange(contentUri,null)
        }
    }
}