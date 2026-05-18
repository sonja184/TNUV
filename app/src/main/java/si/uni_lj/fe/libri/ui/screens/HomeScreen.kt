package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 30.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                HomeHeader(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it }
                )
            }

            if (searchQuery.isNotBlank()) {
                item {
                    SectionTitle("Search results")
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else if (searchResults.isEmpty()) {
                    item {
                        EmptyState("No books found. Try another title or author.")
                    }
                } else {
                    items(searchResults) { book ->
                        BookCard(
                            title = book.title,
                            imageUrl = book.thumbnailUrl,
                            onClick = { onCardClick(book.id) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            } else {
                item {
                    SectionTitle("Most popular")
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val popularBooks = genreBooks["Fiction"] ?: emptyList()

                        items(popularBooks) { book ->
                            BookCard(
                                title = book.title,
                                imageUrl = book.thumbnailUrl,
                                onClick = { onCardClick(book.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                genres.filter { it != "Fiction" }.forEach { genre ->
                    val books = genreBooks[genre] ?: emptyList()

                    if (books.isNotEmpty()) {
                        item {
                            SectionTitle(genre)
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(books) { book ->
                                    BookCard(
                                        title = book.title,
                                        imageUrl = book.thumbnailUrl,
                                        onClick = { onCardClick(book.id) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Discover books",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Find stories, authors and genres you will love.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search books") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyState(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}