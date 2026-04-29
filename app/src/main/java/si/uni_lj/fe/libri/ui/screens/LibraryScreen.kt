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
import si.uni_lj.fe.libri.data.repository.BookStatus
import si.uni_lj.fe.libri.data.repository.LibraryBook
import si.uni_lj.fe.libri.data.repository.UserLibraryRepository
import si.uni_lj.fe.libri.ui.components.BookCard

@Composable
fun LibraryScreen(userLibraryRepository: UserLibraryRepository, onCardClick: (String) -> Unit) {
    val sections = listOf(
        "Currently reading" to BookStatus.CURRENTLY_READING,
        "Read" to BookStatus.READ,
        "Want to read" to BookStatus.WANT_TO_READ
    )

    val sectionBooks = remember { mutableStateMapOf<BookStatus, List<LibraryBook>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        sections.forEach { (_, status) ->
            sectionBooks[status] = userLibraryRepository.getBooksByStatus(status)
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            sections.forEach { (sectionTitle, status) ->
                val books = sectionBooks[status] ?: emptyList()
                if (books.isNotEmpty()) {
                    item {
                        Text(sectionTitle, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        LazyRow {
                            items(books) { book ->
                                BookCard(
                                    title = book.title,
                                    imageUrl = book.coverUrl,
                                    onClick = { onCardClick(book.id) }
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
            
            // If all sections are empty
            if (sectionBooks.values.all { it.isEmpty() }) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your library is empty. Add some books!")
                    }
                }
            }
        }
    }
}
