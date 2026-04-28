package si.uni_lj.fe.libri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import si.uni_lj.fe.libri.ui.screens.BookDetailScreen
import si.uni_lj.fe.libri.ui.screens.CreateAccountScreen
import si.uni_lj.fe.libri.ui.screens.HomeScreen
import si.uni_lj.fe.libri.ui.screens.LibraryScreen
import si.uni_lj.fe.libri.ui.screens.LoginScreen
import si.uni_lj.fe.libri.ui.screens.ProfileScreen
import si.uni_lj.fe.libri.ui.theme.LibriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LibriTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var showCreateAccount by remember { mutableStateOf(false) }

                when {
                    isLoggedIn -> {
                        LibriApp(
                            onLogoutClick = {
                                isLoggedIn = false
                                showCreateAccount = false
                            }
                        )
                    }

                    showCreateAccount -> {
                        CreateAccountScreen(
                            onAccountCreated = {
                                isLoggedIn = true
                                showCreateAccount = false
                            },
                            onBackToLoginClick = {
                                showCreateAccount = false
                            }
                        )
                    }

                    else -> {
                        LoginScreen(
                            onLoginClick = {
                                isLoggedIn = true
                            },
                            onCreateAccountClick = {
                                showCreateAccount = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun LibriApp(
    onLogoutClick: () -> Unit = {}
) {
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
                    HomeScreen(
                        onCardClick = { bookId ->
                            navController.navigate("detail/$bookId")
                        }
                    )
                }

                composable("library") {
                    LibraryScreen(
                        onCardClick = { bookId ->
                            navController.navigate("detail/$bookId")
                        }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        onLogoutClick = onLogoutClick
                    )
                }

                composable(
                    "detail/{bookId}",
                    arguments = listOf(
                        navArgument("bookId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                    BookDetailScreen(bookId = bookId)
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

@Preview(showBackground = true)
@Composable
fun LibriAppPreview() {
    LibriTheme {
        LibriApp()
    }
}