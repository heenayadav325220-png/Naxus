package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.DiscoveryItem
import com.example.data.model.SpaceData
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlassmorphicTextField
import com.example.ui.theme.*
import com.example.ui.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val searchQuery by viewModel.globalSearchQuery.collectAsState()
    val categoryFilter by viewModel.discoverCategoryFilter.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val selectedItem by viewModel.selectedDiscoveryItem.collectAsState()

    // Filter items based on Category selection & Search Query
    val itemsToDisplay = remember(searchQuery, categoryFilter, savedItems) {
        val baseList = if (categoryFilter == "saved") {
            // Map SavedItems in Room back to DiscoveryItems for uniform display
            savedItems.map { saved ->
                DiscoveryItem(
                    id = saved.id,
                    category = saved.category,
                    name = saved.name,
                    shortDescription = saved.description,
                    detailedDescription = saved.detailText,
                    imageUrl = saved.imageResName
                )
            }
        } else {
            SpaceData.discoveries
        }

        val lowerQuery = searchQuery.lowercase().trim()
        baseList.filter { item ->
            val matchesQuery = lowerQuery.isEmpty() || 
                    item.name.lowercase().contains(lowerQuery) || 
                    item.shortDescription.lowercase().contains(lowerQuery) ||
                    item.detailedDescription.lowercase().contains(lowerQuery)
            
            val matchesCategory = categoryFilter == "all" || categoryFilter == "saved" || item.category == categoryFilter
            
            matchesQuery && matchesCategory
        }
    }

    val categoriesList = remember {
        listOf(
            "all" to "All Cosmos",
            "planet" to "Planets",
            "star" to "Stars",
            "galaxy" to "Galaxies",
            "blackhole" to "Black Holes",
            "nebula" to "Nebulas",
            "mission" to "Missions",
            "satellite" to "Satellites",
            "astronaut" to "Astronauts",
            "saved" to "Saved Archives"
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Screen Header
            Text(
                text = "CELESTIAL ARCHIVE DATABASE",
                fontSize = 12.sp,
                color = NebulaBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "Discover the Cosmos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = StarlightWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search input field
            GlassmorphicTextField(
                value = searchQuery,
                onValueChange = { viewModel.setGlobalSearchQuery(it) },
                placeholderText = "Search planets, stars, galaxies...",
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = StarlightWhite.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setGlobalSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = StarlightWhite)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                testTag = "discover_search_bar"
            )

            // Category scrolling chips row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoriesList) { (key, label) ->
                    val isSelected = categoryFilter == key
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) CelestialPurple.copy(alpha = 0.35f) else SpaceCardBg.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) CelestialPurple else SpaceCardStroke.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setDiscoverCategory(key) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("category_chip_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) StarlightWhite else StarlightWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Results List
            if (itemsToDisplay.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = "No Results",
                        modifier = Modifier.size(64.dp),
                        tint = StarlightWhite.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cosmic Signal Lost",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarlightWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "No entities match your search query or criteria.",
                        fontSize = 12.sp,
                        color = StarlightWhite.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("discoveries_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(itemsToDisplay) { item ->
                        val isSaved = savedItems.any { it.id == item.id }
                        DiscoverCard(
                            item = item,
                            isSaved = isSaved,
                            onToggleSave = { viewModel.toggleSaveItem(item) },
                            onInspect = { viewModel.inspectDiscoveryItem(item) }
                        )
                    }
                }
            }
        }

        // Expanded Inspector sheet / detailed modal
        selectedItem?.let { inspected ->
            val isInspectedSaved = savedItems.any { it.id == inspected.id }
            ItemInspectorDialog(
                item = inspected,
                isSaved = isInspectedSaved,
                onDismiss = { viewModel.inspectDiscoveryItem(null) },
                onToggleSave = { viewModel.toggleSaveItem(inspected) }
            )
        }
    }
}

@Composable
fun DiscoverCard(
    item: DiscoveryItem,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onInspect: () -> Unit
) {
    val context = LocalContext.current
    
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onInspect,
        testTag = "discover_card_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner), // Fallback image asset
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(NebulaBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NebulaBlue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarlightWhite
                )
                Text(
                    text = item.shortDescription,
                    fontSize = 12.sp,
                    color = StarlightWhite.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bookmark icon
            IconButton(
                onClick = {
                    onToggleSave()
                    val msg = if (isSaved) "Item unsaved from archives" else "Entity bookmarked offline!"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("bookmark_btn_${item.id}")
            ) {
                Icon(
                    if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save Item",
                    tint = if (isSaved) CelestialPurple else StarlightWhite.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ItemInspectorDialog(
    item: DiscoveryItem,
    isSaved: Boolean,
    onDismiss: () -> Unit,
    onToggleSave: () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() } // Tap outside to dismiss
            .padding(16.dp)
            .testTag("inspector_dialog"),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false) {}, // Prevent clicks through card
            testTag = "inspector_card"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(NebulaBlue.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NebulaBlue
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onToggleSave) {
                            Icon(
                                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isSaved) CelestialPurple else StarlightWhite
                            )
                        }
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Explore ${item.name} on StarNexus!\n\n${item.shortDescription}\n\n${item.detailedDescription}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = StarlightWhite)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = StarlightWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.DarkGray, shape = RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner), // Fallback image asset
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name & Text description
                Text(
                    text = item.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarlightWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.detailedDescription,
                    fontSize = 13.sp,
                    color = StarlightWhite.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Properties table if available
                val properties = SpaceData.discoveries.find { it.id == item.id }?.properties ?: emptyMap()
                if (properties.isNotEmpty()) {
                    Text(
                        text = "TELESCOPIC TELEMETRY",
                        fontSize = 12.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, SpaceCardStroke.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        properties.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 12.sp,
                                    color = StarlightWhite.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = value,
                                    fontSize = 12.sp,
                                    color = StarlightWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (key != properties.keys.last()) {
                                Divider(color = SpaceCardStroke.copy(alpha = 0.15f), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
