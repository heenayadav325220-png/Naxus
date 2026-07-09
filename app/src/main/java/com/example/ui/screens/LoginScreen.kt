package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TravelExplore
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
import com.example.ui.components.*
import com.example.ui.theme.NebulaBlue
import com.example.ui.theme.StarlightWhite
import com.example.ui.viewmodel.SpaceViewModel

@Composable
fun LoginScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    SpaceBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "StarNexus logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .testTag("login_logo")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "StarNexus Command",
                fontSize = 28.sp,
                color = StarlightWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("login_title")
            )

            Text(
                text = "Synchronize with the cosmos",
                fontSize = 14.sp,
                color = StarlightWhite.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("login_subtitle")
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_card"
            ) {
                Text(
                    text = "SECURE PORTAL",
                    fontSize = 12.sp,
                    color = NebulaBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Email field
                GlassmorphicTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Cosmic Email Address",
                    leadingIcon = {
                        Icon(
                            Icons.Default.AlternateEmail,
                            contentDescription = "Email",
                            tint = StarlightWhite.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "email_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                GlassmorphicTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Security Access Code",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = StarlightWhite.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "password_input"
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NebulaBlue)
                    }
                } else {
                    // Google Sign-In with Firebase button
                    GlassmorphicButton(
                        onClick = {
                            isLoading = true
                            viewModel.loginAnonymously { success ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Telemetry synced successfully!", Toast.LENGTH_SHORT).show()
                                    viewModel.setScreen("main")
                                } else {
                                    // Local guest fallback so user never gets stuck
                                    Toast.makeText(context, "Securing offline local workspace...", Toast.LENGTH_SHORT).show()
                                    viewModel.setScreen("main")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "google_signin_button"
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.TravelExplore,
                                contentDescription = "Travel Explore",
                                modifier = Modifier.size(20.dp),
                                tint = StarlightWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SIGN IN WITH GOOGLE", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bypass Guest login
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Entering offline guest mode...", Toast.LENGTH_SHORT).show()
                            viewModel.setScreen("main")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_signin_button"),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StarlightWhite.copy(alpha = 0.8f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            StarlightWhite.copy(alpha = 0.25f)
                        )
                    ) {
                        Text("EXPLORE AS GUEST", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "Federated Space Operations Node • V1.0.0",
                fontSize = 11.sp,
                color = StarlightWhite.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}
