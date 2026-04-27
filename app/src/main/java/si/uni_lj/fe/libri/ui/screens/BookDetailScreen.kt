package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BookDetailScreen(bookId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for book cover
        Box(
            modifier = Modifier
                .size(200.dp, 300.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cover Image")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = bookId,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "Author Name",
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
            text = "This is a detailed description of the book with ID: $bookId. It provides information about the plot, characters, and other interesting details that readers might want to know before or after reading the book.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}