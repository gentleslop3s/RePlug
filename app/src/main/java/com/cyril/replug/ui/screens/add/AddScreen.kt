package com.cyril.replug.ui.screens.add

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.navigation.*
import com.cyril.replug.ui.theme.mainBlue

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark      = Color(0xFF0F1923)
private val PageBg           = Color(0xFFF0F2F5)
private val BorderLight      = Color(0xFFE2E6EC)
private val TextPrimary      = Color(0xFF111827)
private val TextSecondary    = Color(0xFF6B7280)
private val AccentBlue       = Color(0xFF1A6BF5)
private val AccentBlueSurface= Color(0xFFEFF4FF)
private val AccentGreen      = Color(0xFF16A34A)
private val AccentGreenSurface = Color(0xFFDCFCE7)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController) {

    var selectedIndex by remember { mutableIntStateOf(2) } // "Add" is active

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ──────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = com.cyril.replug.R.drawable.repluglogo),
                        contentDescription = "RePlug",
                        modifier = Modifier.size(100.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Rounded.ShoppingCart,
                            contentDescription = "Cart",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextSecondary
                )
            )
        },

        // ── Bottom nav ───────────────────────────────────────────────────────
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = BorderLight,
                    shape = RoundedCornerShape(0.dp)
                )
            ) {
                data class NavItem(val label: String, val icon: ImageVector, val route: String, val index: Int)

                val items = listOf(
                    NavItem("Home",     Icons.Rounded.Home,        ROUTE_HOME,     0),
                    NavItem("Search",   Icons.Rounded.Search,      ROUTE_SEARCH,   1),
                    NavItem("Add",      Icons.Rounded.Add,         ROUTE_ADD,      2),
                    NavItem("Wishlist", Icons.Rounded.FavoriteBorder, ROUTE_WISHLIST, 3),
                    NavItem("Profile",  Icons.Rounded.Person,      ROUTE_PROFILE,  4),
                )

                items.forEach { item ->
                    val isSelected = selectedIndex == item.index
                    NavigationBarItem(
                        icon = {
                            if (item.index == 2) {
                                // FAB-style Add button
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            selectedIndex = item.index
                            navController.navigate(item.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = SurfaceDark,
                            selectedTextColor   = SurfaceDark,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = Color.Transparent
                        )
                    )
                }
            }
        },

        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ── Heading ──────────────────────────────────────────────────
                Text(
                    text = "What would you\nlike to do?",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Choose an option to get started",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))

                // ── Sell card ────────────────────────────────────────────────
                ActionCard(
                    emoji = "🏷️",
                    title = "Sell an item",
                    subtitle = "List your device for someone to buy",
                    accentColor = AccentBlue,
                    surfaceColor = AccentBlueSurface,
                    onClick = { navController.navigate(ROUTE_SELL) }
                )

                Spacer(Modifier.height(14.dp))

                // ── Divider with "or" ────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BorderLight,
                        thickness = 1.dp
                    )
                    Text(
                        "or",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BorderLight,
                        thickness = 1.dp
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── Recycle card ─────────────────────────────────────────────
                ActionCard(
                    emoji = "♻️",
                    title = "Recycle a device",
                    subtitle = "Donate, recycle, or sell for parts responsibly",
                    accentColor = AccentGreen,
                    surfaceColor = AccentGreenSurface,
                    onClick = { navController.navigate(ROUTE_RECYCLE) }
                )
            }
        }
    )
}

// ─── Action card ──────────────────────────────────────────────────────────────
@Composable
private fun ActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji badge
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 28.sp)
        }

        // Text block
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )
            Text(
                subtitle,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }

        // Arrow chevron
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddScreenPreview() {
    AddScreen(rememberNavController())
}