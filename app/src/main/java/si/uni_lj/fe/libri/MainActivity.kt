package si.uni_lj.fe.libri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import si.uni_lj.fe.libri.ui.theme.LibriTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibriTheme {
                LibriApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun LibriApp() {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME_PAGE) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                        currentDestination = it
                        when (it) {
                            AppDestinations.HOME_PAGE -> navController.navigate("home")
                            AppDestinations.MY_LIBRARY -> navController.navigate("library")
                            AppDestinations.MY_PROFILE -> navController.navigate("profile")
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomePage(onCardClick = { bookId -> navController.navigate("detail/$bookId") })
                }
                composable("library") {
                    MyLibraryPage(onCardClick = { bookId -> navController.navigate("detail/$bookId") })
                }
                composable("profile") {
                    DestinationPage(title = "MyProfile")
                }
                composable(
                    "detail/{bookId}",
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                    BookDetailPage(bookId = bookId)
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {

    HOME_PAGE("Homepage", R.drawable.ic_home),
    MY_LIBRARY("MyLibrary", R.drawable.ic_favorite),
    MY_PROFILE("MyProfile", R.drawable.ic_account_box),
}

@Composable
fun DestinationPage(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title)
    }
}

@Composable
fun HomePage(onCardClick: (String) -> Unit) {
    val genres = listOf("Fiction", "Non-fiction", "Science", "Fantasy")
    val mostPopular = List(5) { i -> "Popular Book ${i + 1}" }
    val genreBooks = genres.associateWith { genre -> List(3) { i -> "$genre Book ${i + 1}" } }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Most popular", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LazyRow {
                items(mostPopular) { book ->
                    BookCard(title = book, onClick = { onCardClick(book) })
                    Spacer(Modifier.width(8.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        genres.forEach { genre ->
            item {
                Text(genre, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    items(genreBooks[genre] ?: emptyList()) { book ->
                        BookCard(title = book, onClick = { onCardClick(book) })
                        Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

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
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.BottomCenter),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun BookDetailPage(bookId: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Book detail for $bookId")
    }
}

@Composable
fun MyLibraryPage(onCardClick: (bookId: String) -> Unit) {
    val sections = listOf(
        "Currently reading" to List(3) { i -> "Currently Reading Book ${i + 1}" },
        "Read" to List(3) { i -> "Read Book ${i + 1}" },
        "Want to read" to List(3) { i -> "Want to Read Book ${i + 1}" }
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        sections.forEach { (sectionTitle, books) ->
            item {
                Text(sectionTitle, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    items(books) { book ->
                        BookCard(title = book, onClick = { onCardClick(book) })
                        Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationPagePreview() {
    LibriTheme {
        DestinationPage("Homepage")
    }
}