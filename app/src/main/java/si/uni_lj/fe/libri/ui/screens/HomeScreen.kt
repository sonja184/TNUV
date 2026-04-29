package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.uni_lj.fe.libri.data.api.Doc
import si.uni_lj.fe.libri.data.repository.BookRepository
import si.uni_lj.fe.libri.ui.components.BookCard

@Composable
fun HomeScreen(repository: BookRepository, onCardClick: (String) -> Unit) {
    // Defined fixed genres for consistency
    val genres = listOf("Fiction", "Romance", "Mystery", "Science Fiction", "History", "Fantasy", "Biography")
    
    // Using a key for LaunchedEffect to ensure it doesn't re-run unnecessarily 
    // though Unit is usually enough, a fixed state helps.
    val genreBooks = remember { mutableStateMapOf<String, List<Doc>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (genreBooks.isEmpty()) {
            isLoading = true
            genres.forEach { genre ->
                // Fetch books for each genre using a consistent subject query
                genreBooks[genre] = repository.searchBooks("subject:${genre.lowercase().replace(" ", "_")}")
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Most popular", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    // Using Fiction as the "Most Popular" stable list
                    val popularBooks = genreBooks["Fiction"] ?: emptyList()
                    items(popularBooks) { book ->
                        BookCard(
                            title = book.title,
                            imageUrl = book.thumbnailUrl,
                            onClick = { onCardClick(book.id) }
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            
            // Display other genres (excluding the one used for popular)
            genres.filter { it != "Fiction" }.forEach { genre ->
                val books = genreBooks[genre] ?: emptyList()
                if (books.isNotEmpty()) {
                    item {
                        Text(genre, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        LazyRow {
                            items(books) { book ->
                                BookCard(
                                    title = book.title,
                                    imageUrl = book.thumbnailUrl,
                                    onClick = { onCardClick(book.id) }
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
