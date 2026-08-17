package com.yeremi.ocupa2app.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yeremi.ocupa2app.network.models.Payment
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal
import com.yeremi.ocupa2app.ui.theme.SolarOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    viewModel: PaymentsViewModel,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPayments()
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Mis pagos")
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
                        onClick = viewModel::loadPayments
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
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
                            onClick = viewModel::loadPayments
                        ) {

                            Text("Reintentar")
                        }
                    }
                }
            }

            state.payments.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No tienes pagos registrados.",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                            text = "Historial de pagos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Aquí aparecen los pagos realizados en la plataforma.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(
                        items = state.payments,
                        key = { payment ->
                            payment.id
                        }
                    ) { payment ->

                        PaymentCard(
                            payment = payment
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(
    payment: Payment
) {

    val normalizedStatus =
        payment.status
            .orEmpty()
            .lowercase()

    val statusText =
        when (normalizedStatus) {

            "paid",
            "approved",
            "success",
            "succeeded",
            "completed" ->
                "Completado"

            "failed",
            "declined",
            "rejected" ->
                "Fallido"

            "pending" ->
                "Pendiente"

            else ->
                payment.status
                    ?.replaceFirstChar { character ->
                        character.uppercaseChar()
                    }
                    ?: "Procesado"
        }

    val statusColor =
        when (normalizedStatus) {

            "paid",
            "approved",
            "success",
            "succeeded",
            "completed" ->
                ElectricTeal

            "failed",
            "declined",
            "rejected" ->
                SolarOrange

            else ->
                AcidLime
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(
                    alpha = 0.12f
                )
            ) {

                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(26.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                Text(
                    text = buildString {

                        append(
                            payment.amount ?: 0.0
                        )

                        append(" ")

                        append(
                            payment.currency ?: "USD"
                        )
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                payment.createdAt?.let { createdAt ->

                    Text(
                        text = createdAt.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "#${payment.id.takeLast(6)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}