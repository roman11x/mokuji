package io.github.roman11x.mokuji.data.network

import io.github.roman11x.mokuji.data.Manga
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ToMangaTest {
    @Test
    fun `returns manga with all fields present`() {
        val media = searchMediaHelper()
        val manga = Manga(
            id = 1,
            title = "User Preferred",
            coverUrl = "COVER_URL",
            format = "FORMAT",
            year = 2023
        )
        assertEquals(manga, media.toManga())
    }

    @Test
    fun `returns userPreferred`() {
        val media = searchMediaHelper(title = titleHelper())
        val title = "User Preferred"
        assertEquals(title, media.toManga()?.title)
    }

    @Test
    fun `returns Romanji with userPreferred missing`() {
        val media = searchMediaHelper(title = titleHelper(userPreferred = null))
        val title = "Romaji"
        assertEquals(title, media.toManga()?.title)
    }

    @Test
    fun `returns English with userPreferred and romaji missing`() {
        val media = searchMediaHelper(title = titleHelper(userPreferred = null, romaji = null))
        val title = "English"
        assertEquals(title, media.toManga()?.title)
    }

    @Test
    fun `returns Native with userPreferred, romaji, and english missing`() {
        val media = searchMediaHelper(
            title = titleHelper(
                userPreferred = null,
                romaji = null,
                english = null
            )
        )
        val title = "Native"
        assertEquals(title, media.toManga()?.title)
    }

    @Test
    fun `returns Null if no title is present`() {
        val media = searchMediaHelper(title = null)
        assertNull(media.toManga())
    }

    @Test
    fun `returns Null if title fields are null`() {
        val media = searchMediaHelper(title = Title(null, null, null, null))
        assertNull(media.toManga())
    }

    @Test
    fun `returns null coverUrl if coverImage is null`() {
        val media = searchMediaHelper(coverImage = null)
        assertNull(media.toManga()?.coverUrl)
    }

    @Test
    fun `returns null year if startDate is null`() {
        val media = searchMediaHelper(startDate = null)
        assertNull(media.toManga()?.year)
    }

    private fun titleHelper(
        userPreferred: String? = "User Preferred",
        romaji: String? = "Romaji",
        english: String? = "English",
        native: String? = "Native"
    ): Title =
        Title(userPreferred = userPreferred, romaji = romaji, english = english, native = native)


    // helper function to create per test SearchMedia
    private fun searchMediaHelper(
        id: Int = 1,
        title: Title? = Title("User Preferred", "Romaji", "English", "Native"),
        format: String? = "FORMAT",
        startDate: StartDate? = StartDate(2023),
        coverImage: CoverImage? = CoverImage("COVER_URL")
    ): SearchMedia =
        SearchMedia(
            id = id,
            title = title,
            format = format,
            startDate = startDate,
            coverImage = coverImage
        )
}
