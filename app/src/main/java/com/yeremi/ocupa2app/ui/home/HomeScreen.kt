package com.yeremi.ocupa2app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yeremi.ocupa2app.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

private data class WelcomeSlide(
    val title: String,
    val message: String,
    val accent: Color,
    val icon: ImageVector
)

private val slides = listOf(
    WelcomeSlide(
        title = "Bienvenida a OCUPA2",
        message = "Encuentra empleos temporales cerca de ti o publica el tuyo en minutos.",
        accent = AcidLime,
        icon = Icons.Filled.Campaign
    ),
    WelcomeSlide(
        title = "Publica con confianza",
        message = "Tu identidad permanece oculta hasta que eliges a tu finalista ganador.",
        accent = ElectricTeal,
        icon = Icons.Filled.Security
    ),
    WelcomeSlide(
        title = "Mantente informado",
        message = "Noticias y videos educativos sobre oficios y capacitación, siempre a mano.",
        accent = SolarOrange,
        icon = Icons.Filled.Article
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToPublish: () -> Unit,
    onNavigateToMyOffers: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Auto-scroll del slider cada 4 segundos
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val next = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(next)
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "¡Buenos días!"
            in 12..18 -> "¡Buenas tardes!"
            else -> "¡Buenas noches!"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 }
        ) {
            Column {
                Text(
                    text = greeting,
                    color = IceWhite,
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Text(
                    text = "OCUPA2 • Empleos sin complicaciones",
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Slider ─────────────────────────────
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(tween(600)) { it / 4 }
        ) {
            Column {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(horizontal = 20.dp)
                ) { page ->
                    WelcomeSlideCard(slides[page], onNavigateToExplore)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) AcidLime else Border)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, delayMillis = 200))
        ) {
            Text(
                text = "ACCESOS RÁPIDOS",
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val actions = quickActions(
            onExplore = onNavigateToExplore,
            onNews = onNavigateToNews,
            onPublish = onNavigateToPublish,
            onMyOffers = onNavigateToMyOffers,
            onAbout = onNavigateToAbout,
            onProfile = onNavigateToProfile
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(actions) { index, action ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 300 + (index * 50))) + slideInVertically(tween(400, delayMillis = 300 + (index * 50))) { it / 2 }
                ) {
                    QuickActionCard(action)
                }
            }
        }
    }
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit
)

private fun quickActions(
    onExplore: () -> Unit,
    onNews: () -> Unit,
    onPublish: () -> Unit,
    onMyOffers: () -> Unit,
    onAbout: () -> Unit,
    onProfile: () -> Unit
) = listOf(
    QuickAction("Buscar Empleos", Icons.Filled.Search, AcidLime, onExplore),
    QuickAction("Publicar Oferta", Icons.Filled.PostAdd, SolarOrange, onPublish),
    QuickAction("Mis Ofertas", Icons.Filled.ListAlt, ElectricTeal, onMyOffers),
    QuickAction("Mi Perfil", Icons.Filled.Person, AcidLime, onProfile),
    QuickAction("Noticias", Icons.Filled.Article, ElectricTeal, onNews),
    QuickAction("Acerca de", Icons.Filled.Info, MutedGray, onAbout)
)

@Composable
private fun QuickActionCard(action: QuickAction) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Graphite, Graphite.copy(alpha = 0.8f))
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = action.onClick
            )
            .padding(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(action.accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon, contentDescription = null, tint = action.accent, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = action.label,
            color = IceWhite,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun WelcomeSlideCard(slide: WelcomeSlide, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.98f else 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(slide.accent.copy(alpha = 0.2f), Graphite)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(slide.accent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(slide.icon, contentDescription = null, tint = slide.accent, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = slide.title,
                color = IceWhite,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = slide.message,
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}