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
fun LibraryScreen(repository: BookRepository, onCardClick: (String) -> Unit) {
    val sections = listOf(
        "Currently reading" to "reading",
        "Read" to "history",
        "Want to read" to "classic" // Just using some queries to populate data
    )

    val sectionBooks = remember { mutableStateMapOf<String, List<Doc>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        sections.forEach { (title, query) ->
            sectionBooks[title] = repository.searchBooks(query)
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            sections.forEach { (sectionTitle, _) ->
                item {
                    Text(sectionTitle, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyRow {
                        val books = sectionBooks[sectionTitle] ?: emptyList()
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
