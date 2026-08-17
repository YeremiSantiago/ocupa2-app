package com.yeremi.ocupa2app.ui.applications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.yeremi.ocupa2app.network.models.MyApplicationDetail
import com.yeremi.ocupa2app.network.resolveMediaUrl
import com.yeremi.ocupa2app.ui.theme.AcidLime
import com.yeremi.ocupa2app.ui.theme.ElectricTeal
import com.yeremi.ocupa2app.ui.theme.SolarOrange

@Composable
fun MyApplicationsScreen(
    viewModel: MyApplicationsViewModel,
    onNavigateToOffer: (String) -> Unit
) {

    val state by
    viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadApplications()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "Mis aplicaciones",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Consulta el estado de los empleos a los que aplicaste",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            IconButton(
                onClick =
                    viewModel::loadApplications
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription =
                        "Actualizar"
                )
            }
        }

        when {

            state.isLoading -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
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
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text =
                                state.error!!
                        )

                        Button(
                            onClick =
                                viewModel::loadApplications
                        ) {

                            Text(
                                "Reintentar"
                            )
                        }
                    }
                }
            }

            state.applications
                .isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "Todavía no has aplicado a ninguna oferta."
                    )
                }
            }

            else -> {

                LazyColumn(
                    contentPadding =
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 100.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items =
                            state.applications,
                        key = {
                            it.id
                        }
                    ) { application ->

                        ApplicationCard(
                            application =
                                application,
                            onClick = {

                                val id =
                                    application
                                        .offer
                                        ?.id
                                        ?: application
                                            .offerId

                                if (
                                    !id.isNullOrBlank()
                                ) {

                                    onNavigateToOffer(
                                        id
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationCard(
    application:
    MyApplicationDetail,
    onClick: () -> Unit
) {

    val offer =
        application.offer

    val title =
        offer?.jobTypeName
            ?: offer?.jobTypeKey
            ?: "Oferta"

    val photoUrl =
        resolveMediaUrl(
            offer?.photo
        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape =
            RoundedCornerShape(16.dp)
    ) {

        Column {

            // FOTO

            if (photoUrl != null) {

                SubcomposeAsyncImage(
                    model = photoUrl,
                    contentDescription =
                        "Foto de $title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp),
                    contentScale =
                        ContentScale.Crop,
                    loading = {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            CircularProgressIndicator(
                                color = AcidLime,
                                modifier =
                                    Modifier.size(25.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.BrokenImage,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                                modifier =
                                    Modifier.size(34.dp)
                            )
                        }
                    }
                )
            }

            // DATOS

            Column(
                modifier =
                    Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {

                Text(
                    text = title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                offer?.description
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?.let { description ->

                        Text(
                            text = description,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            maxLines = 2
                        )
                    }

                offer?.address?.let {
                        address ->

                    Text(
                        text = address,
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

                ApplicationStatusChip(
                    status =
                        application.status
                )

                application.appliedAt
                    ?.let {
                            appliedAt ->

                        Text(
                            text =
                                "Aplicaste: ${appliedAt.take(10)}",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
            }
        }
    }
}

@Composable
private fun ApplicationStatusChip(
    status: String
) {

    val normalized =
        status.lowercase()

    val label =
        when (normalized) {

            "applied",
            "review",
            "pending",
            "in_review" ->
                "En revisión"

            "discarded",
            "rejected" ->
                "Descartado"

            "finalist" ->
                "Finalista"

            "winner",
            "won" ->
                "Ganador"

            else ->
                status.replaceFirstChar {
                    it.uppercaseChar()
                }
        }

    val color =
        when (normalized) {

            "winner",
            "won" ->
                AcidLime

            "finalist" ->
                ElectricTeal

            "discarded",
            "rejected" ->
                SolarOrange

            else ->
                MaterialTheme
                    .colorScheme
                    .primary
        }

    Surface(
        color =
            color.copy(
                alpha = 0.14f
            ),
        shape =
            RoundedCornerShape(
                100.dp
            )
    ) {

        Text(
            text = label,
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
            color = color,
            style =
                MaterialTheme
                    .typography
                    .labelMedium,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}