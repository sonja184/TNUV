package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.gson.JsonPrimitive
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
    userLibraryRepository: UserLibraryRepository,
    onBackClick: () -> Unit
) {
    var bookInfo by remember { mutableStateOf<OpenLibraryWorkDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var currentStatus by remember { mutableStateOf(BookStatus.NONE) }
    var isStatusMenuExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookId) {
        val libraryBook = userLibraryRepository.getLibraryBook(bookId)

        if (libraryBook != null) {
            bookInfo = OpenLibraryWorkDetails(
                key = libraryBook.id,
                title = libraryBook.title,
                description = libraryBook.description?.let { JsonPrimitive(it) },
                covers = null,
                authors = libraryBook.authors.map { AuthorRole(AuthorKey(it)) },
                authorNames = libraryBook.authorName?.let { listOf(it) }
            )
            currentStatus = BookStatus.valueOf(libraryBook.status)
        } else {
            val cachedFromRepo = repository.getCachedBookDetails(bookId)
            if (cachedFromRepo != null) {
                bookInfo = cachedFromRepo
            } else {
                bookInfo = repository.getBookDetails(bookId)
            }
            
            if (bookInfo != null && bookInfo?.authorNames == null) {
                val cachedDoc = repository.findDocInCache(bookId)
                if (cachedDoc != null) {
                    bookInfo = bookInfo?.copy(authorNames = cachedDoc.author_name)
                }
            }
            currentStatus = BookStatus.NONE
        }
        isLoading = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (bookInfo != null) {
            val details = bookInfo!!
            var displayCoverUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(bookId, bookInfo) {
                val libBook = userLibraryRepository.getLibraryBook(bookId)
                displayCoverUrl = libBook?.coverUrl ?: details.coverUrl
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(34.dp),
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        ),
                    shape = RoundedCornerShape(34.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AsyncImage(
                        model = displayCoverUrl,
                        contentDescription = "Cover for ${details.title}",
                        modifier = Modifier
                            .size(width = 220.dp, height = 320.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = details.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val authorDisplay = details.authorNames?.joinToString(", ") ?: "Unknown Author"

                Text(
                    text = authorDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(26.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Text(
                            text = "Reading status",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box {
                            Button(
                                onClick = { isStatusMenuExpanded = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(
                                    text = when (currentStatus) {
                                        BookStatus.READ -> "Read"
                                        BookStatus.CURRENTLY_READING -> "Currently Reading"
                                        BookStatus.WANT_TO_READ -> "Want to Read"
                                        BookStatus.NONE -> "Add to Library"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
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
                                                    try {
                                                        userLibraryRepository.updateBookStatus(
                                                            bookId = bookId,
                                                            details = details,
                                                            status = status,
                                                            authorName = authorDisplay
                                                        )
                                                        currentStatus = status
                                                    } catch (e: Exception) {
                                                        snackbarHostState.showSnackbar(
                                                            "Access Denied: Check Firestore Rules in Console."
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }

                                if (currentStatus != BookStatus.NONE) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Remove from Library",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = {
                                            isStatusMenuExpanded = false
                                            scope.launch {
                                                try {
                                                    userLibraryRepository.updateBookStatus(
                                                        bookId = bookId,
                                                        details = details,
                                                        status = BookStatus.NONE
                                                    )
                                                    currentStatus = BookStatus.NONE
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar(
                                                        "Access Denied: Check Firestore Rules."
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = details.descriptionText ?: "No description available.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load book details.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
