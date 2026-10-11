package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MangaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ChillReader", appName)
    }

    @Test
    fun `repository has titles and telegram feed`() {
        val repo = MangaRepository()
        val titles = repo.titles.value
        val feed = repo.telegramFeed.value

        assertTrue(titles.isNotEmpty())
        assertTrue(feed.isNotEmpty())
        assertNotNull(titles.find { it.id == "solo_monarch" })
    }

    @Test
    fun `head nod increments stats`() {
        val repo = MangaRepository()
        val initialNods = repo.stats.value.headNodsDetected
        repo.incrementHeadNodCount()
        assertEquals(initialNods + 1, repo.stats.value.headNodsDetected)
    }
}
