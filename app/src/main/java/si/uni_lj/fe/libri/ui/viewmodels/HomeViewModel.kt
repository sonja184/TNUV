package si.uni_lj.fe.libri.ui.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import si.uni_lj.fe.libri.data.api.Doc
import si.uni_lj.fe.libri.data.repository.BookRepository

class HomeViewModel(private val repository: BookRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val genreBooks = mutableStateMapOf<String, List<Doc>>()
    
    var searchQuery by mutableStateOf("")
    var searchResults by mutableStateOf<List<Doc>>(emptyList())
    var isSearching by mutableStateOf(false)

    val genres = listOf(
        "Fiction", "Romance", "Mystery", "Science Fiction", "History", "Fantasy", "Biography"
    )

    init {
        // Sync load from cache immediately so UI has data during the first frame
        loadFromCache()
        // Refresh in background
        loadAllGenres()
    }

    private fun loadFromCache() {
        genres.forEach { genre ->
            val query = "subject:${genre.lowercase().replace(" ", "_")}"
            repository.getCachedSearch(query)?.let { cachedBooks ->
                genreBooks[genre] = cachedBooks
            }
        }
    }

    fun loadAllGenres(forceRefresh: Boolean = false) {
        // Only show full loading spinner if we have no data at all
        val needsFullLoading = genreBooks.isEmpty() || forceRefresh

        viewModelScope.launch {
            if (needsFullLoading) _isLoading.value = true
            try {
                val results = genres.map { genre ->
                    async {
                        val query = "subject:${genre.lowercase().replace(" ", "_")}"
                        val books = repository.searchBooks(query)
                        genre to books
                    }
                }.awaitAll()
                
                results.forEach { (genre, books) ->
                    genreBooks[genre] = books
                }
            } finally {
                if (needsFullLoading) _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isBlank()) {
            searchResults = emptyList()
            isSearching = false
        } else {
            performSearch(newQuery)
        }
    }

    private fun performSearch(query: String) {
        // Check cache first for instant search results
        val cached = repository.getCachedSearch(query)
        if (cached != null) {
            searchResults = cached
            isSearching = false
            return
        }

        viewModelScope.launch {
            isSearching = true
            searchResults = repository.searchBooks(query)
            isSearching = false
        }
    }
}