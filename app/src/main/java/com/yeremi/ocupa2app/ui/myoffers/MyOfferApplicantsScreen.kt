@file:OptIn(ExperimentalMaterial3Api::class)
package com.yeremi.ocupa2app.ui.myoffers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.network.models.Applicant
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun MyOfferApplicantsScreen(
    offerId: String,
    viewModel: MyOffersViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(offerId) {
        viewModel.loadApplicants(offerId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        TopAppBar(
            title = {
                Text(
                    "Aplicantes",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    color = IceWhite
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
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

        val currentOffer = uiState.myOffers.firstOrNull { it.id == offerId }
        if (currentOffer != null) {
            OfferSummaryCard(currentOffer)
        }

        when {

            uiState.isLoading && uiState.applicants.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AcidLime
                    )
                }
            }

            uiState.applicants.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Todavía nadie ha aplicado a esta oferta.",
                        color = MutedGray,
                        fontFamily = WorkSansFamily
                    )
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(uiState.applicants) { applicant ->

                        ApplicantCard(
                            applicant = applicant,
                            isBusy = uiState.actionInProgressId == applicant.id,

                            onRate = { rating ->
                                viewModel.rateApplicant(
                                    offerId,
                                    applicant.id,
                                    rating
                                )
                            },

                            onDiscard = {
                                viewModel.discardApplicant(
                                    offerId,
                                    applicant.id
                                )
                            },

                            onMarkFinalist = {
                                viewModel.markFinalist(
                                    offerId,
                                    applicant.id
                                )
                            },

                            onChooseWinner = {
                                viewModel.chooseWinner(
                                    offerId,
                                    applicant.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    applicant: Applicant,
    isBusy: Boolean,
    onRate: (Int) -> Unit,
    onDiscard: () -> Unit,
    onMarkFinalist: () -> Unit,
    onChooseWinner: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = applicant.applicantName ?: "Aplicante",
                    color = IceWhite,
                    fontFamily = WorkSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                ApplicantStatusBadge(
                    applicant.status
                )
            }

            if (isBusy) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = AcidLime,
                    strokeWidth = 2.dp
                )
            }
        }

        if (!applicant.comment.isNullOrBlank()) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "\"${applicant.comment}\"",
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 13.sp
            )
        }

        if (applicant.answers.isNotEmpty()) {
            Spacer(
                modifier = Modifier.height(10.dp)
            )

            applicant.answers.forEach { answer ->

                Text(
                    text = "• ${answer.value}",
                    color = IceWhite.copy(alpha = 0.8f),
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Calificación con estrellas
        Row {
            for (i in 1..5) {

                Icon(
                    imageVector =
                        if ((applicant.rating ?: 0) >= i) {
                            Icons.Filled.Star
                        } else {
                            Icons.Filled.StarBorder
                        },

                    contentDescription = "Calificar $i",

                    tint = SolarOrange,

                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            enabled = !isBusy
                        ) {
                            onRate(i)
                        }
                )
            }
        }

        // Botones de acción
        if (
            applicant.status != "discarded" &&
            applicant.status != "winner"
        ) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = onDiscard,
                    enabled = !isBusy,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SolarOrange
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Descartar",
                        fontFamily = WorkSansFamily,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onMarkFinalist,
                    enabled = !isBusy,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricTeal
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Finalista",
                        fontFamily = WorkSansFamily,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onChooseWinner,
                enabled = !isBusy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AcidLime,
                    contentColor = Obsidian
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Elegir ganador (crea contrato)",
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ApplicantStatusBadge(
    status: String
) {
    val (label, color) = when (status) {

        "winner" ->
            "Ganador" to AcidLime

        "finalist" ->
            "Finalista" to ElectricTeal

        "discarded" ->
            "Descartado" to SolarOrange

        else ->
            "En revisión" to MutedGray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                color.copy(alpha = 0.15f)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 3.dp
            )
    ) {
        Text(
            text = label,
            color = color,
            fontFamily = WorkSansFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OfferSummaryCard(offer: com.yeremi.ocupa2app.network.models.MyOffer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!offer.photo.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = offer.photo,
                contentDescription = offer.jobTypeKey,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = offer.jobTypeKey?.replaceFirstChar { it.uppercase() } ?: "Oferta",
                color = IceWhite,
                fontFamily = WorkSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            if (!offer.description.isNullOrBlank()) {
                Text(
                    text = offer.description,
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            if (!offer.deadline.isNullOrBlank()) {
                Text(
                    text = "Vence: ${offer.deadline}",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 11.sp
                )
            }
        }
    }
}