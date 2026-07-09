package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SpaceBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.SpaceViewModel
import kotlin.math.cos
import kotlin.math.sin

// Representation of stars on our interactive sky chart
private data class SkyStar(
    val id: String,
    val name: String,
    val xAngle: Float, // polar coordinate radius
    val yAngle: Float, // polar coordinate degree
    val magnitude: Float, // size and brightness
    val info: String,
    val color: Color
)

@Composable
fun SkyScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Interactive Map state
    var mapOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedStarName by remember { mutableStateOf("Sirius") }
    var selectedStarInfo by remember { mutableStateOf("Brightest star in Canis Major. Known as the Dog Star, Sirius is a nearby binary system.") }
    
    var showConstellations by remember { mutableStateOf(true) }
    var showGridlines by remember { mutableStateOf(true) }
    var showPlanetPaths by remember { mutableStateOf(true) }

    // Hardcoded sky chart stars (Relative positions)
    val skyStars = remember {
        listOf(
            SkyStar("sirius", "Sirius", 120f, 45f, 5f, "Brightest star in Canis Major. Magnitude: -1.46. Distance: 8.6 light-years.", NebulaBlue),
            SkyStar("betelgeuse", "Betelgeuse", 180f, 120f, 6f, "Red supergiant star in Orion's shoulder. Exploding soon. Distance: 640 light-years.", CometFuchsia),
            SkyStar("rigel", "Rigel", 220f, 110f, 5.5f, "Blue supergiant star in Orion's foot. Distance: 860 light-years.", NebulaBlue),
            SkyStar("polaris", "Polaris", 30f, 0f, 4.5f, "The North Star, situated near the Earth's celestial pole in Ursa Minor.", StarlightWhite),
            SkyStar("vega", "Vega", 140f, 310f, 4.8f, "Brilliant blue star in Lyra. Baseline for stellar magnitudes. Distance: 25 light-years.", NebulaBlue),
            SkyStar("antares", "Antares", 260f, 210f, 5.2f, "Red supergiant star in Scorpius. The 'Heart of Scorpio'.", CometFuchsia),
            SkyStar("aldebaran", "Aldeberan", 190f, 75f, 4.9f, "Red giant star forming the eye of Taurus. Distance: 65 light-years.", CelestialPurple),
            SkyStar("capella", "Capella", 110f, 150f, 4.7f, "Brightest star in Auriga. Actually a quadruple star system.", StarlightWhite)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Screen Title
            item {
                Text(
                    text = "STEREOGRAPHIC SKY CONSOLE",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
                Text(
                    text = "Night Sky Map",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarlightWhite,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Interactive Map Canvas Card
            item {
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    testTag = "sky_map_card"
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "INTERACTIVE STAR CHART (DRAG TO PAN)",
                            fontSize = 11.sp,
                            color = StarlightWhite.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Draggable Celestial Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, SpaceCardStroke.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        mapOffset = Offset(
                                            x = (mapOffset.x + dragAmount.x).coerceIn(-150f, 150f),
                                            y = (mapOffset.y + dragAmount.y).coerceIn(-150f, 150f)
                                        )
                                    }
                                }
                                .testTag("sky_canvas_box")
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2 + mapOffset.x, size.height / 2 + mapOffset.y)

                                // Draw radial celestial coordinate grids
                                if (showGridlines) {
                                    drawCircle(
                                        color = CelestialPurple.copy(alpha = 0.12f),
                                        radius = 60f,
                                        center = center,
                                        style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f)))
                                    )
                                    drawCircle(
                                        color = CelestialPurple.copy(alpha = 0.12f),
                                        radius = 130f,
                                        center = center,
                                        style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f)))
                                    )
                                    drawCircle(
                                        color = CelestialPurple.copy(alpha = 0.12f),
                                        radius = 200f,
                                        center = center,
                                        style = Stroke(width = 1.5f)
                                    )

                                    // Crosshair lines
                                    drawLine(
                                        color = CelestialPurple.copy(alpha = 0.1f),
                                        start = Offset(center.x - 220f, center.y),
                                        end = Offset(center.x + 220f, center.y),
                                        strokeWidth = 1f
                                    )
                                    drawLine(
                                        color = CelestialPurple.copy(alpha = 0.1f),
                                        start = Offset(center.x, center.y - 220f),
                                        end = Offset(center.x, center.y + 220f),
                                        strokeWidth = 1f
                                    )
                                }

                                // Draw Constellation boundary lines
                                if (showConstellations) {
                                    // Orion lines: Betelgeuse (180f, 120f) to Rigel (220f, 110f), Sirius (120f, 45f) to Aldebaran (190f, 75f)
                                    val bRad = Math.toRadians(120.0)
                                    val rRad = Math.toRadians(110.0)
                                    val bPos = Offset(center.x + (180f * cos(bRad)).toFloat(), center.y + (180f * sin(bRad)).toFloat())
                                    val rPos = Offset(center.x + (220f * cos(rRad)).toFloat(), center.y + (220f * sin(rRad)).toFloat())

                                    val sRad = Math.toRadians(45.0)
                                    val aRad = Math.toRadians(75.0)
                                    val sPos = Offset(center.x + (120f * cos(sRad)).toFloat(), center.y + (120f * sin(sRad)).toFloat())
                                    val aPos = Offset(center.x + (190f * cos(aRad)).toFloat(), center.y + (190f * sin(aRad)).toFloat())

                                    drawLine(
                                        color = NebulaBlue.copy(alpha = 0.35f),
                                        start = bPos,
                                        end = rPos,
                                        strokeWidth = 1.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                    )
                                    drawLine(
                                        color = NebulaBlue.copy(alpha = 0.35f),
                                        start = sPos,
                                        end = aPos,
                                        strokeWidth = 1.5f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                    )
                                    drawLine(
                                        color = NebulaBlue.copy(alpha = 0.25f),
                                        start = bPos,
                                        end = aPos,
                                        strokeWidth = 1f
                                    )
                                }

                                // Draw Planet orbits
                                if (showPlanetPaths) {
                                    drawCircle(
                                        color = NebulaBlue.copy(alpha = 0.08f),
                                        radius = 160f,
                                        center = center,
                                        style = Stroke(width = 1.2f)
                                    )
                                }

                                // Draw stars on canvas
                                for (star in skyStars) {
                                    val rad = Math.toRadians(star.yAngle.toDouble())
                                    val posX = center.x + (star.xAngle * cos(rad)).toFloat()
                                    val posY = center.y + (star.xAngle * sin(rad)).toFloat()

                                    // Draw glowing shadow
                                    drawCircle(
                                        color = star.color.copy(alpha = 0.25f),
                                        radius = star.magnitude * 2f,
                                        center = Offset(posX, posY)
                                    )
                                    // Core star
                                    drawCircle(
                                        color = star.color,
                                        radius = star.magnitude * 0.7f,
                                        center = Offset(posX, posY)
                                    )
                                }
                            }

                            // Coordinate info overlay
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "RA: 06h 45m / DEC: -16°42'",
                                    fontSize = 10.sp,
                                    color = StarlightWhite.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Toggle settings row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Constellations",
                                fontSize = 11.sp,
                                color = if (showConstellations) NebulaBlue else StarlightWhite.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .clickable { showConstellations = !showConstellations }
                                    .testTag("toggle_constellations")
                            )
                            Text(
                                text = "Gridlines",
                                fontSize = 11.sp,
                                color = if (showGridlines) NebulaBlue else StarlightWhite.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .clickable { showGridlines = !showGridlines }
                                    .testTag("toggle_gridlines")
                            )
                            Text(
                                text = "Planet Orbits",
                                fontSize = 11.sp,
                                color = if (showPlanetPaths) NebulaBlue else StarlightWhite.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .clickable { showPlanetPaths = !showPlanetPaths }
                                    .testTag("toggle_orbits")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Star selector Quick-Menu
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(skyStars) { star ->
                                val isSelected = selectedStarName == star.name
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) CelestialPurple.copy(alpha = 0.3f) else SpaceCardBg.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) CelestialPurple else SpaceCardStroke.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            selectedStarName = star.name
                                            selectedStarInfo = star.info
                                        }
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = star.name,
                                        color = if (isSelected) StarlightWhite else StarlightWhite.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected star inspection readout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SpaceCardBg.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, SpaceCardStroke.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(NebulaBlue, shape = CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Telemetry: $selectedStarName",
                                        fontWeight = FontWeight.Bold,
                                        color = StarlightWhite,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedStarInfo,
                                    fontSize = 12.sp,
                                    color = StarlightWhite.copy(alpha = 0.8f),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Moon Phase Widget
            item {
                Text(
                    text = "CELESTIAL EPHEMERIS",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Moon Card
                    GlassmorphicCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        testTag = "moon_phase_card"
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "MOON PHASE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarlightWhite.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            // Simple vector moon visualization
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.Transparent, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Draw dark shadow moon
                                    drawCircle(color = Color(0xFF1E1E2E))
                                    // Draw lit sector representing Waxing Crescent
                                    drawArc(
                                        color = Color(0xFFFFF2A3),
                                        startAngle = -90f,
                                        sweepAngle = 180f,
                                        useCenter = false
                                    )
                                    // Glow
                                    drawCircle(
                                        color = Color(0x30FFF2A3),
                                        radius = 26.dp.toPx(),
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Waxing Crescent",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarlightWhite
                            )
                            Text(
                                text = "Illumination: 28%",
                                fontSize = 11.sp,
                                color = StarlightWhite.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Solar times card
                    GlassmorphicCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        testTag = "sunrise_sunset_card"
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "SOLAR EPHEMERIS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarlightWhite.copy(alpha = 0.5f),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.WbSunny,
                                    contentDescription = "Sunrise",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Sunrise", fontSize = 10.sp, color = StarlightWhite.copy(alpha = 0.5f))
                                    Text("05:41 AM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.NightsStay,
                                    contentDescription = "Sunset",
                                    tint = CelestialPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Sunset", fontSize = 10.sp, color = StarlightWhite.copy(alpha = 0.5f))
                                    Text("08:34 PM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Meteor showers & Alerts
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "meteor_alert_card"
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CometFuchsia.copy(alpha = 0.2f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NewReleases,
                                contentDescription = "Active Alert",
                                tint = CometFuchsia,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Meteor Shower Alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CometFuchsia
                            )
                            Text(
                                text = "Perseids Shower active. Peak in 3 days. Peak density: 110/hr.",
                                fontSize = 12.sp,
                                color = StarlightWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
