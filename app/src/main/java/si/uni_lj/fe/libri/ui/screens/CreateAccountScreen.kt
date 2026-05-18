package si.uni_lj.fe.libri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import si.uni_lj.fe.libri.ui.theme.ErrorRed

@Composable
fun CreateAccountScreen(
    onAccountCreated: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 34.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join Libri and build your personal reading space.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(36.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                ),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },

                    label = { Text("Name") },

                    singleLine = true,

                    shape = RoundedCornerShape(18.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },

                    label = { Text("Email") },

                    singleLine = true,

                    shape = RoundedCornerShape(18.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },

                    label = { Text("Password") },

                    singleLine = true,

                    visualTransformation = PasswordVisualTransformation(),

                    shape = RoundedCornerShape(18.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },

                    label = { Text("Confirm Password") },

                    singleLine = true,

                    visualTransformation = PasswordVisualTransformation(),

                    shape = RoundedCornerShape(18.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),

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
                            name.isBlank() -> "Please enter your name."
                            email.isBlank() -> "Please enter your email."
                            password.isBlank() -> "Please enter your password."
                            confirmPassword.isBlank() -> "Please confirm your password."
                            password != confirmPassword -> "Passwords do not match."

                            else -> {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {

                                            val user = auth.currentUser

                                            val profileUpdates =
                                                UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .build()

                                            user?.updateProfile(profileUpdates)
                                                ?.addOnCompleteListener { updateTask ->

                                                    if (updateTask.isSuccessful) {
                                                        onAccountCreated()

                                                    } else {

                                                        errorMessage =
                                                            updateTask.exception?.message
                                                                ?: "Name not saved."
                                                    }
                                                }

                                        } else {

                                            errorMessage =
                                                task.exception?.message
                                                    ?: "Account creation failed."
                                        }
                                    }

                                ""
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text(
                        text = "Create account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = onBackToLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Already have an account? Log in",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}