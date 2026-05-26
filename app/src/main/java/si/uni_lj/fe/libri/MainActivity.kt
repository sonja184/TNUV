package si.uni_lj.fe.libri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import si.uni_lj.fe.libri.data.api.BookApiService
import si.uni_lj.fe.libri.data.repository.BookRepository
import si.uni_lj.fe.libri.data.repository.UserLibraryRepository
import si.uni_lj.fe.libri.ui.screens.*
import si.uni_lj.fe.libri.ui.theme.LibriTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl(BookApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val repository =
            BookRepository(
                retrofit.create(BookApiService::class.java)
            )

        val userLibraryRepository =
            UserLibraryRepository()

        setContent {
            // Check if user is already logged in on startup
            var isLoggedIn by rememberSaveable {
                mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
            }

            var isDarkTheme by rememberSaveable {
                mutableStateOf(false)
            }

            var showCreateAccount by rememberSaveable {
                mutableStateOf(false)
            }

            LibriTheme(
                darkTheme = isDarkTheme
            ) {
                when {
                    isLoggedIn -> {
                        LibriApp(
                            repository = repository,
                            userLibraryRepository = userLibraryRepository,
                            onLogoutClick = {
                                isLoggedIn = false
                                showCreateAccount = false
                            },
                            isDarkTheme = isDarkTheme,
                            onThemeChange = {
                                isDarkTheme = it
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

@Composable
fun LibriApp(
    repository: BookRepository,
    userLibraryRepository: UserLibraryRepository,
    onLogoutClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // Ensure sync is active when the app is showing
    DisposableEffect(Unit) {
        userLibraryRepository.startSync()
        onDispose {
            userLibraryRepository.stopSync()
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        when (destination) {
                            AppDestinations.HOME_PAGE -> {
                                Icon(
                                    imageVector = Icons.Outlined.Home,
                                    contentDescription = destination.label
                                )
                            }
                            AppDestinations.MY_LIBRARY -> {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = destination.label
                                )
                            }
                            AppDestinations.MY_PROFILE -> {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = destination.label
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = destination.label,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    selected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo("home") {
                                    inclusive = false
                                    saveState = false
                                }

                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        repository = repository,
                        onCardClick = { bookId ->
                            navController.navigate("detail/$bookId")
                        }
                    )
                }

                composable("library") {
                    LibraryScreen(
                        userLibraryRepository = userLibraryRepository,
                        onCardClick = { bookId ->
                            navController.navigate("detail/$bookId")
                        }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        userLibraryRepository = userLibraryRepository,
                        onLogoutClick = onLogoutClick,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = onThemeChange
                    )
                }

                composable(
                    route = "detail/{bookId}",
                    arguments = listOf(
                        navArgument("bookId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                    BookDetailScreen(
                        bookId = bookId,
                        repository = repository,
                        userLibraryRepository = userLibraryRepository,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val route: String
) {
    HOME_PAGE("Home", "home"),
    MY_LIBRARY("Library", "library"),
    MY_PROFILE("Profile", "profile")
}
