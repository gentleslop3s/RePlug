package com.cyril.replug.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.ROUTE_REGISTER

private val SurfaceDark        = Color(0xFF0F1923)
private val PageBg             = Color(0xFFF0F2F5)
private val BorderLight        = Color(0xFFE2E6EC)
private val TextPrimary        = Color(0xFF111827)
private val TextSecondary      = Color(0xFF6B7280)
private val AccentGreen        = Color(0xFF16A34A)
private val AccentGreenSurface = Color(0xFFDCFCE7)

@Composable
fun Onboarding3Screen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Illustration badge ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(AccentGreenSurface)
                    .border(1.dp, BorderLight, RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(R.drawable.recycle),
                    contentDescription = "Recycle",
                    modifier           = Modifier.size(300.dp)
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Dot indicators (page 3 of 3 active) ──────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Inactive dots
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BorderLight)
                    )
                }
                // Active dot — green on last page
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(28.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Heading ───────────────────────────────────────────────────────
            Text(
                text          = "Recycle Old Devices",
                fontSize      = 26.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextPrimary,
                textAlign     = TextAlign.Center,
                letterSpacing = (-0.5).sp,
                lineHeight    = 32.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text       = "Give unused tech a second life.",
                fontSize   = 15.sp,
                color      = TextSecondary,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(48.dp))

            // ── Primary button ────────────────────────────────────────────────
            Button(
                onClick  = { navController.navigate(ROUTE_REGISTER) },
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark,
                    contentColor   = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    "Start Now",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreen3Preview() = Onboarding3Screen(rememberNavController())