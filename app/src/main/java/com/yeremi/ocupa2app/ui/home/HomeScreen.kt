package com.yeremi.ocupa2app.ui.home
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Campaign
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
import kotlinx.coroutines.delay

// Contenido del slider — texto/íconos locales, no requiere API.
private data class WelcomeSlide(
    val title: String,
    val message: String,
    val accent: Color,
    val icon: ImageVector
)

private val slides = listOf(
    WelcomeSlide(
        title = "Bienvenido a OCUPA2",
        message = "Encuentra empleos temporales cerca de ti o publica el tuyo en minutos.",
        accent = AcidLime,
        icon = Icons.Filled.Campaign
    ),
    WelcomeSlide(
        title = "Publica con confianza",
        message = "Tu identidad permanece oculta hasta que eliges a tu finalista ganador.",
        accent = ElectricTeal,
        icon = Icons.Filled.Campaign
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
    onNavigateToAbout: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-scroll del slider cada 4 segundos
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val next = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "OCUPA2",
            color = IceWhite,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = "Empleos temporales, sin complicaciones",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Slider ─────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 20.dp)
        ) { page ->
            WelcomeSlideCard(slides[page])
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(slides.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) AcidLime else Border)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "ACCESOS RÁPIDOS",
            color = MutedGray,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickActions(onNavigateToExplore, onNavigateToNews, onNavigateToPublish, onNavigateToMyOffers, onNavigateToAbout)) { action ->
                QuickActionCard(action)
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
    onAbout: () -> Unit
) = listOf(
    QuickAction("Explorar ofertas", Icons.Filled.Campaign, AcidLime, onExplore),
    QuickAction("Publicar oferta", Icons.Filled.Campaign, SolarOrange, onPublish),
    QuickAction("Mis ofertas", Icons.Filled.Campaign, ElectricTeal, onMyOffers),
    QuickAction("Noticias", Icons.Filled.Article, ElectricTeal, onNews),
    QuickAction("Acerca de", Icons.Filled.Campaign, MutedGray, onAbout)
)

@Composable
private fun QuickActionCard(action: QuickAction) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .clickableNoRipple(action.onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(action.accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon, contentDescription = null, tint = action.accent, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = action.label,
            color = IceWhite,
            fontFamily = WorkSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun WelcomeSlideCard(slide: WelcomeSlide) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(slide.accent.copy(alpha = 0.18f), Graphite)
                )
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(slide.accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(slide.icon, contentDescription = null, tint = slide.accent, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = slide.title,
                color = IceWhite,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = slide.message,
                color = MutedGray,
                fontFamily = WorkSansFamily,
                fontSize = 13.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

// Helper para clickable sin ripple, evita import extra en cada archivo
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    return this.then(
        Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    )
}