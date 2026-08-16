package com.yeremi.ocupa2app.ui.offers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.yeremi.ocupa2app.network.models.Offer
import com.yeremi.ocupa2app.network.resolveMediaUrl
import com.yeremi.ocupa2app.ui.theme.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun OfferCard(
    offer: Offer,
    onLikeToggle: () -> Unit,
    onClick: () -> Unit
) {

    val photoUrl =
        resolveMediaUrl(
            offer.photo
        )

    Surface(
        color =
            MaterialTheme
                .colorScheme
                .surface,
        shape =
            RoundedCornerShape(16.dp),
        border =
            BorderStroke(
                1.dp,
                MaterialTheme
                    .colorScheme
                    .outlineVariant
            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            }
    ) {

        Column {

            // ─────────────────────────────────
            // FOTO DE LA OFERTA
            // ─────────────────────────────────

            if (photoUrl != null) {

                SubcomposeAsyncImage(
                    model = photoUrl,
                    contentDescription =
                        "Foto de ${offer.jobTypeName ?: "oferta"}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
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
                                modifier =
                                    Modifier.size(26.dp),
                                strokeWidth = 2.dp,
                                color = AcidLime
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

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.BrokenImage,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(32.dp),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )

                                Text(
                                    text =
                                        "Imagen no disponible",
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

            // ─────────────────────────────────
            // INFORMACIÓN
            // ─────────────────────────────────

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Text(
                        text =
                            offer.jobTypeName
                                ?: "Oferta",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        modifier =
                            Modifier.weight(1f)
                    )

                    offer.contractType
                        ?.let { type ->

                            SemanticChip(
                                text =
                                    translateContractType(
                                        type
                                    ).uppercase(),
                                containerColor =
                                    LimeSoft,
                                contentColor =
                                    AcidLime,
                                borderColor =
                                    LimeBorder
                            )
                        }
                }

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                if (
                    !offer.description
                        .isNullOrBlank()
                ) {

                    Text(
                        text =
                            offer.description,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        maxLines = 2,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }

                HorizontalDivider(
                    color =
                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // SALARIO

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                offer.payment?.let {
                                    "${it.amount ?: 0.0} ${it.currency ?: "DOP"}"
                                } ?: "–",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                offer.payment
                                    ?.period
                                    ?.let {
                                        translatePeriod(
                                            it
                                        )
                                    }
                                    ?: "",
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

                    VerticalDivider(
                        modifier =
                            Modifier
                                .height(32.dp)
                                .padding(
                                    horizontal = 8.dp
                                ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                    )

                    // CONTRATO

                    Row(
                        modifier =
                            Modifier.weight(1f),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Schedule,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            modifier =
                                Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                translateContractType(
                                    offer.contractType
                                        ?: ""
                                ),
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

                    VerticalDivider(
                        modifier =
                            Modifier
                                .height(32.dp)
                                .padding(
                                    horizontal = 8.dp
                                ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                    )

                    // UBICACIÓN

                    Row(
                        modifier =
                            Modifier.weight(1f),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.LocationOn,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            modifier =
                                Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                offer.address
                                    ?: "–",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                // PARTE INFERIOR

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    val daysLeft =
                        calculateDays(
                            offer.deadline
                                ?: ""
                        )

                    SemanticChip(
                        text =
                            if (daysLeft > 0) {
                                "Vence en $daysLeft días"
                            } else {
                                "Vence hoy"
                            },
                        containerColor =
                            if (daysLeft <= 3) {
                                OrangeSoft
                            } else {
                                LimeSoft
                            },
                        contentColor =
                            if (daysLeft <= 3) {
                                SolarOrange
                            } else {
                                AcidLime
                            },
                        borderColor =
                            if (daysLeft <= 3) {
                                OrangeBorder
                            } else {
                                LimeBorder
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )

                    Text(
                        text =
                            "${offer.applicantsCount} aplicantes",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    IconButton(
                        onClick =
                            onLikeToggle
                    ) {

                        Icon(
                            imageVector =
                                if (
                                    offer.likedByMe
                                ) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                            contentDescription =
                                "Favorito",
                            tint =
                                if (
                                    offer.likedByMe
                                ) {
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SemanticChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color
) {

    Surface(
        color = containerColor,
        shape =
            RoundedCornerShape(100.dp),
        border =
            BorderStroke(
                1.dp,
                borderColor
            )
    ) {

        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 2.dp
                ),
            color = contentColor,
            fontFamily =
                RajdhaniFamily,
            fontWeight =
                FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

private fun translateContractType(
    type: String
): String =
    when (type.lowercase()) {

        "fijo",
        "fixed" ->
            "Fijo"

        "temporal",
        "temporary" ->
            "Temporal"

        "por horas",
        "hourly" ->
            "Por horas"

        else ->
            type
    }

private fun translatePeriod(
    period: String
): String =
    when (period.lowercase()) {

        "mensual" ->
            "mensual"

        "quincenal" ->
            "quincenal"

        "semanal" ->
            "semanal"

        "diario" ->
            "diario"

        else ->
            period
    }

private fun calculateDays(
    deadline: String
): Int {

    return try {

        val date =
            if (
                deadline.contains("T")
            ) {
                OffsetDateTime
                    .parse(deadline)
                    .toLocalDate()
            } else {
                LocalDate.parse(
                    deadline,
                    DateTimeFormatter
                        .ISO_LOCAL_DATE
                )
            }

        ChronoUnit.DAYS
            .between(
                LocalDate.now(),
                date
            )
            .toInt()
            .coerceAtLeast(0)

    } catch (_: Exception) {

        0
    }
}