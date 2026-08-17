package com.yeremi.ocupa2app.ui.publish

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.network.models.JobTypeDetail
import com.yeremi.ocupa2app.network.models.PublishQuestion
import com.yeremi.ocupa2app.ui.components.Ocupa2Button
import com.yeremi.ocupa2app.ui.components.Ocupa2TextField
import com.yeremi.ocupa2app.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishOfferScreen(
    viewModel: PublishOfferViewModel,
    onPublishSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val handleBack = {
        when (uiState.step) {
            PublishStep.DATOS -> onNavigateBack()
            PublishStep.FOTO -> viewModel.goToStep(PublishStep.DATOS)
            PublishStep.PAGO -> viewModel.goToStep(PublishStep.FOTO)
            PublishStep.EXITO -> onPublishSuccess()
        }
    }

    BackHandler(enabled = true) {
        handleBack()
    }

    LaunchedEffect(Unit) {
        if (viewModel.uiState.value.step == PublishStep.EXITO) {
            viewModel.reset()
        }
        viewModel.loadJobTypes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Publicar oferta",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    color = IceWhite
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = handleBack
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = IceWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        StepIndicator(
            currentStep = uiState.step
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = SolarOrange,
                fontFamily = WorkSansFamily,
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                )
            )
        }

        when (uiState.step) {
            PublishStep.DATOS ->
                StepDatos(uiState, viewModel)

            PublishStep.FOTO ->
                StepFoto(uiState, viewModel, context)

            PublishStep.PAGO ->
                StepPago(uiState, viewModel)

            PublishStep.EXITO ->
                StepExito(onPublishSuccess)
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: PublishStep
) {
    val steps = listOf(
        "Datos",
        "Foto",
        "Pago"
    )

    val currentIndex = when (currentStep) {
        PublishStep.DATOS -> 0
        PublishStep.FOTO -> 1
        PublishStep.PAGO -> 2
        PublishStep.EXITO -> 3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEachIndexed { index, _ ->

            val isActive = index <= currentIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isActive) AcidLime else Border
                    )
            )
        }
    }
}

// ── PASO 1: Datos de la oferta ──────────────────────────────────────

@Composable
private fun StepDatos(
    uiState: PublishUiState,
    viewModel: PublishOfferViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Text(
            "Tipo de empleo",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(
            Modifier.height(8.dp)
        )

        JobTypeSelector(
            uiState.jobTypes,
            uiState.selectedJobType
        ) {
            viewModel.selectJobType(it)
        }

        Text(
            "Tipo de contrato",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(
            Modifier.height(8.dp)
        )

        ContractTypeSelector(
            uiState.contractType
        ) {
            viewModel.updateContractType(it)
        }

        Ocupa2TextField(
            value = uiState.address,
            onValueChange = {
                viewModel.updateAddress(it)
            },
            label = "Dirección",
            placeholder = "Calle, sector, ciudad",
            leadingIcon = Icons.Filled.LocationOn
        )

        // Ubicación (lat/lng)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Ocupa2TextField(
                    value = uiState.lat?.toString() ?: "",
                    onValueChange = {
                        viewModel.updateLocation(
                            it.toDoubleOrNull() ?: 0.0,
                            uiState.lng ?: 0.0
                        )
                    },
                    label = "Latitud",
                    placeholder = "18.4861",
                    leadingIcon = Icons.Filled.MyLocation,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Ocupa2TextField(
                    value = uiState.lng?.toString() ?: "",
                    onValueChange = {
                        viewModel.updateLocation(
                            uiState.lat ?: 0.0,
                            it.toDoubleOrNull() ?: 0.0
                        )
                    },
                    label = "Longitud",
                    placeholder = "-69.9312",
                    leadingIcon = Icons.Filled.MyLocation,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        }

        Ocupa2TextField(
            value = uiState.paymentAmount,
            onValueChange = {
                viewModel.updatePaymentAmount(it)
            },
            label = "Pago ofrecido (DOP)",
            placeholder = "Según el tipo de contrato",
            leadingIcon = Icons.Filled.AttachMoney,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Ocupa2TextField(
            value = uiState.description,
            onValueChange = {
                viewModel.updateDescription(it)
            },
            label = "Descripción del trabajo",
            placeholder = "Describe las tareas a realizar",
            leadingIcon = Icons.Filled.Description
        )

        Ocupa2TextField(
            value = uiState.deadline,
            onValueChange = {
                viewModel.updateDeadline(it)
            },
            label = "Fecha límite para aplicar",
            placeholder = "yyyy-MM-dd",
            leadingIcon = Icons.Filled.CalendarToday,
            helperText = "Formato: 2026-09-30"
        )

        // Campos dinámicos según el tipo de empleo elegido
        uiState.selectedJobType?.fields?.forEach { field ->

            Ocupa2TextField(
                value = uiState.customAnswers[field.key] ?: "",
                onValueChange = {
                    viewModel.updateCustomAnswer(
                        field.key,
                        it
                    )
                },
                label = field.label,
                placeholder = field.label,
                leadingIcon = Icons.Filled.Tune
            )
        }

        Spacer(
            Modifier.height(16.dp)
        )

        AdditionalQuestionsEditor(
            uiState.additionalQuestions,
            viewModel
        )

        Spacer(
            Modifier.height(24.dp)
        )

        Ocupa2Button(
            text = "Siguiente",
            isLoading = false,
            onClick = {
                if (viewModel.validateStepDatos()) {
                    viewModel.goToStep(
                        PublishStep.FOTO
                    )
                }
            }
        )

        Spacer(
            Modifier.height(32.dp)
        )
    }
}

@Composable
private fun JobTypeSelector(
    jobTypes: List<JobTypeDetail>,
    selected: JobTypeDetail?,
    onSelect: (JobTypeDetail) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jobTypes) { jobType ->

            val isSelected =
                selected?.key == jobType.key

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) {
                            LimeSoft
                        } else {
                            Graphite
                        }
                    )
                    .clickable {
                        onSelect(jobType)
                    }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
            ) {
                Text(
                    text = jobType.name,
                    color = if (isSelected) {
                        AcidLime
                    } else {
                        IceWhite
                    },
                    fontFamily = WorkSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ContractTypeSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "temporal" to "Temporal",
        "fijo" to "Fijo",
        "horas" to "Por horas"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->

            val isSelected = selected == value

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) {
                            LimeSoft
                        } else {
                            Graphite
                        }
                    )
                    .clickable {
                        onSelect(value)
                    }
                    .padding(
                        vertical = 12.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (isSelected) {
                        AcidLime
                    } else {
                        IceWhite
                    },
                    fontFamily = WorkSansFamily,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AdditionalQuestionsEditor(
    questions: List<PublishQuestion>,
    viewModel: PublishOfferViewModel
) {
    var label by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("text")
    }

    Text(
        "Preguntas adicionales (opcional)",
        color = MutedGray,
        fontFamily = WorkSansFamily,
        fontSize = 12.sp
    )

    Spacer(
        Modifier.height(8.dp)
    )

    questions.forEachIndexed { index, question ->

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Graphite)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    question.label,
                    color = IceWhite,
                    fontFamily = WorkSansFamily,
                    fontSize = 13.sp
                )

                Text(
                    "Tipo: ${question.type}",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = {
                    viewModel.removeQuestion(index)
                }
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Eliminar",
                    tint = SolarOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    Spacer(
        Modifier.height(8.dp)
    )

    Ocupa2TextField(
        value = label,
        onValueChange = {
            label = it
        },
        label = "Nueva pregunta",
        placeholder = "Ej: ¿Tienes experiencia previa?",
        leadingIcon = Icons.Filled.Help
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "text" to "Texto",
            "date" to "Fecha",
            "select" to "Opciones",
            "check" to "Sí/No"
        ).forEach { (value, l) ->

            val isSelected = type == value

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) {
                            TealSoft
                        } else {
                            Graphite
                        }
                    )
                    .clickable {
                        type = value
                    }
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    l,
                    color = if (isSelected) {
                        ElectricTeal
                    } else {
                        IceWhite
                    },
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(
        Modifier.height(8.dp)
    )

    TextButton(
        onClick = {
            if (label.isNotBlank()) {
                viewModel.addQuestion(
                    PublishQuestion(
                        label = label,
                        type = type
                    )
                )

                label = ""
            }
        }
    ) {
        Text(
            "+ Agregar pregunta",
            color = AcidLime,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── PASO 2: Foto obligatoria ─────────────────────────────────────────

@Composable
private fun StepFoto(
    uiState: PublishUiState,
    viewModel: PublishOfferViewModel,
    context: android.content.Context
) {
    var previewBitmap by remember {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {
            val inputStream =
                context.contentResolver.openInputStream(uri)

            val file = File(
                context.cacheDir,
                "offer_photo_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }

            viewModel.setPhoto(file)

            previewBitmap =
                android.graphics.BitmapFactory.decodeFile(
                    file.absolutePath
                )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            Modifier.height(24.dp)
        )

        Text(
            "Foto del empleo",
            color = IceWhite,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
            "Obligatoria para publicar la oferta",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontSize = 12.sp
        )

        Spacer(
            Modifier.height(20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Graphite)
                .clickable {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {

            if (previewBitmap != null) {

                Image(
                    bitmap = previewBitmap!!.asImageBitmap(),
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.fillMaxSize()
                )

            } else {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.AddAPhoto,
                        contentDescription = null,
                        tint = MutedGray,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "Toca para elegir una foto",
                        color = MutedGray,
                        fontFamily = WorkSansFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(
            Modifier.height(32.dp)
        )

        Ocupa2Button(
            text = "Continuar al pago",
            isLoading = false,
            onClick = {
                viewModel.confirmPhotoAndGoToPago()
            }
        )

        Spacer(
            Modifier.height(12.dp)
        )

        TextButton(
            onClick = {
                viewModel.goToStep(
                    PublishStep.DATOS
                )
            }
        ) {
            Text(
                "Volver a datos",
                color = MutedGray,
                fontFamily = WorkSansFamily
            )
        }
    }
}

// ── PASO 3: Pago de 1 USD ────────────────────────────────────────────

@Composable
private fun StepPago(
    uiState: PublishUiState,
    viewModel: PublishOfferViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            Modifier.height(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(LimeSoft)
                .padding(16.dp)
        ) {
            Column {

                Text(
                    "Costo de publicación",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp
                )

                Text(
                    "$1.00 USD",
                    color = AcidLime,
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                Text(
                    "Tarjeta de prueba aprobada: 4242 4242 4242 4242",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(
            Modifier.height(20.dp)
        )

        Ocupa2TextField(
            value = uiState.cardholder,
            onValueChange = {
                viewModel.updateCard(
                    cardholder = it
                )
            },
            label = "Nombre en la tarjeta",
            placeholder = "Como aparece en la tarjeta",
            leadingIcon = Icons.Filled.Person
        )

        Ocupa2TextField(
            value = uiState.cardNumber,
            onValueChange = {
                viewModel.updateCard(
                    cardNumber = it
                )
            },
            label = "Número de tarjeta",
            placeholder = "4242 4242 4242 4242",
            leadingIcon = Icons.Filled.CreditCard,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Ocupa2TextField(
                    value = uiState.expMonth,
                    onValueChange = {
                        viewModel.updateCard(
                            expMonth = it
                        )
                    },
                    label = "Mes exp.",
                    placeholder = "12",
                    leadingIcon = Icons.Filled.CalendarToday,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Ocupa2TextField(
                    value = uiState.expYear,
                    onValueChange = {
                        viewModel.updateCard(
                            expYear = it
                        )
                    },
                    label = "Año exp.",
                    placeholder = "2030",
                    leadingIcon = Icons.Filled.CalendarToday,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Ocupa2TextField(
                    value = uiState.cvv,
                    onValueChange = {
                        viewModel.updateCard(
                            cvv = it
                        )
                    },
                    label = "CVV",
                    placeholder = "123",
                    leadingIcon = Icons.Filled.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    )
                )
            }
        }

        Spacer(
            Modifier.height(24.dp)
        )

        Ocupa2Button(
            text = uiState.loadingMessage ?: "Pagar y publicar",
            isLoading = uiState.isLoading,
            onClick = {
                viewModel.payAndPublish()
            }
        )

        Spacer(
            Modifier.height(12.dp)
        )

        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.goToStep(
                    PublishStep.FOTO
                )
            }
        ) {
            Text(
                "Volver a la foto",
                color = MutedGray,
                fontFamily = WorkSansFamily
            )
        }

        Spacer(
            Modifier.height(32.dp)
        )
    }
}

// ── PASO FINAL: Éxito ────────────────────────────────────────────────

@Composable
private fun StepExito(
    onPublishSuccess: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AcidLime,
                modifier = Modifier.size(64.dp)
            )

            Spacer(
                Modifier.height(16.dp)
            )

            Text(
                "¡Oferta publicada!",
                color = IceWhite,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                "Ya puedes ver tus aplicantes en Mis ofertas",
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 13.sp
            )

            Spacer(
                Modifier.height(24.dp)
            )

            Ocupa2Button(
                text = "Ver mis ofertas",
                onClick = onPublishSuccess
            )
        }
    }
}