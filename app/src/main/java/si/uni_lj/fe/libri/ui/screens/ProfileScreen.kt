package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import si.uni_lj.fe.libri.R
import si.uni_lj.fe.libri.R.string.*
import si.uni_lj.fe.libri.data.repository.BookStatus
import si.uni_lj.fe.libri.data.repository.UserLibraryRepository

@Composable
fun ProfileScreen(
    userLibraryRepository: UserLibraryRepository,
    onLogoutClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val name = user?.displayName ?: stringResource(guest_user)
    val email = user?.email ?: stringResource(no_email)

    // Use collectAsState directly on the StateFlow property.
    // This ensures real-time updates and is more idiomatic for StateFlow.
    val allBooksNullable by userLibraryRepository.libraryBooks.collectAsState()
    val allBooks = allBooksNullable ?: emptyList()
    var favoriteGenre by remember { mutableStateOf("") }
    val noGenre = stringResource(no_genre)

    // Derived state for counters - updates automatically when allBooks changes
    val readCount = remember(allBooks) {
        allBooks.count { it.bookStatus == BookStatus.READ }
    }
    val currentlyReadingCount = remember(allBooks) {
        allBooks.count { it.bookStatus == BookStatus.CURRENTLY_READING }
    }
    val savedCount = remember(allBooks) {
        allBooks.count { it.bookStatus == BookStatus.WANT_TO_READ }
    }
    LaunchedEffect(allBooks) {
        val fav = userLibraryRepository.getFavoriteGenre()
        favoriteGenre = if (fav.isBlank()) noGenre else fav
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(
            name = name,
            email = email
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileInfoCard(
            name = name,
            email = email,
            favoriteGenre = favoriteGenre,
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = stringResource(read),
                value = readCount.toString(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = stringResource(reading),
                value = currentlyReadingCount.toString(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = stringResource(saved),
                value = savedCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedButton(
            onClick = {
                auth.signOut()
                onLogoutClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(log_out),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(108.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile icon",
                        modifier = Modifier.size(58.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileInfoCard(
    name: String,
    email: String,
    favoriteGenre: String,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = stringResource(username),
                value = name
            )

            ProfileDivider()

            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = stringResource(R.string.email),
                value = email
            )

            ProfileDivider()

            ProfileInfoRow(
                icon = Icons.Default.Star,
                label = stringResource(favorite_genre_label),
                value = favoriteGenre
            )

            ProfileDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(dark_mode),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = if (isDarkTheme) stringResource(on) else stringResource(off),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBubble {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IconBubble(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.size(42.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    )
}

@Composable
private fun ProfileDivider() {
    Spacer(modifier = Modifier.height(18.dp))

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    )

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
