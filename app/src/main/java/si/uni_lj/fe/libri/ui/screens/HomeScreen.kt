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
    val genres = listOf("Fiction", "Non-fiction", "Science", "Fantasy")
    
    // States for books
    var mostPopular by remember { mutableStateOf<List<Doc>>(emptyList()) }
    val genreBooks = remember { mutableStateMapOf<String, List<Doc>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        // Fetch most popular (using "trending" or similar query)
        mostPopular = repository.searchBooks("subject:fiction")
        
        // Fetch for each genre
        genres.forEach { genre ->
            genreBooks[genre] = repository.searchBooks("subject:$genre")
        }
        isLoading = false
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
                    items(mostPopular) { book ->
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
            genres.forEach { genre ->
                item {
                    Text(genre, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyRow {
                        items(genreBooks[genre] ?: emptyList()) { book ->
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
