package si.uni_lj.fe.libri.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): OpenLibrarySearchResponse

    @GET("works/{workId}.json")
    suspend fun getBookDetails(
        @Path("workId") workId: String
    ): OpenLibraryWorkDetails

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}

data class OpenLibrarySearchResponse(
    val docs: List<Doc>?
)

data class Doc(
    val key: String, // e.g., "/works/OL1234W"
    val title: String,
    val author_name: List<String>?,
    val cover_i: Int?,
    val first_publish_year: Int?
) {
    val id: String get() = key.removePrefix("/works/")
    val thumbnailUrl: String? get() = cover_i?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
}

data class OpenLibraryWorkDetails(
    val key: String?,
    val title: String,
    val description: Any?, // Can be String or Map {"type": "/type/text", "value": "..."}
    val covers: List<Int>?,
    val authors: List<AuthorRole>?
) {
    val descriptionText: String? get() = when (description) {
        is String -> description
        is Map<*, *> -> description["value"] as? String
        else -> null
    }
    val coverUrl: String? get() = covers?.firstOrNull()?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
}

data class AuthorRole(
    val author: AuthorKey
)

data class AuthorKey(
    val key: String
)
