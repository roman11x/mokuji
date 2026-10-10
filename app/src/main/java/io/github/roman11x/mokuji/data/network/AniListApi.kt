package io.github.roman11x.mokuji.data.network

import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

// the query string for the Anilist API
private const val SEARCH_QUERY = $$"""
query($search: String) {
  Page(perPage: 20) {
    media(search: $search, type: MANGA, isAdult: false) {
      id
      title {
        userPreferred
        romaji
        english
        native
      }
      format 
      startDate {
        year
      }
      coverImage {
        large
      }
    }
  }
}
"""

// we need to convert the query and the variables to JSON
// so that the API can understand them
@Serializable
data class SearchVariables(
    val search: String
)

@Serializable
data class SearchRequest(
    val query: String, // the query string
    val variables: SearchVariables // the variables for the query
)

private const val ANILIST_URL =
    "https://graphql.anilist.co"   // AniList's single GraphQL address; every request is a POST here

// class for sending the POST to the API
class AniListApi(private val httpClient: HttpClient) {
    suspend fun searchManga(search: String): MangaReply {
        val reply: MangaReply = httpClient.post(ANILIST_URL) { //send a POST
            contentType(ContentType.Application.Json) //we're sending a Json
            setBody(
                SearchRequest(
                    query = SEARCH_QUERY,
                    variables = SearchVariables(search = search)
                )
            )
        }.body() //get the body of the reply
        return reply
    }
}
