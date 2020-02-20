package com.diskin.alon.visuals.photos.data

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * [RxContentProvider] unit test class.
 */
class RxContentProviderTest {

    @Mock
    lateinit var contentResolver: ContentResolver
    @Mock
    lateinit var fetch: () -> Any
    @Mock
    lateinit var uri: Uri

    private val observerCaptor = argumentCaptor<ContentObserver>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Capture content observer upon observer registration
        whenever(contentResolver.registerContentObserver(eq(uri), eq(true), observerCaptor.capture()))
            .doAnswer {  }
    }

    @Test
    fun registerContentObserver_whenCreated() {
        // Given an initialized factory

        // When content observable is created and subscribed
        RxContentProvider.create(fetch,contentResolver,uri).test()

        // Then observable should register a content observer to content provider
        verify(contentResolver).registerContentObserver(
            eq(uri),
            eq(true),
            eq(observerCaptor.firstValue))
    }

    @Test
    fun triggerContentFetch_whenCreated() {
        // Test case fixture
        val testContent = "content"

        whenever(fetch.invoke()).doReturn(testContent)

        // Given an initialized factory

        // When content observable is created and subscribed
        val testObserver = RxContentProvider.create(fetch,contentResolver,uri).test()

        // Then content resolver should ask provider to notify observer
        verify(contentResolver).notifyChange(eq(uri), eq(null))

        // When content observer is notified
        observerCaptor.firstValue.onChange(true)

        // Then observable should invoke content fetch function
        verify(fetch).invoke()

        // And emit result to subscribed observers
        testObserver.assertValue(testContent)
    }

    @Test
    fun unregisterContentObserver_whenCancelled() {
        // Given an initialized factory

        // When content observable is created and subscribed
        val subscription = RxContentProvider.create(fetch,contentResolver,uri).test()

        // And in later point subscription is cancelled
        subscription.cancel()

        // Then observable should unregister content observer from content provider
        verify(contentResolver).unregisterContentObserver(any())
    }

    @Test
    fun emitContent_whenContentObserverNotified() {
        // Test case fixture
        val testContent = "content"

        whenever(fetch.invoke()).doReturn(testContent)

        // Given an initialized factory

        // When content observable is created and subscribed
        val testObserver = RxContentProvider.create(fetch,contentResolver,uri).test()

        // And content observer is updated
        observerCaptor.firstValue.onChange(true)

        // Then observable should invoke content fetch function
        verify(fetch).invoke()

        // And emit result to subscribed observers
        testObserver.assertValue(testContent)
    }
}