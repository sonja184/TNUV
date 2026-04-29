package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails
import si.uni_lj.fe.libri.data.repository.BookRepository

@Composable
fun BookDetailScreen(bookId: String, repository: BookRepository) {
    var bookInfo by remember { mutableStateOf<OpenLibraryWorkDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookId) {
        isLoading = true
        bookInfo = repository.getBookDetails(bookId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (bookInfo != null) {
        val details = bookInfo!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book cover
            AsyncImage(
                model = details.coverUrl,
                contentDescription = "Cover for ${details.title}",
                modifier = Modifier
                    .size(200.dp, 300.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = details.title,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = details.authors?.joinToString(", ") { it.author.key.removePrefix("/authors/") } ?: "Unknown Author",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = details.descriptionText ?: "No description available.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Failed to load book details.")
        }
    }
}
