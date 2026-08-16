@file:OptIn(ExperimentalMaterial3Api::class)
package com.yeremi.ocupa2app.ui.news

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yeremi.ocupa2app.ui.components.Ocupa2Button
import com.yeremi.ocupa2app.ui.theme.*

@Composable
fun NewsDetailScreen(
    newsIndex: Int,
    viewModel: NewsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(newsIndex) { viewModel.selectNews(newsIndex) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Noticia", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = IceWhite) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = IceWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        val news = uiState.selectedNews
        if (news == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró la noticia.", color = MutedGray, fontFamily = WorkSansFamily)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            if (!news.image.isNullOrBlank()) {
                AsyncImage(
                    model = news.image,
                    contentDescription = news.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Column(modifier = Modifier.padding(20.dp)) {
                if (!news.source.isNullOrBlank()) {
                    Text(
                        text = news.source.uppercase(),
                        color = AcidLime,
                        fontFamily = WorkSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = news.title,
                    color = IceWhite,
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                if (!news.date.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = news.date, color = MutedGray, fontFamily = WorkSansFamily, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = news.summary ?: "",
                    color = IceWhite.copy(alpha = 0.85f),
                    fontFamily = WorkSansFamily,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (!news.url.isNullOrBlank()) {
                    Ocupa2Button(
                        text = "Leer artículo completo",
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.url)))
                        }
                    )
                }
            }
        }
    }
}