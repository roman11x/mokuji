package io.github.roman11x.mokuji.data.network

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class DecodingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `successful reply decodes its data into MangaReply`() {
        val text = """
        {
          "data": {
            "Page": {
              "media": [
                {
                  "id": 85737,
                  "title": {
                    "userPreferred": "Re:Zero kara Hajimeru Isekai Seikatsu",
                    "romaji": "Re:Zero kara Hajimeru Isekai Seikatsu",
                    "english": "Re:ZERO -Starting Life in Another World-",
                    "native": "Re:ゼロから始める異世界生活"
                  },
                  "format": "NOVEL",
                  "startDate": {
                    "year": 2014
                  },
                  "coverImage": {
                    "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx85737-WkWOr5EgwPyo.jpg"
                  }
                },
                {
                  "id": 87259,
                  "title": {
                    "userPreferred": "Re:Zero kara Hajimeru Isekai Seikatsu: Dai-3 Shou - Truth of Zero",
                    "romaji": "Re:Zero kara Hajimeru Isekai Seikatsu: Dai-3 Shou - Truth of Zero",
                    "english": "Re:ZERO -Starting Life in Another World- Chapter 3: Truth of Zero",
                    "native": "Re:ゼロから始める異世界生活 第三章 Truth of Zero"
                  },
                  "format": "MANGA",
                  "startDate": {
                    "year": 2015
                  },
                  "coverImage": {
                    "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/nx87259-yJMWileGmcl5.jpg"
                  }
                }
              ]
            }
          }
        }
    """
        val reply = json.decodeFromString<MangaReply>(text)
        assertEquals(85737, reply.data?.page?.media?.get(0)?.id)
        assertEquals("Re:Zero kara Hajimeru Isekai Seikatsu", reply.data?.page?.media?.get(0)?.title?.romaji)

    }

    @Test
    fun `error reply decodes its message into errors`() {
        val text = """
            {
              "errors": [
                {
                  "message": "Cannot query field \"formatt\" on type \"Media\". Did you mean \"format\"?",
                  "status": 400,
                  "locations": [
                    {
                      "line": 11,
                      "column": 7
                    }
                  ]
                }
              ],
              "data": null
            }
        """
        val reply = json.decodeFromString<MangaReply>(text)
        assertEquals(
            "Cannot query field \"formatt\" on type \"Media\". Did you mean \"format\"?",
            reply.errors[0].message
        )
    }
}
