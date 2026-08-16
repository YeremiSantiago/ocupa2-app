package com.yeremi.ocupa2app.ui.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.SubcomposeAsyncImage
import com.yeremi.ocupa2app.network.models.OfferQuestion
import com.yeremi.ocupa2app.network.resolveMediaUrl
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailScreen(
    offerId: String,
    viewModel: OfferDetailViewModel,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    val answers = remember {
        mutableStateMapOf<String, String>()
    }

    var comment by remember {
        mutableStateOf("")
    }

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle de oferta")
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
                }
            )
        }
    ) { padding ->

        when {

            state.isLoading &&
                    state.offer == null -> {

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

            state.error != null &&
                    state.offer == null -> {

                OfferDetailError(
                    modifier = Modifier.padding(padding),
                    message = state.error!!,
                    onRetry = {
                        viewModel.loadOffer(offerId)
                    }
                )
            }

            state.offer != null -> {

                val offer = state.offer!!

                val photoUrl = resolveMediaUrl(
                    offer.photo
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(bottom = 32.dp)
                ) {

                    if (photoUrl != null) {

                        SubcomposeAsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto del empleo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = AcidLime,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment =
                                            Alignment.CenterHorizontally,
                                        verticalArrangement =
                                            Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector =
                                                Icons.Default.BrokenImage,
                                            contentDescription = null,
                                            tint =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant,
                                            modifier =
                                                Modifier.size(40.dp)
                                        )

                                        Text(
                                            text =
                                                "No se pudo cargar la foto",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        )
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text =
                                offer.jobTypeName
                                    ?: offer.jobTypeKey
                                    ?: "Oferta de empleo",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        offer.contractType?.let { contract ->

                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        contract.replaceFirstChar {
                                            it.uppercaseChar()
                                        }
                                    )
                                }
                            )
                        }

                        InfoLine(
                            icon = Icons.Default.LocationOn,
                            text =
                                offer.address
                                    ?: "Ubicación no especificada"
                        )

                        val paymentText =
                            buildString {

                                offer.payment
                                    ?.amount
                                    ?.let {
                                        append(it)
                                    }

                                offer.payment
                                    ?.currency
                                    ?.let {
                                        append(" $it")
                                    }

                                offer.payment
                                    ?.period
                                    ?.let {
                                        append(" / $it")
                                    }
                            }.ifBlank {
                                "Pago no especificado"
                            }

                        InfoLine(
                            icon = Icons.Default.Payments,
                            text = paymentText
                        )

                        Text(
                            text = "Descripción",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                offer.description
                                    ?: "Sin descripción",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyLarge
                        )

                        offer.deadline?.let { deadline ->

                            Text(
                                text =
                                    "Fecha límite: ${deadline.take(10)}",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }

                        HorizontalDivider()

                        Text(
                            text = "Aplicar a esta oferta",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "La identidad de quien publicó la oferta se mantiene oculta hasta seleccionar al ganador.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = comment,
                            onValueChange = {
                                comment = it
                            },
                            label = {
                                Text(
                                    "¿Por qué eres apto para el puesto?"
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        offer.questions
                            .forEachIndexed { index, question ->

                                val key =
                                    question.id
                                        ?: "question_$index"

                                QuestionField(
                                    question = question,
                                    value =
                                        answers[key].orEmpty(),
                                    onValueChange = {
                                        answers[key] = it
                                    }
                                )
                            }

                        state.message?.let { message ->

                            Text(
                                text = message,
                                color =
                                    if (state.applySuccess) {
                                        ElectricTeal
                                    } else {
                                        MaterialTheme
                                            .colorScheme
                                            .error
                                    }
                            )
                        }

                        Button(
                            onClick = {

                                val normalizedAnswers =
                                    buildMap {

                                        offer.questions
                                            .forEachIndexed {
                                                    index,
                                                    question ->

                                                val localKey =
                                                    question.id
                                                        ?: "question_$index"

                                                val requestKey =
                                                    question.id
                                                        ?: question
                                                            .label
                                                            .ifBlank {
                                                                localKey
                                                            }

                                                put(
                                                    requestKey,
                                                    answers[
                                                        localKey
                                                    ].orEmpty()
                                                )
                                            }
                                    }

                                viewModel.apply(
                                    comment = comment,
                                    answers =
                                        normalizedAnswers
                                )
                            },
                            enabled =
                                !state.isApplying &&
                                        !state.applySuccess &&
                                        !offer.appliedByMe,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            AcidLime
                                    )
                        ) {

                            if (state.isApplying) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Text(
                                    when {
                                        state.applySuccess ||
                                                offer.appliedByMe ->
                                            "Ya aplicaste"

                                        else ->
                                            "Enviar aplicación"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionField(
    question: OfferQuestion,
    value: String,
    onValueChange: (String) -> Unit
) {

    val label =
        question.label +
                if (question.required) {
                    " *"
                } else {
                    ""
                }

    when (question.type.lowercase()) {

        "check",
        "checkbox",
        "boolean" -> {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Checkbox(
                    checked =
                        value == "true",
                    onCheckedChange = {
                        onValueChange(
                            it.toString()
                        )
                    }
                )

                Text(label)
            }
        }

        "select" -> {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = label,
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge
                )

                question.options
                    .orEmpty()
                    .forEach { option ->

                        FilterChip(
                            selected =
                                value == option,
                            onClick = {
                                onValueChange(
                                    option
                                )
                            },
                            label = {
                                Text(option)
                            }
                        )
                    }
            }
        }

        "date" -> {

            OutlinedTextField(
                value = value,
                onValueChange =
                    onValueChange,
                label = {
                    Text(label)
                },
                placeholder = {
                    Text("AAAA-MM-DD")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        else -> {

            OutlinedTextField(
                value = value,
                onValueChange =
                    onValueChange,
                label = {
                    Text(label)
                },
                modifier =
                    Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoLine(
    icon:
    androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AcidLime
        )

        Text(text = text)
    }
}

@Composable
private fun OfferDetailError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Card(
            modifier =
                Modifier.padding(24.dp),
            shape =
                RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier =
                    Modifier.padding(20.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(message)

                Button(
                    onClick = onRetry
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}