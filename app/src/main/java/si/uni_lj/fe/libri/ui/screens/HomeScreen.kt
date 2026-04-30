package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.uni_lj.fe.libri.data.api.Doc
import si.uni_lj.fe.libri.data.repository.BookRepository
import si.uni_lj.fe.libri.ui.components.BookCard

@Composable
fun HomeScreen(
    repository: BookRepository,
    onCardClick: (String) -> Unit
) {
    val genres = listOf(
        "Fiction",
        "Romance",
        "Mystery",
        "Science Fiction",
        "History",
        "Fantasy",
        "Biography"
    )

    val genreBooks = remember { mutableStateMapOf<String, List<Doc>>() }

    var isLoading by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Doc>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (genreBooks.isEmpty()) {
            isLoading = true

            genres.forEach { genre ->
                genreBooks[genre] =
                    repository.searchBooks("subject:${genre.lowercase().replace(" ", "_")}")
            }

            isLoading = false
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            searchResults = emptyList()
            isSearching = false
        } else {
            isSearching = true
            searchResults = repository.searchBooks(searchQuery)
            isSearching = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Discover books",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search books") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "Search results",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (searchResults.isEmpty()) {
                    item {
                        Text(
                            text = "No books found.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(searchResults) { book ->
                        BookCard(
                            title = book.title,
                            imageUrl = book.thumbnailUrl,
                            onClick = { onCardClick(book.id) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                item {
                    Text(
                        text = "Most popular",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow {
                        val popularBooks = genreBooks["Fiction"] ?: emptyList()

                        items(popularBooks) { book ->
                            BookCard(
                                title = book.title,
                                imageUrl = book.thumbnailUrl,
                                onClick = { onCardClick(book.id) }
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                genres.filter { it != "Fiction" }.forEach { genre ->
                    val books = genreBooks[genre] ?: emptyList()

                    if (books.isNotEmpty()) {
                        item {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow {
                                items(books) { book ->
                                    BookCard(
                                        title = book.title,
                                        imageUrl = book.thumbnailUrl,
                                        onClick = { onCardClick(book.id) }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}