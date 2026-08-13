package com.yeremi.ocupa2app.ui.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.components.Ocupa2Button
import com.yeremi.ocupa2app.ui.components.Ocupa2TextField
import com.yeremi.ocupa2app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (Boolean) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var referralMatricula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToHome -> onRegisterSuccess(true)
                is AuthViewModel.AuthEvent.NavigateToCompleteProfile -> onRegisterSuccess(false)
                else -> {}
            }
        }
    }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Crear cuenta",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, ElectricTeal)
                                ) {
                                    Text(
                                        text = "SOLO ESTUDIANTES",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateToLogin) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Regístrate con tu correo y matrícula del ITLA",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Ocupa2TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "Nombre",
                        placeholder = "ej. María",
                        leadingIcon = Icons.Default.Person
                    )

                    Ocupa2TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Apellido",
                        placeholder = "ej. García Pérez",
                        leadingIcon = Icons.Default.Person
                    )

            Ocupa2TextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico",
                placeholder = "tu@correo.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Ocupa2TextField(
                value = referralMatricula,
                onValueChange = { referralMatricula = it },
                label = "Matrícula / ID Estudiante",
                placeholder = "20241499",
                leadingIcon = Icons.Default.Badge,
                helperText = "Tu número de matrícula del ITLA"
            )

            Ocupa2TextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Mín. 6 caracteres",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Ocupa2TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar Contraseña",
                placeholder = "Repite tu contraseña",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val errorToShow = localError ?: (uiState as? AuthViewModel.AuthState.Error)?.message
            if (errorToShow != null) {
                Text(
                    text = errorToShow,
                    color = SolarOrange,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Ocupa2Button(
                text = "CREAR CUENTA",
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || 
                        referralMatricula.isBlank() || password.isBlank()) {
                        localError = "Todos los campos son obligatorios"
                    } else if (password.length < 6) {
                        localError = "La contraseña debe tener al menos 6 caracteres"
                    } else if (password != confirmPassword) {
                        localError = "Las contraseñas no coinciden"
                    } else {
                        localError = null
                        viewModel.register(email, firstName, lastName, password, referralMatricula)
                    }
                },
                isLoading = uiState is AuthViewModel.AuthState.Loading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Inicia sesión",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
