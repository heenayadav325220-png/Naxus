package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlassmorphicTextField
import com.example.ui.theme.*
import com.example.ui.viewmodel.SpaceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val chatMessages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.chatInputText.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val selectedImage by viewModel.chatSelectedImage.collectAsState()

    var showImageDrawer by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }

    val suggestedQuestions = remember {
        listOf(
            "Explain Black Holes",
            "How large is Jupiter?",
            "What is a Supernova?",
            "Tell me about Orion"
        )
    }

    val listState = rememberLazyListState()

    // Scroll to bottom when a new message arrives
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header / Console Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STARNEXUS COGNITIVE NODE",
                        fontSize = 11.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Cosmic AI Chat",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarlightWhite
                    )
                }

                // Clear history button
                IconButton(
                    onClick = {
                        viewModel.clearChatHistory()
                        Toast.makeText(context, "Telemetry memory cleared", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = "Clear Session",
                        tint = StarlightWhite.copy(alpha = 0.6f)
                    )
                }
            }

            // Message Stream Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.02f), shape = RoundedCornerShape(12.dp))
                    .border(1.dp, SpaceCardStroke.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (chatMessages.isEmpty()) {
                    // Empty Chat State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(CelestialPurple.copy(alpha = 0.15f), shape = CircleShape)
                                .border(1.dp, CelestialPurple.copy(alpha = 0.4f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = "AI",
                                tint = CelestialPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "StarNexus AI Online",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarlightWhite
                        )
                        Text(
                            text = "Inquire about black holes, gravitational anomalies, constellations, or planetary physics. You can also upload a stellar capture for analysis!",
                            fontSize = 12.sp,
                            color = StarlightWhite.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMessages) { message ->
                            ChatBubble(message = message)
                        }

                        if (isThinking) {
                            item {
                                ThinkingBubble()
                            }
                        }
                    }
                }
            }

            // Suggested Queries Carousel
            if (chatMessages.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestedQuestions) { question ->
                        Box(
                            modifier = Modifier
                                .background(SpaceCardBg.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
                                .border(1.dp, SpaceCardStroke.copy(alpha = 0.25f), shape = RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.setChatInputText(question)
                                    viewModel.sendChatMessage()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = question,
                                color = NebulaBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Entry and Actions panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image attach button
                IconButton(
                    onClick = { showImageDrawer = !showImageDrawer },
                    modifier = Modifier
                        .size(44.dp)
                        .background(SpaceCardBg.copy(alpha = 0.2f), shape = CircleShape)
                        .border(1.dp, SpaceCardStroke.copy(alpha = 0.3f), shape = CircleShape)
                        .testTag("attach_image_btn")
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "Attach telemetry photo",
                        tint = if (selectedImage != null) NebulaBlue else StarlightWhite
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text entry bar
                GlassmorphicTextField(
                    value = inputText,
                    onValueChange = { viewModel.setChatInputText(it) },
                    placeholderText = if (selectedImage != null) "Describe attached telemetry..." else "Inquire about stars, planets...",
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    testTag = "ai_chat_textfield"
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Voice / Send contextual button
                if (inputText.isEmpty() && selectedImage == null) {
                    // Microphone button (Voice search/input)
                    IconButton(
                        onClick = {
                            isRecordingVoice = true
                            scope.launch {
                                delay(2000) // Simulating listening
                                isRecordingVoice = false
                                viewModel.setChatInputText("What is a supermassive black hole?")
                                Toast.makeText(context, "Voice telemetry encoded!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(CelestialPurple.copy(alpha = 0.15f), shape = CircleShape)
                            .border(1.dp, CelestialPurple.copy(alpha = 0.4f), shape = CircleShape)
                            .testTag("voice_input_btn")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = CelestialPurple
                        )
                    }
                } else {
                    // Send message button
                    IconButton(
                        onClick = { viewModel.sendChatMessage() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(listOf(PrimarySpace, SecondarySpace)),
                                shape = CircleShape
                            )
                            .testTag("send_message_btn")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Transmit",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Voice Recording Ripple Overlay
        if (isRecordingVoice) {
            VoiceRippleOverlay()
        }

        // Image attach bottom sheet simulation
        if (showImageDrawer) {
            ImageAttachmentDrawer(
                onImageSelected = { bitmap ->
                    viewModel.setChatSelectedImage(bitmap)
                    showImageDrawer = false
                    Toast.makeText(context, "Space telemetry linked!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showImageDrawer = false }
            )
        }
    }
}

@Composable
fun ChatBubble(message: com.example.data.database.ChatMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(CelestialPurple.copy(alpha = 0.2f), shape = CircleShape)
                    .border(1.dp, CelestialPurple.copy(alpha = 0.4f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = CelestialPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .background(
                    if (isUser) PrimarySpace.copy(alpha = 0.35f) else SpaceCardBg.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .border(
                    1.dp,
                    if (isUser) PrimarySpace.copy(alpha = 0.5f) else SpaceCardStroke.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                if (message.imageUriString != null) {
                    Text(
                        text = "[Linked Telemetry Image]",
                        fontSize = 11.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = StarlightWhite,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimarySpace.copy(alpha = 0.2f), shape = CircleShape)
                    .border(1.dp, PrimarySpace.copy(alpha = 0.4f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "User",
                    tint = PrimarySpace,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(CelestialPurple.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = "Thinking", tint = CelestialPurple, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(SpaceCardBg.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                .border(1.dp, SpaceCardStroke.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Analyzing stellar vectors...",
                fontSize = 12.sp,
                color = StarlightWhite.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun VoiceRippleOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Ripple pulsing effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(CelestialPurple.copy(alpha = 0.2f), shape = CircleShape)
                    .border(2.dp, CelestialPurple, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Listening",
                    tint = CelestialPurple,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "LISTENING TELEMETRY...",
                fontSize = 12.sp,
                color = CelestialPurple,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Speak your celestial query now",
                fontSize = 14.sp,
                color = StarlightWhite.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ImageAttachmentDrawer(
    onImageSelected: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}, // Prevent clicks through card
            testTag = "image_attach_drawer"
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LINK STELLAR TELEMETRY",
                        fontSize = 12.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StarlightWhite)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a local space scan file to transmit for AI description:",
                    fontSize = 13.sp,
                    color = StarlightWhite.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Render options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val options = listOf(
                        Pair("Black Hole", R.drawable.img_hero_banner),
                        Pair("Nebula Space", R.drawable.img_hero_banner),
                        Pair("Galaxy", R.drawable.img_hero_banner)
                    )

                    for ((label, resId) in options) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                                onImageSelected(bitmap)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = label,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = StarlightWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
