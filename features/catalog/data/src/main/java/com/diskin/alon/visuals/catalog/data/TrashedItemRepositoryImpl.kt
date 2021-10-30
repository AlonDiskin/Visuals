package com.diskin.alon.visuals.catalog.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.catalog.presentation.model.TrashedFilter
import com.diskin.alon.visuals.catalog.presentation.model.TrashItem
import com.diskin.alon.visuals.catalog.presentation.interfaces.TrashItemRepository
import com.diskin.alon.visuals.catalog.presentation.model.TrashItemType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * [TrashItemRepository] implementation
 */
class TrashedItemRepositoryImpl @Inject constructor(
    private val dao: TrashedItemDao,
    private val mediaVisualProvider: DeviceMediaProvider<MediaStoreVisual>
) : TrashItemRepository {

    override fun getAll(filter: TrashedFilter): Observable<List<TrashItem>> {
        val deviceMediaObservable = mediaVisualProvider.getAll()
            .map { storeVisuals ->
                // if any existing entity in dao,not existing on device,
                // remove that item from dao
                val removed = mutableListOf<TrashedItemEntity>()
                val current = dao.getAll().blockingFirst()!!

                current.forEach {
                    if (!storeVisuals.contains(MediaStoreVisual(Uri.parse(it.uri)))) {
                        removed.add(it)
                    }
                }

                if (removed.isNotEmpty()) {
                    dao.delete(*removed.toTypedArray()).blockingAwait()
                }
            }

        return Observable.merge(
            deviceMediaObservable.ignoreElements().toObservable(),
            dao.getAll())
            .map { it
                .filter { entity ->
                    when(filter) {
                        TrashedFilter.ALL -> true
                        TrashedFilter.PICTURE -> entity.type == TrashedEntityType.PICTURE
                        TrashedFilter.VIDEO -> entity.type == TrashedEntityType.VIDEO
                    }
                }
                .map { entity ->
                    TrashItem(
                        Uri.parse(entity.uri),
                        when (entity.type) {
                            TrashedEntityType.PICTURE -> TrashItemType.PICTURE
                            TrashedEntityType.VIDEO -> TrashItemType.VIDEO
                        }
                    )
                }
                .reversed()
            }
    }

    override fun restore(items: List<Uri>): Completable {
        return Completable
            .fromCallable { dao.deleteAllByUri(items.map { it.toString() }) }
            .subscribeOn(Schedulers.io())
    }

    override fun restoreAll(): Completable {
        return Completable.fromCallable {
            val allUri = dao.getAll().blockingFirst().map { it.uri }
            dao.deleteAllByUri(allUri) }
            .subscribeOn(Schedulers.io())
    }
}