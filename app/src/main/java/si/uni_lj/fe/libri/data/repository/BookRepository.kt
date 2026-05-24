package si.uni_lj.fe.libri.data.repository

import si.uni_lj.fe.libri.data.api.BookApiService
import si.uni_lj.fe.libri.data.api.Doc
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails

class BookRepository(private val apiService: BookApiService) {
    
    companion object {
        private val searchCache = mutableMapOf<String, List<Doc>>()
        private val detailsCache = mutableMapOf<String, OpenLibraryWorkDetails>()
    }

    suspend fun searchBooks(query: String, forceRefresh: Boolean = false): List<Doc> {
        if (!forceRefresh) {
            searchCache[query]?.let { return it }
        }

        return try {
            val response = apiService.searchBooks(query)
            val docs = response.docs ?: emptyList()
            searchCache[query] = docs
            docs
        } catch (e: Exception) {
            searchCache[query] ?: emptyList()
        }
    }
    
    fun getCachedSearch(query: String): List<Doc>? = searchCache[query]

    fun findDocInCache(workId: String): Doc? {
        return searchCache.values.flatten().find { it.id == workId }
    }

    suspend fun getBookDetails(workId: String): OpenLibraryWorkDetails? {
        detailsCache[workId]?.let { return it }

        return try {
            val details = apiService.getBookDetails(workId)
            detailsCache[workId] = details
            details
        } catch (e: Exception) {
            null
        }
    }

    fun getCachedBookDetails(workId: String): OpenLibraryWorkDetails? = detailsCache[workId]
    
    fun clearCache() {
        searchCache.clear()
        detailsCache.clear()
    }
}
