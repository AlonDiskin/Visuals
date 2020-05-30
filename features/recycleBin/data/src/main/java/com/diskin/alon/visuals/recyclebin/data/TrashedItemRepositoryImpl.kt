package com.diskin.alon.visuals.recyclebin.data

import android.net.Uri
import com.diskin.alon.common.data.DeviceMediaProvider
import com.diskin.alon.common.data.TrashedEntityType
import com.diskin.alon.common.data.TrashedItemDao
import com.diskin.alon.common.data.TrashedItemEntity
import com.diskin.alon.visuals.recuclebin.presentation.TrashedFilter
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItem
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemRepository
import com.diskin.alon.visuals.recuclebin.presentation.TrashedItemType
import io.reactivex.Observable
import javax.inject.Inject

/**
 * [TrashedItemRepository] implementation
 */
class TrashedItemRepositoryImpl @Inject constructor(
    private val dao: TrashedItemDao,
    private val mediaVisualProvider: DeviceMediaProvider<MediaStoreVisual>
) : TrashedItemRepository {

    override fun getAll(filter: TrashedFilter): Observable<List<TrashedItem>> {
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
                    TrashedItem(
                        Uri.parse(entity.uri),
                        when(entity.type) {
                            TrashedEntityType.PICTURE -> TrashedItemType.PICTURE
                            TrashedEntityType.VIDEO -> TrashedItemType.VIDEO
                        }
                    )
                }
                .reversed()
            }
    }
}