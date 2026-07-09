package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.model.NewsArticle
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SpaceBackground
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NebulaBlue
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.SpaceCardStroke
import com.example.ui.theme.StarlightWhite
import com.example.ui.viewmodel.SpaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Resolve ViewModel safely
        val viewModel = ViewModelProvider(this)[SpaceViewModel::class.java]

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            MyApplicationTheme(darkTheme = isDarkMode) {
                val currentScreen by viewModel.currentScreen.collectAsState()
                
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "splash" -> {
                            SplashScreen(
                                onTimeout = { viewModel.setScreen("login") },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        "login" -> {
                            LoginScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        "main" -> {
                            MainScaffold(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScaffold(viewModel: SpaceViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedArticle by viewModel.selectedArticle.collectAsState()

    // SpaceBackground envelops the entire main scaffold
    SpaceBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent, // Let SpaceBackground show through
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                GlassmorphicBottomNavigation(
                    activeTab = activeTab,
                    onTabSelected = { viewModel.setActiveTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Cross-fade screen switching animation
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "screen_switch"
                ) { tab ->
                    when (tab) {
                        "home" -> HomeScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        "sky" -> SkyScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        "discover" -> DiscoverScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        "ai" -> AiScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        "profile" -> ProfileScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        // Expanded News Article Overlay (Modal drawer/sheet)
        selectedArticle?.let { article ->
            ArticleInspectorOverlay(
                article = article,
                onDismiss = { viewModel.inspectArticle(null) }
            )
        }
    }
}

@Composable
fun GlassmorphicBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = remember {
        listOf(
            TabItem("home", "Home", Icons.Default.Home),
            TabItem("sky", "Sky", Icons.Default.Language),
            TabItem("discover", "Discover", Icons.Default.ManageSearch),
            TabItem("ai", "AI", Icons.Default.SmartToy),
            TabItem("profile", "Profile", Icons.Default.AccountCircle)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpaceCardBg.copy(alpha = 0.28f),
                        SpaceCardBg.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SpaceCardStroke.copy(alpha = 0.45f),
                        SpaceCardStroke.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .height(68.dp)
            .clip(RoundedCornerShape(24.dp))
            .testTag("glass_bottom_navigation")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (item in items) {
                val isSelected = activeTab == item.id
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(item.id) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_tab_${item.id}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isSelected) NebulaBlue.copy(alpha = 0.12f) else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) NebulaBlue else StarlightWhite.copy(alpha = 0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    // Selected Dot indicator
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(NebulaBlue, shape = CircleShape)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

data class TabItem(val id: String, val label: String, val icon: ImageVector)

@Composable
fun ArticleInspectorOverlay(
    article: NewsArticle,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() }
            .padding(16.dp)
            .testTag("article_dialog"),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false) {},
            testTag = "article_card"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(NebulaBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = article.source.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NebulaBlue
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StarlightWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = article.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarlightWhite
                )

                Text(
                    text = "Published ${article.publishedDate}",
                    fontSize = 11.sp,
                    color = StarlightWhite.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Divider(color = SpaceCardStroke.copy(alpha = 0.15f), modifier = Modifier.padding(bottom = 12.dp))

                Text(
                    text = article.content,
                    fontSize = 14.sp,
                    color = StarlightWhite.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "DISPATCH FOOTNOTE",
                    fontSize = 11.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "This dispatch has been encrypted and distributed via StarNexus secure telemetry node. Archive reference ID: #ST-${article.id}.",
                    fontSize = 11.sp,
                    color = StarlightWhite.copy(alpha = 0.4f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
