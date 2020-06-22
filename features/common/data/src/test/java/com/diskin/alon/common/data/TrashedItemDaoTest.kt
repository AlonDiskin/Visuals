package com.diskin.alon.common.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [TrashedItemDao] integration test class.
 */
@RunWith(AndroidJUnit4::class)
class TrashedItemDaoTest {

    private lateinit var db: TestDatabase
    private lateinit var dao: TrashedItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.trashedDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun writeTrashedItemsAndReadInList() {
        // Given an initialized trashed items

        val items = listOf(
            TrashedItemEntity("test uri 1",TrashedEntityType.VIDEO),
            TrashedItemEntity("test uri 2",TrashedEntityType.PICTURE)
        )

        // When item is inserted to dao
        dao.insert(*items.toTypedArray()).blockingAwait()

        // Then reading all items from dao should return inserted items
        assertThat(dao.getAll().blockingFirst()).isEqualTo(items)
    }

    @Test
    fun deleteAllByUriWithTransaction() {
        // Given an initialized trashed items

        val items = listOf(
            TrashedItemEntity("test uri 1",TrashedEntityType.VIDEO),
            TrashedItemEntity("test uri 2",TrashedEntityType.PICTURE)
        )

        // When item is inserted to dao
        dao.insert(*items.toTypedArray()).blockingAwait()

        // When items deleted by uri
        dao.deleteAllByUri(listOf("test uri 1", "test uri 2"))

        assertThat(dao.getAll().blockingFirst().size).isEqualTo(0)
    }
}