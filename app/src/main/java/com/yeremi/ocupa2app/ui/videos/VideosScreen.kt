package com.yeremi.ocupa2app.ui.videos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.yeremi.ocupa2app.network.models.VideoItem
import com.yeremi.ocupa2app.ui.theme.AcidLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosScreen(
    viewModel: VideosViewModel,
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Videos")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onNavigateBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = viewModel::loadVideos
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        when {

            state.isLoading -> {

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

            state.error != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = state.error!!
                        )

                        Button(
                            onClick = viewModel::loadVideos
                        ) {

                            Text("Reintentar")
                        }
                    }
                }
            }

            state.videos.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No hay videos disponibles.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {

                    item {

                        Text(
                            text = "Capacitación y tutoriales",
                            style =
                                MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Videos de YouTube sobre empleo, oficios y capacitación.",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(
                        items = state.videos,
                        key = { video ->
                            video.youtubeId
                        }
                    ) { video ->

                        VideoCard(
                            video = video,
                            onClick = {

                                try {

                                    val intent =
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(video.url)
                                        )

                                    context.startActivity(intent)

                                } catch (_: Exception) {

                                    // Evita que la app se cierre
                                    // si el dispositivo no puede
                                    // abrir el enlace.
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
private fun VideoCard(
    video: VideoItem,
    onClick: () -> Unit
) {

    // YouTube proporciona automáticamente
    // esta miniatura usando el youtubeId.
    val thumbnailUrl =
        "https://img.youtube.com/vi/${video.youtubeId}/hqdefault.jpg"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {

                SubcomposeAsyncImage(
                    model = thumbnailUrl,
                    contentDescription =
                        "Miniatura de ${video.title}",
                    modifier =
                        Modifier.fillMaxSize(),
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
                                    Modifier.size(28.dp),
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
                                    Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = AcidLime,
                                modifier =
                                    Modifier.size(55.dp)
                            )
                        }
                    }
                )

                Surface(
                    modifier =
                        Modifier.align(
                            Alignment.Center
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .surface
                            .copy(alpha = 0.85f),
                    shape =
                        RoundedCornerShape(100.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.PlayArrow,
                        contentDescription =
                            "Reproducir",
                        tint = AcidLime,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(34.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {

                Text(
                    text = video.title,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (
                    !video.description
                        .isNullOrBlank()
                ) {

                    Text(
                        text = video.description,
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AcidLime,
                        modifier =
                            Modifier.size(18.dp)
                    )

                    Text(
                        text =
                            "Ver video en YouTube",
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        color = AcidLime,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}