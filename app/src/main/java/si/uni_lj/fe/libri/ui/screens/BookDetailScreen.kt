package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import si.uni_lj.fe.libri.data.api.AuthorKey
import si.uni_lj.fe.libri.data.api.AuthorRole
import si.uni_lj.fe.libri.data.api.OpenLibraryWorkDetails
import si.uni_lj.fe.libri.data.repository.BookRepository
import si.uni_lj.fe.libri.data.repository.BookStatus
import si.uni_lj.fe.libri.data.repository.UserLibraryRepository

@Composable
fun BookDetailScreen(
    bookId: String,
    repository: BookRepository,
    userLibraryRepository: UserLibraryRepository
) {
    var bookInfo by remember { mutableStateOf<OpenLibraryWorkDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var currentStatus by remember { mutableStateOf(BookStatus.NONE) }
    var isStatusMenuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(bookId) {
        isLoading = true
        
        // 1. Check if book is in library (Firebase)
        val libraryBook = userLibraryRepository.getLibraryBook(bookId)
        
        if (libraryBook != null) {
            // Book is in library, use cached data
            bookInfo = OpenLibraryWorkDetails(
                key = libraryBook.id,
                title = libraryBook.title,
                description = libraryBook.description,
                covers = null, // Not needed as we use coverUrl
                authors = libraryBook.authors.map { AuthorRole(AuthorKey(it)) }
            ).let { details ->
                // Override coverUrl behavior by providing a custom object if needed, 
                // but here we can just handle it in the UI or use a wrapper.
                // For simplicity, I'll pass it as is and use libraryBook.coverUrl in UI.
                details
            }
            // Add a temporary property to details to store the coverUrl if it's from cache
            // Since we can't easily modify the class, let's just use a separate state or 
            // handle it in the UI block.
            currentStatus = BookStatus.valueOf(libraryBook.status)
        } else {
            // 2. Not in library, fetch from API
            bookInfo = repository.getBookDetails(bookId)
            currentStatus = BookStatus.NONE
        }
        
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (bookInfo != null) {
        val details = bookInfo!!
        
        // Determine cover URL (either from API or from cache)
        // If covers is null but we have cached coverUrl, use that.
        // We'll fetch the LibraryBook again or just use a state.
        // Let's use a simpler approach: get the LibraryBook inside the LaunchedEffect and store its coverUrl.
        var displayCoverUrl by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(bookId, bookInfo) {
            val libBook = userLibraryRepository.getLibraryBook(bookId)
            displayCoverUrl = libBook?.coverUrl ?: details.coverUrl
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book cover
            AsyncImage(
                model = displayCoverUrl,
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
                text = details.authors?.joinToString(", ") { it.author.key.removePrefix("/authors/").removePrefix("authors/") } ?: "Unknown Author",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status Dropdown
            Box {
                Button(
                    onClick = { isStatusMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        when (currentStatus) {
                            BookStatus.READ -> "Read"
                            BookStatus.CURRENTLY_READING -> "Currently Reading"
                            BookStatus.WANT_TO_READ -> "Want to Read"
                            BookStatus.NONE -> "Add to Library"
                        }
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isStatusMenuExpanded,
                    onDismissRequest = { isStatusMenuExpanded = false }
                ) {
                    BookStatus.entries.forEach { status ->
                        if (status != BookStatus.NONE) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (status) {
                                            BookStatus.READ -> "Read"
                                            BookStatus.CURRENTLY_READING -> "Currently Reading"
                                            BookStatus.WANT_TO_READ -> "Want to Read"
                                            else -> ""
                                        }
                                    )
                                },
                                onClick = {
                                    isStatusMenuExpanded = false
                                    scope.launch {
                                        userLibraryRepository.updateBookStatus(bookId, details, status)
                                        currentStatus = status
                                    }
                                }
                            )
                        }
                    }
                    if (currentStatus != BookStatus.NONE) {
                        DropdownMenuItem(
                            text = { Text("Remove from Library") },
                            onClick = {
                                isStatusMenuExpanded = false
                                scope.launch {
                                    userLibraryRepository.updateBookStatus(bookId, details, BookStatus.NONE)
                                    currentStatus = BookStatus.NONE
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
