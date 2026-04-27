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
fun LibraryScreen(onCardClick: (String) -> Unit) {
    val sections = listOf(
        "Currently reading" to List(3) { i -> "Currently Reading Book ${i + 1}" },
        "Read" to List(3) { i -> "Read Book ${i + 1}" },
        "Want to read" to List(3) { i -> "Want to Read Book ${i + 1}" }
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        sections.forEach { (sectionTitle, books) ->
            item {
                Text(sectionTitle, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    items(books) { book ->
                        BookCard(title = book, onClick = { onCardClick(book) })
                        Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}