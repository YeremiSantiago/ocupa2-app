package com.yeremi.ocupa2app.ui.offers

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.yeremi.ocupa2app.R
import com.yeremi.ocupa2app.network.models.Offer
import com.yeremi.ocupa2app.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapOffersScreen(
    viewModel: MapOffersViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val offers by viewModel.offers.collectAsState()
    val selectedOffer by viewModel.selectedOffer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val showPermissionOverlay by viewModel.showPermissionOverlay.collectAsState()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(18.4861, -69.9312), 12f)
    }

    // Estilo del mapa con fallback seguro si el JSON falla
    val mapStyleOptions = remember {
        runCatching {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        }.getOrNull()
    }

    // Sincronizar estado del permiso con el ViewModel
    LaunchedEffect(locationPermissionState.status.isGranted) {
        viewModel.updateLocationPermission(locationPermissionState.status.isGranted)
    }

    // Animar cámara al marcador seleccionado
    LaunchedEffect(selectedOffer) {
        selectedOffer?.let {
            val lat = it.location?.lat ?: return@let
            val lng = it.location?.lng ?: return@let
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, lng), 14f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Google Map ─────────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapStyleOptions = mapStyleOptions,
                isMyLocationEnabled = locationPermissionState.status.isGranted
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
        ) {
            offers.forEach { offer ->
                val lat = offer.location?.lat ?: return@forEach
                val lng = offer.location?.lng ?: return@forEach
                val isSelected = offer.id == selectedOffer?.id

                // AcidLime (verde) = normal, ElectricTeal (cyan) = tiene foto
                val markerHue = if (!offer.photo.isNullOrEmpty())
                    BitmapDescriptorFactory.HUE_CYAN
                else
                    BitmapDescriptorFactory.HUE_GREEN

                Marker(
                    state = MarkerState(position = LatLng(lat, lng)),
                    title = offer.jobTypeName ?: "Oferta",
                    icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                    alpha = if (isSelected) 1.0f else 0.6f,
                    zIndex = if (isSelected) 1f else 0f,
                    onClick = {
                        viewModel.selectOffer(offer)
                        false
                    }
                )
            }
        }

        // ── 2. TopBar Superpuesto ─────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Mapa de Empleos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                // Ícono de lista: navega a ExploreOffersScreen (no popBackStack)
                IconButton(onClick = onNavigateToList) {
                    Icon(
                        Icons.Default.FormatListBulleted,
                        contentDescription = "Ver lista",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ── 3. Bottom Sheet ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            RoundedCornerShape(2.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Empleos cercanos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${offers.size} disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Error message ─────────────────────────────────────────
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                // ── Empty state ───────────────────────────────────────────
                if (offers.isEmpty() && !isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.WorkOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay empleos disponibles en el mapa",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ── Selected Offer Card ───────────────────────────────────
                selectedOffer?.let { offer ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = offer.jobTypeName ?: "Oferta",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${translateContractType(offer.contractType ?: "")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = offer.payment?.let { "RD$ ${it.amount?.toInt() ?: 0}/${it.period ?: ""}" } ?: "–",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = AcidLime
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (hasLocationPermission) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = offer.address ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 120.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onNavigateToDetail(offer.id) },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    "VER DETALLE",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Mini cards LazyRow ────────────────────────────────────
                if (offers.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Filtrar selectedOffer para no mostrarla duplicada
                        items(offers.filter { it.id != selectedOffer?.id }) { offer ->
                            Surface(
                                onClick = { viewModel.selectOffer(offer) },
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.width(160.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = offer.jobTypeName ?: "Oferta",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = translateContractType(offer.contractType ?: ""),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "RD$ ${offer.payment?.amount?.toInt() ?: 0}",
                                        color = AcidLime,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── 4. Overlay de Permiso ─────────────────────────────────────────
        // Se muestra si el permiso no está concedido Y el usuario no eligió "ver sin ubicación"
        if (!locationPermissionState.status.isGranted && showPermissionOverlay) {
            val isPermanentlyDenied = !locationPermissionState.status.isGranted &&
                    !locationPermissionState.status.shouldShowRationale

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ubicación no disponible",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPermanentlyDenied)
                                "Has denegado el permiso permanentemente. Ve a Configuración para activarlo."
                            else
                                "Activa los permisos de ubicación para ver los empleos cercanos a ti.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón principal: solicitar permiso o abrir Configuración del sistema
                        Button(
                            onClick = {
                                if (isPermanentlyDenied) {
                                    // Permiso denegado permanentemente → abrir Configuración del sistema
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                    context.startActivity(intent)
                                } else {
                                    locationPermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isPermanentlyDenied) "ABRIR CONFIGURACIÓN" else "ACTIVAR UBICACIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón secundario: cerrar overlay y ver el mapa sin ubicación
                        TextButton(onClick = { viewModel.dismissPermissionOverlay() }) {
                            Text(
                                "Ver sin ubicación",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // ── 5. Loading Overlay ────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun translateContractType(type: String): String = when (type) {
    "TEMPORARY" -> "Temporal"
    "FIXED" -> "Fijo"
    "HOURLY" -> "Por horas"
    else -> type
}
