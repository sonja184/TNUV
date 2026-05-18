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
import androidx.compose.ui.draw.shadow
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
            )
        ) {
            item {
                LibraryHeader()
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
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(books) { book ->
                                BookCard(
                                    title = book.title,
                                    imageUrl = book.coverUrl,
                                    onClick = { onCardClick(book.id) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
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
private fun LibraryHeader() {
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
                text = "My Library",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Track what you are reading, finished, and planning to read.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(30.dp))
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
            fontWeight = FontWeight.Bold
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        ) {
            Text(
                text = "$count",
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyLibraryMessage() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Your library is empty",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add books from the home page to start building your personal collection.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}