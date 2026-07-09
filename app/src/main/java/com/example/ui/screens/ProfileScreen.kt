package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassmorphicButton
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GlassmorphicTextField
import com.example.ui.theme.*
import com.example.ui.viewmodel.SpaceViewModel
import com.example.data.database.NotificationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val firebaseUser by viewModel.firebaseUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val currentLanguage by viewModel.languageSetting.collectAsState()
    val notificationsList by viewModel.notifications.collectAsState()

    var feedbackText by remember { mutableStateOf("") }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Section: Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(CelestialPurple.copy(alpha = 0.15f), shape = CircleShape)
                        .border(1.5.dp, CelestialPurple, shape = CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon), // Space icon as placeholder
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = firebaseUser?.displayName ?: "Astronaut Commander",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarlightWhite,
                    modifier = Modifier.testTag("profile_username")
                )

                Text(
                    text = firebaseUser?.email ?: "sector_guest_902@starnexus.local",
                    fontSize = 12.sp,
                    color = StarlightWhite.copy(alpha = 0.5f),
                    modifier = Modifier.testTag("profile_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Sign out button
                    OutlinedButton(
                        onClick = {
                            viewModel.logoutUser()
                            Toast.makeText(context, "Logged out of command node.", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CometFuchsia.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CometFuchsia)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Exit", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SIGN OUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Backup manual sync button
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Transmitting archives to cloud database...", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, NebulaBlue.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NebulaBlue)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYNC CLOUD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: System Preferences & Toggles
        item {
            Text(
                text = "STEERING PREFERENCES",
                fontSize = 11.sp,
                color = NebulaBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                testTag = "preferences_card"
            ) {
                // Dark/Light Mode toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleDarkMode() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme",
                            tint = StarlightWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Immersive Theme", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                            Text("High contrast space-optimized display", fontSize = 11.sp, color = StarlightWhite.copy(alpha = 0.5f))
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NebulaBlue, checkedTrackColor = NebulaBlue.copy(alpha = 0.4f))
                    )
                }

                Divider(color = SpaceCardStroke.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                // Language settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = StarlightWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Language Decoder", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                            Text("Active: $currentLanguage", fontSize = 11.sp, color = StarlightWhite.copy(alpha = 0.5f))
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Select", tint = StarlightWhite.copy(alpha = 0.5f))
                }

                Divider(color = SpaceCardStroke.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                // Notification settings toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setNotificationsEnabled(!notificationsEnabled) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "Alerts",
                            tint = StarlightWhite.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cosmic Event Alerts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                            Text("Meteor, ISS pass, & eclipses alerts", fontSize = 11.sp, color = StarlightWhite.copy(alpha = 0.5f))
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = NebulaBlue, checkedTrackColor = NebulaBlue.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // Section: System Notifications (Alerts Center)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYSTEM ALERTS LOG (${notificationsList.size})",
                    fontSize = 11.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                if (notificationsList.isNotEmpty()) {
                    Text(
                        text = "Mark all read",
                        fontSize = 11.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.markAllNotificationsAsRead() }
                    )
                }
            }
        }

        if (notificationsList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SpaceCardBg.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, SpaceCardStroke.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All systems stable. No cosmic alerts logged.", fontSize = 12.sp, color = StarlightWhite.copy(alpha = 0.5f))
                }
            }
        } else {
            items(notificationsList) { alert ->
                AlertItemCard(
                    alert = alert,
                    onMarkRead = { viewModel.markNotificationAsRead(alert.id) },
                    onDelete = { viewModel.deleteNotification(alert.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Section: Feedback / Mission Reports
        item {
            Text(
                text = "TRANSMIT MISSION REPORT (FEEDBACK)",
                fontSize = 11.sp,
                color = NebulaBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                testTag = "feedback_card"
            ) {
                Text(
                    text = "Reporting any telemetry errors, sensor glitches, or UI feedback straight to star command.",
                    fontSize = 11.sp,
                    color = StarlightWhite.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GlassmorphicTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholderText = "Enter report description...",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    testTag = "feedback_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlassmorphicButton(
                    onClick = {
                        if (feedbackText.trim().isNotEmpty()) {
                            feedbackText = ""
                            Toast.makeText(context, "Feedback report transmitted to Command!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please write a brief feedback report.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    testTag = "submit_feedback_btn"
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Upload, contentDescription = "Transmit", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TRANSMIT REPORT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: About & Privacy
        item {
            Text(
                text = "REGISTRY telemetry",
                fontSize = 11.sp,
                color = NebulaBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "about_card"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Privacy & Protocols Registry", fontSize = 13.sp, color = StarlightWhite)
                    Icon(Icons.Default.HelpOutline, contentDescription = "Protocol Details", tint = StarlightWhite.copy(alpha = 0.5f))
                }

                Divider(color = SpaceCardStroke.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Build Signature", fontSize = 13.sp, color = StarlightWhite.copy(alpha = 0.6f))
                    Text("v1.4.2-Nexus", fontSize = 13.sp, color = StarlightWhite, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Developer Node", fontSize = 13.sp, color = StarlightWhite.copy(alpha = 0.6f))
                    Text("AI Command Center", fontSize = 13.sp, color = NebulaBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = DarkSurface,
            title = { Text("Select Cosmic Decoder Language", color = StarlightWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val languages = listOf("English", "Español", "Français", "हिन्दी")
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguageSetting(lang)
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Decoders calibrated to $lang", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = {
                                    viewModel.setLanguageSetting(lang)
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Decoders calibrated to $lang", Toast.LENGTH_SHORT).show()
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = StarlightWhite, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = DarkSurface,
            title = { Text("Privacy & Protocols Registry", color = StarlightWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Protocols v4.0 • Secure Node Operations",
                        fontSize = 12.sp,
                        color = NebulaBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "StarNexus operates as a fully offline-first, client-synchronized federation. All astronomical inquiries, chat logs, catalog search queries, and bookmarked telemetry details are kept safely within local sandboxed secure storage.\n\nSync operations are safely authenticated with Google Identity via Firebase Auth. Your backups are kept securely on Google Cloud Firestore, protected by industry-standard Firebase Security rules. No metadata or telemetry details are ever leaked or shared with external operations nodes.",
                        fontSize = 12.sp,
                        color = StarlightWhite.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("DISMISS PROTOCOLS", color = NebulaBlue)
                }
            }
        )
    }
}

@Composable
fun AlertItemCard(
    alert: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {
    val isRead = alert.isRead
    
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { if (!isRead) onMarkRead() },
        backgroundColor = if (isRead) SpaceCardBg.copy(alpha = 0.05f) else SpaceCardBg.copy(alpha = 0.18f),
        borderColor = if (isRead) SpaceCardStroke.copy(alpha = 0.12f) else SpaceCardStroke,
        testTag = "alert_item_${alert.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unread indicators
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (isRead) Color.Transparent else CometFuchsia,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRead) StarlightWhite.copy(alpha = 0.6f) else StarlightWhite
                )
                Text(
                    text = alert.body,
                    fontSize = 11.sp,
                    color = if (isRead) StarlightWhite.copy(alpha = 0.4f) else StarlightWhite.copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete notification
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss alert",
                    tint = StarlightWhite.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
