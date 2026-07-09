package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NewsArticle
import com.example.data.model.SpaceData
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SpaceBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val firebaseUser by viewModel.firebaseUser.collectAsState()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()
    val savedArticles by viewModel.savedArticles.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val facts = remember {
        listOf(
            "One day on Venus is longer than an entire Martian or Earth year! Venus rotates backward on its axis.",
            "Neutron stars are so incredibly dense that a single teaspoon of their material would weigh about 6 billion tons on Earth.",
            "Light takes roughly 8 minutes and 20 seconds to travel from the Sun's core surface to Earth.",
            "The footprints left by Apollo astronauts on the Moon will remain there for at least 100 million years due to the lack of atmosphere.",
            "Space is completely silent. There is no atmosphere or medium for sound waves to vibrate and travel through.",
            "We can only observe 5% of the universe. The rest (95%) is composed of mysterious dark matter and dark energy."
        )
    }
    var currentFactIndex by remember { mutableStateOf(0) }

    // Coroutine to simulate pull-to-refresh
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header / Welcome Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome to StarNexus,",
                            fontSize = 14.sp,
                            color = StarlightWhite.copy(alpha = 0.6f)
                        )
                        Text(
                            text = firebaseUser?.displayName ?: "Commander Guest",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarlightWhite,
                            modifier = Modifier.testTag("home_user_greeting")
                        )
                    }

                    // Notification bell icon with unread count badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(SpaceCardBg.copy(alpha = 0.2f), shape = CircleShape)
                            .border(1.dp, SpaceCardStroke.copy(alpha = 0.3f), shape = CircleShape)
                            .clickable {
                                viewModel.setActiveTab("profile") // Navigate to notifications on profile
                                Toast.makeText(context, "System Alerts Console loaded", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("notification_shortcut"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Alerts Hub",
                            tint = StarlightWhite
                        )
                        if (unreadNotificationsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(CometFuchsia, shape = CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadNotificationsCount.toString(),
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Featured Space Image Card (Nebula nursery banner)
            item {
                Text(
                    text = "FEATURED TELEMETRY",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    testTag = "featured_image_card"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = "Cosmic nebula nursery",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Dark gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                        )

                        // Caption
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "The Orion Nebula Nursery (M42)",
                                color = StarlightWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A vast interstellar nursery spawning thousands of newborn stars",
                                color = StarlightWhite.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Quick Action Buttons
            item {
                Text(
                    text = "QUICK STEERING CODES",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val actions = listOf(
                        Triple(Icons.Default.Language, "Sky Map", "sky"),
                        Triple(Icons.Default.SmartToy, "AI Assistant", "ai"),
                        Triple(Icons.Default.ManageSearch, "Discover", "discover"),
                        Triple(Icons.Default.Bookmarks, "Bookmarks", "bookmarks")
                    )

                    for ((icon, label, tab) in actions) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (tab == "bookmarks") {
                                        viewModel.setActiveTab("discover")
                                        viewModel.setDiscoverCategory("all")
                                        Toast.makeText(context, "Favorites Shelf Filtered", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.setActiveTab(tab)
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                PrimarySpace.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.dp, SpaceCardStroke.copy(alpha = 0.35f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                    tint = StarlightWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = StarlightWhite.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Daily Astronomy Fact Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY COSMIC INTEL",
                        fontSize = 12.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        modifier = Modifier.clickable {
                            currentFactIndex = (currentFactIndex + 1) % facts.size
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Next Fact",
                            tint = NebulaBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Next",
                            fontSize = 12.sp,
                            color = NebulaBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    testTag = "daily_fact_card"
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CelestialPurple.copy(alpha = 0.25f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Fact",
                                tint = CelestialPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Did you know?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = facts[currentFactIndex],
                                fontSize = 13.sp,
                                color = StarlightWhite.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Trending Space News
            item {
                Text(
                    text = "TRENDING CELESTIAL DISPATCH",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(SpaceData.articles) { article ->
                val isSaved = savedArticles.any { it.id == article.id }
                
                NewsCard(
                    article = article,
                    isSaved = isSaved,
                    onToggleSave = { viewModel.toggleSaveArticle(article) },
                    onReadMore = { viewModel.inspectArticle(article) }
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    article: NewsArticle,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onReadMore: () -> Unit
) {
    val context = LocalContext.current
    
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        testTag = "news_card_${article.id}"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // News Image Small
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Simple text if network is offline or Coil loading, but Unsplash links are direct
                    androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray)
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner), // Fallback
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarlightWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${article.publishedDate} • ${article.source}",
                        fontSize = 11.sp,
                        color = StarlightWhite.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.summary,
                fontSize = 12.sp,
                color = StarlightWhite.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onReadMore,
                    modifier = Modifier.testTag("read_news_btn_${article.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("READ DISPATCH", color = NebulaBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Read More",
                            tint = NebulaBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row {
                    // Bookmark toggle
                    IconButton(
                        onClick = {
                            onToggleSave()
                            val msg = if (isSaved) "Article removed from archives" else "Article archived offline!"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("news_bookmark_btn_${article.id}")
                    ) {
                        Icon(
                            if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Archive Article",
                            tint = if (isSaved) CelestialPurple else StarlightWhite.copy(alpha = 0.7f)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Read this awesome space dispatch on StarNexus:\n${article.title}\n\n${article.summary}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = StarlightWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
