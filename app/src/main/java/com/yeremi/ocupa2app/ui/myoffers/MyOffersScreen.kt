@file:OptIn(ExperimentalMaterial3Api::class)
package com.yeremi.ocupa2app.ui.myoffers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yeremi.ocupa2app.network.models.MyOffer
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun MyOffersScreen(
    viewModel: MyOffersViewModel,
    onNavigateToApplicants: (String) -> Unit,
    onNavigateToPublish: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyOffers()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis ofertas publicadas",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        color = IceWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPublish,
                containerColor = AcidLime,
                contentColor = Obsidian
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Publicar nueva oferta"
                )
            }
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
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

            uiState.myOffers.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Aún no has publicado ninguna oferta.",
                            color = MutedGray,
                            fontFamily = WorkSansFamily
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.myOffers) { offer ->
                        MyOfferCard(
                            offer = offer,
                            onClick = {
                                onNavigateToApplicants(offer.id)
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyOfferCard(
    offer: MyOffer,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!offer.photo.isNullOrBlank()) {
            AsyncImage(
                model = offer.photo,
                contentDescription = offer.jobTypeKey,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GraphiteRaise)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = offer.jobTypeKey
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Oferta",
                color = IceWhite,
                fontFamily = WorkSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            if (!offer.description.isNullOrBlank()) {
                Text(
                    text = offer.description,
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            StatusBadge(
                active = offer.active
            )
        }
    }
}

@Composable
private fun StatusBadge(
    active: Boolean
) {
    val (label, color) =
        if (active) {
            "Activa" to ElectricTeal
        } else {
            "Desactivada" to MutedGray
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