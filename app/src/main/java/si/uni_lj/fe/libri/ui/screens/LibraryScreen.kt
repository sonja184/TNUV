package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import si.uni_lj.fe.libri.data.repository.BookStatus
import si.uni_lj.fe.libri.data.repository.LibraryBook
import si.uni_lj.fe.libri.data.repository.UserLibraryRepository
import si.uni_lj.fe.libri.ui.components.BookCard

@Composable
fun LibraryScreen(
    userLibraryRepository: UserLibraryRepository,
    onCardClick: (String) -> Unit
) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 24.dp
            )
        ) {
            item {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Track your reading progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            sections.forEach { (sectionTitle, status) ->
                val books = sectionBooks[status] ?: emptyList()

                if (books.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = sectionTitle,
                            count = books.size
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(books) { book ->
                                BookCard(
                                    title = book.title,
                                    imageUrl = book.coverUrl,
                                    onClick = { onCardClick(book.id) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }

            if (sectionBooks.values.all { it.isEmpty() }) {
                item {
                    EmptyLibraryMessage()
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        ) {
            Text(
                text = "$count",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyLibraryMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your library is empty",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Add books to start building your personal library.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}