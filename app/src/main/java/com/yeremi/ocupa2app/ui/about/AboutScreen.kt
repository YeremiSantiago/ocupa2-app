@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeremi.ocupa2app.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.yeremi.ocupa2app.R
import com.yeremi.ocupa2app.ui.theme.*
import kotlinx.coroutines.delay

data class TeamMember(
    val name: String,
    val matricula: String,
    val phone: String,            // formato con código de país, ej: "+18095551234"
    val telegramUsername: String, // sin @ y sin link, solo el usuario
    val accent: Color,
    val photoRes: Int? = null     // ID de recurso local (R.drawable.xxx)
)

private val teamMembers = listOf(
    TeamMember(
        name = "Diomar Arianny",
        matricula = "2024-1872",
        phone = "+18097538841",
        telegramUsername = "diomarfleming",
        accent = AcidLime,
        photoRes = R.drawable.team_diomar
    ),
    TeamMember(
        name = "Jeremy Santiago Hernández",
        matricula = "2024-1504",
        phone = "+18492014771",
        telegramUsername = "Yeremixs151",
        accent = ElectricTeal,
        photoRes = R.drawable.team_jeremy
    ),
    TeamMember(
        name = "Zoibe Mesa González",
        matricula = "2024-1831",
        phone = "+18099103642",
        telegramUsername = "ZoibeMesa",
        accent = SolarOrange,
        photoRes = R.drawable.team_zoibe
    )
)

@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text("Acerca de", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = IceWhite)
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = IceWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        LazyColumn {
            item { HeroHeader() }

            item {
                Text(
                    text = "EQUIPO DE DESARROLLO",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            itemsIndexed(teamMembers) { index, member ->
                AnimatedTeamMemberCard(member = member, index = index)
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
                FooterChip()
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun HeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(AcidLime.copy(alpha = 0.15f), MaterialTheme.colorScheme.background)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            // Imagen del equipo principal
            Image(
                painter = painterResource(R.drawable.team_group),
                contentDescription = "Equipo Ocupa2",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, AcidLime.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                    .background(Graphite),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "OCUPA2",
                color = IceWhite,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

            Text(
                text = "Hecho con dedicación por 3 estudiantes del ITLA",
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

// Cada tarjeta entra con un pequeño delay escalonado según su posición (index),
// deslizándose hacia arriba y apareciendo con fade — le da vida a la lista sin
// exagerar ni distraer.
@Composable
private fun AnimatedTeamMemberCard(member: TeamMember, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(350)) +
                slideInVertically(animationSpec = tween(350)) { fullHeight -> fullHeight / 4 }
    ) {
        TeamMemberCard(member)
    }
}

@Composable
private fun TeamMemberCard(member: TeamMember) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Graphite)
    ) {
        // Barra de acento arriba de la tarjeta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(member.accent)
        )

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(member.accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                if (member.photoRes != null) {
                    Image(
                        painter = painterResource(member.photoRes),
                        contentDescription = member.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = member.name.take(1).uppercase(),
                        color = member.accent,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    color = IceWhite,
                    fontFamily = WorkSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Matrícula ${member.matricula}",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}")))
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(member.accent.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Filled.Call, contentDescription = "Llamar a ${member.name}", tint = member.accent, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/${member.telegramUsername}")))
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(TealSoft)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Telegram de ${member.name}", tint = ElectricTeal, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun FooterChip() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(GraphiteRaise)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Proyecto Final · ITLA · 2026",
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}