package com.cyril.replug.ui.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.*

// ─── Colour tokens (shared) ───────────────────────────────────────────────────
private val SurfaceDark    = Color(0xFF0F1923)
private val PageBg         = Color(0xFFF0F2F5)
private val BorderLight    = Color(0xFFE2E6EC)
private val TextPrimary    = Color(0xFF111827)
private val TextSecondary  = Color(0xFF6B7280)
private val AccentBlue     = Color(0xFF1A6BF5)

// ─── Each product now carries its own imageRes ────────────────────────────────
data class Product(
    val name: String,
    val price: String,
    val tag: String,
    val imageRes: Int          // ← set a different drawable per card
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {

    var selectedIndex    by remember { mutableIntStateOf(1) }
    var searchQuery      by remember { mutableStateOf("") }
    val categories       = listOf("All", "Phones", "Laptops", "Audio", "Cameras", "Wearables")
    var selectedCategory by remember { mutableStateOf("All") }

    // ── Product list — swap imageRes per entry ────────────────────────────────
    val products = listOf(
        Product("Phones",      "", "",  R.drawable.phones),
        Product("Laptops",     "", "",  R.drawable.laptops),  // ← change drawable
        Product("Tablets",     "", "", R.drawable.tablet),  // ← change drawable
        Product("Headphones",  "", "",  R.drawable.headphones),  // ← change drawable
        Product("Storage",     "", "",  R.drawable.storage),  // ← change drawable
        Product("Audio",       "", "", R.drawable.jbl_speaker),  // ← change drawable
    )

    val tagColors = mapOf(
        "Hot"  to Color(0xFFEF4444),
        "New"  to Color(0xFF16A34A),
        "Sale" to AccentBlue
    )
    val tagSurfaces = mapOf(
        "Hot"  to Color(0xFFFEF2F2),
        "New"  to Color(0xFFDCFCE7),
        "Sale" to Color(0xFFEFF4FF)
    )

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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            // ── Search bar ────────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search products...", color = TextSecondary, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentBlue,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(14.dp))

            // ── Category chips ────────────────────────────────────────────────
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.size) { i ->
                    val cat        = categories[i]
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick  = { selectedCategory = cat },
                        label    = {
                            Text(
                                cat,
                                fontSize   = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SurfaceDark,
                            selectedLabelColor     = Color.White,
                            containerColor         = Color.White,
                            labelColor             = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = isSelected,
                            borderColor         = BorderLight,
                            selectedBorderColor = SurfaceDark
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Section header ────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Featured Listings",
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "See all",
                    fontSize   = 13.sp,
                    color      = AccentBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Product grid ──────────────────────────────────────────────────
            LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    SearchProductCard(
                        product    = product,
                        tagColor   = tagColors[product.tag]   ?: AccentBlue,
                        tagSurface = tagSurfaces[product.tag] ?: Color(0xFFEFF4FF),
                        onClick    = { navController.navigate(ROUTE_CONVERSATION) }
                    )
                }
            }
        }
    }
}

// ─── Product card ─────────────────────────────────────────────────────────────
@Composable
fun SearchProductCard(
    product: Product,
    tagColor: Color,
    tagSurface: Color,
    onClick: () -> Unit
) {
    var wishlisted by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            Box {
                // ── Product image (individual per card via product.imageRes) ──
                Image(
                    painter            = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // ── Tag badge ─────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .background(tagSurface, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text       = product.tag,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = tagColor
                    )
                }

                // ── Wishlist toggle ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { wishlisted = !wishlisted },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = if (wishlisted) Icons.Rounded.Favorite
                        else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint               = if (wishlisted) Color(0xFFEF4444)
                        else TextSecondary,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text          = product.name,
                fontSize      = 14.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = TextPrimary,
                maxLines      = 1,
                letterSpacing = (-0.2).sp
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text       = product.price,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = AccentBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(rememberNavController())
}