package com.yeremi.ocupa2app.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.components.*
import com.yeremi.ocupa2app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    onNavigateBack: () -> Unit
) {
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cambiar Contraseña",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isSuccess) {
                SuccessState(onContinue = onNavigateBack)
            } else {
                FormContent(
                    viewModel = viewModel,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

@Composable
fun FormContent(
    viewModel: ChangePasswordViewModel,
    newPassword: String,
    confirmPassword: String,
    isLoading: Boolean,
    errorMessage: String?
) {
    val scrollState = rememberScrollState()
    
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Security,
                contentDescription = null,
                tint = AcidLime,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actualiza tu contraseña",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa una nueva contraseña segura para tu cuenta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column {
            PasswordInputField(
                label = "NUEVA CONTRASEÑA",
                value = newPassword,
                onValueChange = { viewModel.onNewPasswordChanged(it) },
                isVisible = newVisible,
                onToggleVisibility = { newVisible = !newVisible }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            StrengthIndicator(
                strength = viewModel.passwordStrength,
                password = newPassword
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PasswordInputField(
            label = "CONFIRMAR NUEVA CONTRASEÑA",
            value = confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            isVisible = confirmVisible,
            onToggleVisibility = { confirmVisible = !confirmVisible },
            trailingIcon = {
                if (confirmPassword.isNotEmpty()) {
                    val isMatch = confirmPassword == newPassword
                    Icon(
                        if (isMatch) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (isMatch) ElectricTeal else SolarOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        RequirementsCard(
            minLength = viewModel.requirementMinLength,
            hasUpper = viewModel.requirementUppercase,
            hasNumber = viewModel.requirementNumber
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = SolarOrange,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        Ocupa2Button(
            text = "ACTUALIZAR CONTRASEÑA",
            onClick = { viewModel.changePassword() },
            isLoading = isLoading,
            enabled = viewModel.isFormValid,
            containerColor = if (viewModel.isFormValid) AcidLime else GraphiteHigh,
            contentColor = if (viewModel.isFormValid) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp, 
                    if (isError) SolarOrange else MaterialTheme.colorScheme.outlineVariant, 
                    RoundedCornerShape(12.dp)
                ),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AcidLime
            )
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = SolarOrange,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StrengthIndicator(strength: ChangePasswordViewModel.PasswordStrength, password: String) {
    androidx.compose.animation.AnimatedVisibility(visible = password.isNotEmpty()) {
        val segments = 3
        val activeSegments = when(strength) {
            ChangePasswordViewModel.PasswordStrength.WEAK -> 1
            ChangePasswordViewModel.PasswordStrength.MEDIUM -> 2
            ChangePasswordViewModel.PasswordStrength.STRONG -> 3
        }
        
        val color = when(strength) {
            ChangePasswordViewModel.PasswordStrength.WEAK -> SolarOrange
            ChangePasswordViewModel.PasswordStrength.MEDIUM -> AcidLime
            ChangePasswordViewModel.PasswordStrength.STRONG -> ElectricTeal
        }
        
        val label = when(strength) {
            ChangePasswordViewModel.PasswordStrength.WEAK -> "Seguridad: Débil"
            ChangePasswordViewModel.PasswordStrength.MEDIUM -> "Seguridad: Media"
            ChangePasswordViewModel.PasswordStrength.STRONG -> "Seguridad: Fuerte"
        }

        Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..segments) {
                    val segmentColor by animateColorAsState(
                        targetValue = if (i <= activeSegments) color else MaterialTheme.colorScheme.surfaceVariant,
                        label = "segment_color"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(segmentColor)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = label,
                    color = color,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RequirementsCard(minLength: Boolean, hasUpper: Boolean, hasNumber: Boolean) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Requisitos",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            RequirementItem(text = "Mínimo 6 caracteres", isMet = minLength)
            RequirementItem(text = "Al menos una mayúscula", isMet = hasUpper)
            RequirementItem(text = "Al menos un número", isMet = hasNumber)
        }
    }
}

@Composable
fun RequirementItem(text: String, isMet: Boolean) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) ElectricTeal else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SuccessState(onContinue: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.75f,
                stiffness = 200f
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                .background(TealSoft, CircleShape)
                .border(2.dp, TealBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = ElectricTeal,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡Contraseña actualizada!",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu contraseña ha sido cambiada exitosamente",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = TealSoft,
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(1.dp, TealBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(ElectricTeal, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sesión segura activa",
                    color = ElectricTeal,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SuccessInfoItem(text = "Cierre de sesión automático en otros dispositivos")
                Spacer(modifier = Modifier.height(12.dp))
                SuccessInfoItem(text = "Próximo inicio de sesión requerirá la nueva contraseña")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Ocupa2Button(
            text = "CONTINUAR",
            onClick = onContinue
        )
    }
}

@Composable
fun SuccessInfoItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = ElectricTeal,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
