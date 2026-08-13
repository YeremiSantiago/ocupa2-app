package com.yeremi.ocupa2app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.components.Ocupa2Button
import com.yeremi.ocupa2app.ui.components.Ocupa2TextField
import com.yeremi.ocupa2app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    viewModel: CompleteProfileViewModel,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var cedula by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    var isGenderExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is CompleteProfileViewModel.ProfileState.ProfileLoaded) {
            val user = (uiState as CompleteProfileViewModel.ProfileState.ProfileLoaded).user
            cedula = user.cedula ?: ""
            firstName = user.firstName ?: ""
            lastName = user.lastName ?: ""
            gender = user.gender ?: ""
            birthDate = user.birthDate ?: ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is CompleteProfileViewModel.ProfileEvent.NavigateToHome) {
                onNavigateToHome()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                Ocupa2Button(
                    text = "GUARDAR Y CONTINUAR",
                    onClick = {
                        viewModel.updateProfile(cedula, firstName, lastName, gender, birthDate)
                    },
                    isLoading = uiState is CompleteProfileViewModel.ProfileState.Loading,
                    enabled = cedula.isNotBlank() && firstName.isNotBlank() && 
                             lastName.isNotBlank() && gender.isNotBlank() && birthDate.isNotBlank()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Completa tu perfil",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Esta información es requerida para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))



                Ocupa2TextField(
                    value = cedula,
                    onValueChange = { cedula = it },
                    label = "Cédula",
                    placeholder = "000-0000000-0",
                    leadingIcon = Icons.Default.CreditCard,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Ocupa2TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "Nombre",
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Default.Person
                )

                Ocupa2TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Apellido",
                    placeholder = "Tu apellido",
                    leadingIcon = Icons.Default.Person
                )

                // Gender Selector
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(
                        text = "GÉNERO",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = isGenderExpanded,
                        onExpandedChange = { isGenderExpanded = it }
                    ) {
                        TextField(
                            value = when(gender) {
                                "masculino" -> "Masculino"
                                "femenino" -> "Femenino"
                                "otro" -> "Otro"
                                else -> ""
                            },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            placeholder = { Text("Selecciona tu género", color = MutedGray) },
                            leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = isGenderExpanded,
                            onDismissRequest = { isGenderExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Masculino", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { gender = "masculino"; isGenderExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Femenino", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { gender = "femenino"; isGenderExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Otro", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { gender = "otro"; isGenderExpanded = false }
                            )
                        }
                    }
                }

                // Date Picker
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Ocupa2TextField(
                        value = birthDate,
                        onValueChange = {},
                        label = "Fecha de Nacimiento",
                        placeholder = "DD/MM/AAAA",
                        leadingIcon = Icons.Default.CalendarToday,
                        keyboardOptions = KeyboardOptions.Default
                    )
                    // Overlay invisible click
                    Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).padding(top = 24.dp)) {
                        Button(
                            onClick = { showDatePicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Warning Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Estos datos son permanentes y no podrán ser modificados una vez guardados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (uiState is CompleteProfileViewModel.ProfileState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as CompleteProfileViewModel.ProfileState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        birthDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
