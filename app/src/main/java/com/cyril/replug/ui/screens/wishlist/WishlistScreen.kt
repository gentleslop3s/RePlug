package com.cyril.replug.ui.screens.wishlist

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// ─── Colour tokens (shared) ───────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(navController: NavController) {

    var selectedIndex by remember { mutableIntStateOf(3) } // Wishlist is active

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ───────────────────────────────────────────────────────────
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

        // ── Bottom nav ────────────────────────────────────────────────────────
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
                data class NavItem(
                    val label: String,
                    val icon: ImageVector,
                    val route: String,
                    val index: Int
                )

                val items = listOf(
                    NavItem("Home",     Icons.Rounded.Home,           ROUTE_HOME,     0),
                    NavItem("Search",   Icons.Rounded.Search,         ROUTE_SEARCH,   1),
                    NavItem("Add",      Icons.Rounded.Add,            ROUTE_ADD,      2),
                    NavItem("Wishlist", Icons.Rounded.FavoriteBorder, ROUTE_WISHLIST, 3),
                    NavItem("Profile",  Icons.Rounded.Person,         ROUTE_PROFILE,  4),
                )

                items.forEach { item ->
                    val isSelected = selectedIndex == item.index
                    NavigationBarItem(
                        icon = {
                            if (item.index == 2) {
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

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Wishlist illustration ─────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF1F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(com.cyril.replug.R.drawable.wishlist),
                        contentDescription = "Empty wishlist",
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Heading ───────────────────────────────────────────────────
                Text(
                    text = "Your wishlist is empty",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Save items you love and find them here",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // ── CTA button ────────────────────────────────────────────────
                Button(
                    onClick = { navController.navigate(ROUTE_HOME) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Browse items",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(rememberNavController())
}