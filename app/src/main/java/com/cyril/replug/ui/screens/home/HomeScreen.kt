package com.cyril.replug.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.cyril.replug.data.ProductViewModel
import com.cyril.replug.navigation.*

// ─── Colour tokens (same as AddScreen) ───────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val AccentBlue    = Color(0xFF1A6BF5)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }

    val viewModel: ProductViewModel = viewModel(
        viewModelStoreOwner = context as androidx.activity.ComponentActivity
    )

    LaunchedEffect(Unit) {
        viewModel.fetchProducts(context)
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ───────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.repluglogo),
                        contentDescription = "RePlug",
                        modifier = Modifier.size(100.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(ROUTE_WISHLIST) }) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = "Wishlist",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = { navController.navigate(ROUTE_ORDERS) }) {
                        Icon(
                            Icons.Rounded.ShoppingCart,
                            contentDescription = "Cart",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = { navController.navigate(ROUTE_NOTIFICATION)}) {
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
                    NavItem("Inbox",    Icons.Rounded.Inbox,          ROUTE_CHAT_INBOX, 3),
                    NavItem("Profile",  Icons.Rounded.Person,         ROUTE_PROFILE,  4),
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

        content = { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.products) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                            .clickable {
                                navController.navigate("productDetail/${product.id}")
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            // ── Product image ──────────────────────────────
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                AsyncImage(
                                    model = product.imageUrl,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp
                                            )
                                        )
                                )
                            }

                            // ── Product info ───────────────────────────────
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = product.name ?: "No name",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    letterSpacing = (-0.2).sp
                                )
                                Text(
                                    text = "Ksh ${product.price}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                                Text(
                                    text = product.category ?: "",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}