package com.cyril.replug.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark        = Color(0xFF0F1923)
private val PageBg             = Color(0xFFF0F2F5)
private val BorderLight        = Color(0xFFE2E6EC)
private val TextPrimary        = Color(0xFF111827)
private val TextSecondary      = Color(0xFF6B7280)
private val AccentBlue         = Color(0xFF1A6BF5)
private val AccentBlueSurface  = Color(0xFFEFF4FF)
private val AccentGreen        = Color(0xFF16A34A)
private val AccentGreenSurface = Color(0xFFDCFCE7)
private val AccentAmber        = Color(0xFFD97706)
private val AccentAmberSurface = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ──────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint     = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Hero ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(vertical = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Logo mark
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(AccentBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♻️", fontSize = 36.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "RePlug",
                        fontSize      = 28.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.6).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Version 1.0.0",
                        fontSize = 13.sp,
                        color    = Color.White.copy(alpha = 0.45f)
                    )
                    Spacer(Modifier.height(12.dp))

                    // Tag line pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            "Buy · Sell · Recycle Electronics",
                            fontSize   = 13.sp,
                            color      = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Mission card ─────────────────────────────────────────────────
            AboutCard {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentBlueSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 20.sp)
                    }
                    Text(
                        "Our Mission",
                        fontSize      = 15.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = TextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "RePlug is on a mission to reduce e-waste by creating a trusted marketplace where people can buy, sell, and responsibly recycle electronics.\n\nWe believe pre-owned devices deserve a second life — and that sustainable choices should be easy and rewarding.",
                    fontSize   = 14.sp,
                    color      = TextSecondary,
                    lineHeight = 22.sp
                )
            }

            // ── What we offer ────────────────────────────────────────────────
            AboutCard {
                AboutSectionLabel("What we offer")
                Spacer(Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureRow(
                        emoji    = "🏷️",
                        title    = "Buy & Sell",
                        subtitle = "List your devices or find great deals from verified sellers.",
                        color    = AccentBlue,
                        surface  = AccentBlueSurface
                    )
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    FeatureRow(
                        emoji    = "♻️",
                        title    = "Recycle responsibly",
                        subtitle = "Donate, recycle, or sell for parts — we handle the rest.",
                        color    = AccentGreen,
                        surface  = AccentGreenSurface
                    )
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    FeatureRow(
                        emoji    = "🔒",
                        title    = "Safe & secure",
                        subtitle = "Verified listings and secure payments built in.",
                        color    = AccentAmber,
                        surface  = AccentAmberSurface
                    )
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    FeatureRow(
                        emoji    = "🇰🇪",
                        title    = "Built for Kenya",
                        subtitle = "M-Pesa payments, local pickup & drop-off support.",
                        color    = AccentGreen,
                        surface  = AccentGreenSurface
                    )
                }
            }

            // ── Stats strip ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "10K+",  label = "Users",    emoji = "👥")
                StatDivider()
                StatItem(value = "5K+",   label = "Listings", emoji = "📦")
                StatDivider()
                StatItem(value = "1K+",   label = "Recycled", emoji = "♻️")
            }

            Spacer(Modifier.height(8.dp))

            // ── App info ─────────────────────────────────────────────────────
            AboutCard {
                AboutSectionLabel("App info")
                Spacer(Modifier.height(4.dp))

                InfoRow(label = "Version",     value = "1.0.0")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                InfoRow(label = "Platform",    value = "Android")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                InfoRow(label = "Developer",   value = "Cyril Odhiambo")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                InfoRow(label = "Country",     value = "🇰🇪  Kenya")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                InfoRow(label = "Contact",     value = "hello@replug.co.ke")
            }

            // ── Legal links ──────────────────────────────────────────────────
            AboutCard {
                AboutSectionLabel("Legal")
                Spacer(Modifier.height(4.dp))

                LegalRow(icon = Icons.Rounded.Gavel,          label = "Terms of Service")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                LegalRow(icon = Icons.Rounded.PrivacyTip,     label = "Privacy Policy")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                LegalRow(icon = Icons.Rounded.Cookie,         label = "Cookie Policy")
            }

            // ── Footer ───────────────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Made with ♥ by Cyril", fontSize = 13.sp, color = TextSecondary)
                Text(
                    "© 2025 RePlug. All rights reserved.",
                    fontSize = 12.sp,
                    color    = TextSecondary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun AboutCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun AboutSectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color         = TextSecondary
    )
}

@Composable
private fun FeatureRow(
    emoji   : String,
    title   : String,
    subtitle: String,
    color   : Color,
    surface : Color
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surface),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold,   color = Color.White, letterSpacing = (-0.3).sp)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun LegalRow(icon: ImageVector, label: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun AboutScreenPreview() {
    AboutScreen(rememberNavController())
}