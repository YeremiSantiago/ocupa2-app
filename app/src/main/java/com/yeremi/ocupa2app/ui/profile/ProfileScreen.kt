package com.yeremi.ocupa2app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel:
    CompleteProfileViewModel,

    onNavigateToChangePassword:
        () -> Unit,

    onNavigateToExperiences:
        () -> Unit,

    onNavigateToPayments:
        () -> Unit,

    onNavigateToVideos:
        () -> Unit,

    onLogout:
        () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = (uiState as? CompleteProfileViewModel.ProfileState.ProfileLoaded)?.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header con gradiente ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GraphiteRaise, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(top = 48.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Avatar con iniciales
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(LimeSoft, GraphiteHigh))
                        )
                        .border(2.dp, LimeBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = buildString {
                        user?.firstName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                        user?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                    }.ifEmpty { "?" }
                    Text(
                        text = initials,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = AcidLime
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre
                Text(
                    text = user?.let { "${it.firstName ?: ""} ${it.lastName ?: ""}".trim() }
                        ?: "Cargando...",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Email / correo
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge "Perfil completo"
                if (user?.profileCompleted == true) {
                    Surface(
                        color = TealSoft,
                        shape = RoundedCornerShape(100.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = ElectricTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Perfil verificado",
                                color = ElectricTeal,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ── Datos personales ─────────────────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))
        SectionLabel(text = "DATOS PERSONALES")

        ProfileInfoCard {
            user?.cedula?.let {
                ProfileInfoRow(
                    icon = Icons.Outlined.CreditCard,
                    label = "Cédula",
                    value = it
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            user?.gender?.let {
                ProfileInfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Género",
                    value = it.replaceFirstChar { c -> c.uppercaseChar() }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            user?.birthDate?.let {
                val datePart = it.take(10) // yyyy-MM-dd
                ProfileInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Fecha de nacimiento",
                    value = datePart
                )
            }
        }

        // ── Mi actividad ─────────────────────────────────────────────

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        SectionLabel(
            text =
                "MI ACTIVIDAD"
        )

        ProfileActionCard(
            icon =
                Icons.Outlined.WorkHistory,
            title =
                "Experiencias",
            subtitle =
                "Agrega experiencia y certificados",
            accentColor =
                ElectricTeal,
            onClick =
                onNavigateToExperiences
        )

        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )

        ProfileActionCard(
            icon =
                Icons.Outlined.ReceiptLong,
            title =
                "Mis pagos",
            subtitle =
                "Consulta tu historial de pagos",
            accentColor =
                AcidLime,
            onClick =
                onNavigateToPayments
        )

        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )

        ProfileActionCard(
            icon =
                Icons.Outlined.PlayCircle,
            title =
                "Videos",
            subtitle =
                "Capacitación y tutoriales",
            accentColor =
                SolarOrange,
            onClick =
                onNavigateToVideos
        )

        // ── Seguridad ─────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel(text = "SEGURIDAD")

        ProfileActionCard(
            icon = Icons.Outlined.Lock,
            title = "Cambiar contraseña",
            subtitle = "Actualiza tu clave de acceso",
            accentColor = AcidLime,
            onClick = onNavigateToChangePassword
        )

        // ── Sesión ────────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel(text = "SESIÓN")

        ProfileActionCard(
            icon = Icons.Outlined.Logout,
            title = "Cerrar sesión",
            subtitle = "Salir de tu cuenta",
            accentColor = SolarOrange,
            showChevron = false,
            onClick = onLogout
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Matrícula en el pie
        user?.referralMatricula?.let { matricula ->
            Text(
                text = "Matrícula: $matricula",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(100.dp)) // espacio para la navbar
    }
}

// ── Componentes auxiliares ────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileInfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MutedGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfileActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono con fondo accentuado
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showChevron) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MutedGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
