package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import si.uni_lj.fe.libri.ui.theme.ErrorRed

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp)
    ) {

        Text(
            text = "Welcome to Libri",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Log in to continue reading",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(60.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },

                    label = {
                        Text("Email")
                    },

                    singleLine = true,

                    shape = RoundedCornerShape(16.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },

                    label = {
                        Text("Password")
                    },

                    singleLine = true,

                    visualTransformation = PasswordVisualTransformation(),

                    shape = RoundedCornerShape(16.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        errorMessage = when {
                            email.isBlank() -> "Please enter your email."

                            password.isBlank() -> "Please enter your password."

                            else -> {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            onLoginClick()
                                        } else {
                                            errorMessage =
                                                task.exception?.message
                                                    ?: "Login failed."
                                        }
                                    }

                                ""
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Don't have an account? Create one",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}