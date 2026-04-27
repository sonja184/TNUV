package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.uni_lj.fe.libri.ui.components.BookCard

@Composable
fun HomeScreen(onCardClick: (String) -> Unit) {
    val genres = listOf("Fiction", "Non-fiction", "Science", "Fantasy")
    val mostPopular = List(5) { i -> "Popular Book ${i + 1}" }
    val genreBooks = genres.associateWith { genre -> List(3) { i -> "$genre Book ${i + 1}" } }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Most popular", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LazyRow {
                items(mostPopular) { book ->
                    BookCard(title = book, onClick = { onCardClick(book) })
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
                        BookCard(title = book, onClick = { onCardClick(book) })
                        Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}