package com.yeremi.ocupa2app.ui.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.network.models.Offer
import com.yeremi.ocupa2app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun OfferCard(
    offer: Offer,
    onLikeToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Título + Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    if (offer.isUrgent) {
                        SemanticChip(text = "URGENTE", containerColor = OrangeSoft, contentColor = SolarOrange, borderColor = OrangeBorder)
                    }
                    if (offer.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        SemanticChip(text = "VERIFICADO", containerColor = TealSoft, contentColor = ElectricTeal, borderColor = TealBorder)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${offer.jobType.name} · ${translateContractType(offer.contractType)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila de 3 datos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RD$ ${offer.salary.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = offer.salaryPeriod,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = translateContractType(offer.contractType),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = offer.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Deadline chip logic
                SemanticChip(
                    text = "Vence en ${calculateDays(offer.deadline)} días",
                    containerColor = if (calculateDays(offer.deadline) <= 3) OrangeSoft else LimeSoft,
                    contentColor = if (calculateDays(offer.deadline) <= 3) SolarOrange else AcidLime,
                    borderColor = if (calculateDays(offer.deadline) <= 3) OrangeBorder else LimeBorder
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${offer.applicantsCount} aplicantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onLikeToggle) {
                    Icon(
                        imageVector = if (offer.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (offer.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SemanticChip(text: String, containerColor: Color, contentColor: Color, borderColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = contentColor,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

private fun translateContractType(type: String): String = when(type) {
    "TEMPORARY" -> "Temporal"
    "FIXED" -> "Fijo"
    "HOURLY" -> "Por horas"
    else -> type
}

private fun calculateDays(deadline: String): Int {
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val deadlineDate = LocalDate.parse(deadline, formatter)
        val today = LocalDate.now()
        ChronoUnit.DAYS.between(today, deadlineDate).toInt().coerceAtLeast(0)
    } catch (e: Exception) {
        0
    }
}
