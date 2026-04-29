package si.uni_lj.fe.libri.data.repository

import si.uni_lj.fe.libri.data.api.BookApiService
import si.uni_lj.fe.libri.data.api.Doc
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails

class BookRepository(private val apiService: BookApiService) {
    suspend fun searchBooks(query: String): List<Doc> {
        return try {
            val response = apiService.searchBooks(query)
            response.docs ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBookDetails(workId: String): OpenLibraryWorkDetails? {
        return try {
            apiService.getBookDetails(workId)
        } catch (e: Exception) {
            null
        }
    }
}