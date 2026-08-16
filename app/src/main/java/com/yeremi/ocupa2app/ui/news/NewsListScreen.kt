@file:OptIn(ExperimentalMaterial3Api::class)
package com.yeremi.ocupa2app.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yeremi.ocupa2app.network.models.NewsItem
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun NewsListScreen(
    viewModel: NewsViewModel,
    onNavigateToDetail: (Int) -> Unit,   // manda el INDEX, no un id (no existe id)
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadNews() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Noticias", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = IceWhite) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = IceWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AcidLime)
            }
            uiState.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.errorMessage ?: "", color = MutedGray, fontFamily = WorkSansFamily)
            }
            uiState.news.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no hay noticias.", color = MutedGray, fontFamily = WorkSansFamily)
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.news) { index, news ->
                    NewsCard(news = news, onClick = { onNavigateToDetail(index) })
                }
            }
        }
    }
}

@Composable
private fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .clickable(onClick = onClick)
    ) {
        if (!news.image.isNullOrBlank()) {
            AsyncImage(
                model = news.image,
                contentDescription = news.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(GraphiteRaise),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Newspaper, contentDescription = null, tint = MutedGray)
            }
        }

        Column(modifier = Modifier.padding(14.dp)) {
            if (!news.source.isNullOrBlank()) {
                Text(
                    text = news.source.uppercase(),
                    color = AcidLime,
                    fontFamily = WorkSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = news.title,
                color = IceWhite,
                fontFamily = WorkSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            if (!news.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.summary,
                    color = MutedGray,
                    fontFamily = WorkSansFamily,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
        }
    }
}