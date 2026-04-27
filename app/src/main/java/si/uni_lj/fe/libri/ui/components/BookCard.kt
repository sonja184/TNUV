package si.uni_lj.fe.libri.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BookCard(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 180.dp)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for book cover
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.BottomCenter),
            style = MaterialTheme.typography.bodySmall
        )
    }
}