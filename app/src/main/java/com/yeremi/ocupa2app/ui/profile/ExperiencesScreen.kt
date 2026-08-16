package com.yeremi.ocupa2app.ui.profile

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yeremi.ocupa2app.network.models.Experience
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperiencesScreen(
    viewModel: ExperiencesViewModel,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDialog by remember {
        mutableStateOf(false)
    }

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var certificateUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            certificateUri = uri
        }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Experiencias")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar experiencia"
                        )
                    }
                }
            )
        }
    ) { padding ->

        when {

            state.isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AcidLime
                    )
                }
            }

            state.error != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = state.error!!
                        )

                        Button(
                            onClick = viewModel::loadExperiences
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Text(
                            text = "Mi perfil y experiencias",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Agrega las experiencias que respalden tu perfil. Puedes adjuntar una imagen como certificado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (state.experiences.isEmpty()) {

                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {

                                Text(
                                    text = "Aún no tienes experiencias registradas.",
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }

                    items(
                        items = state.experiences,
                        key = { experience ->
                            experience.id
                        }
                    ) { experience ->

                        ExperienceCard(
                            experience = experience
                        )
                    }

                    item {

                        Button(
                            onClick = {
                                showDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AcidLime
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Agregar experiencia"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {

        AlertDialog(
            onDismissRequest = {

                if (!state.isSaving) {
                    showDialog = false
                }
            },
            title = {
                Text("Nueva experiencia")
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = {
                            Text("Título / experiencia")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = {
                            Text("Descripción")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedButton(
                        onClick = {
                            imagePicker.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                if (certificateUri == null) {
                                    "Adjuntar certificado"
                                } else {
                                    "Certificado seleccionado"
                                }
                        )
                    }

                    state.message?.let { message ->

                        Text(
                            text = message,
                            color =
                                if (
                                    message.contains(
                                        "agregada",
                                        ignoreCase = true
                                    )
                                ) {
                                    ElectricTeal
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                        )
                    }
                }
            },
            confirmButton = {

                Button(
                    enabled = !state.isSaving,
                    onClick = {

                        val base64Certificate =
                            certificateUri?.let { uri ->

                                try {

                                    context
                                        .contentResolver
                                        .openInputStream(uri)
                                        ?.use { stream ->

                                            Base64.encodeToString(
                                                stream.readBytes(),
                                                Base64.NO_WRAP
                                            )
                                        }

                                } catch (_: Exception) {
                                    null
                                }
                            }

                        viewModel.addExperience(
                            title = title,
                            description = description,
                            certificateBase64 = base64Certificate
                        )
                    }
                ) {

                    if (state.isSaving) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text("Guardar")
                    }
                }
            },
            dismissButton = {

                TextButton(
                    enabled = !state.isSaving,
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(
        state.message,
        state.isSaving
    ) {

        if (
            state.message?.contains(
                "agregada",
                ignoreCase = true
            ) == true &&
            !state.isSaving
        ) {

            showDialog = false

            title = ""

            description = ""

            certificateUri = null

            viewModel.clearMessage()
        }
    }
}

@Composable
private fun ExperienceCard(
    experience: Experience
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = experience.title ?: "Experiencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            experience.description?.let { description ->

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            experience.createdAt?.let { createdAt ->

                Text(
                    text = createdAt.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!experience.certificate.isNullOrBlank()) {

                Text(
                    text = "Certificado",
                    style = MaterialTheme.typography.labelLarge,
                    color = ElectricTeal
                )

                AsyncImage(
                    model = experience.certificate,
                    contentDescription = "Certificado de experiencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}