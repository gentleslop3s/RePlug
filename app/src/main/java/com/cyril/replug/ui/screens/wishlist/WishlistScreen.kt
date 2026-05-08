package com.cyril.replug.ui.screens.wishlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.navigation.*

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val AccentBlue    = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)

// ─── Wishlist item data class ─────────────────────────────────────────────────
data class WishlistItem(
    val id         : String = "",
    val name       : String = "",
    val price      : String = "",
    val category   : String = "",
    val imageUrl   : String = "",
    val productId  : String = "",
    val addedAt    : Long   = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(navController: NavController) {

    // TODO: replace with real Firebase fetch for current user's wishlist
    val wishlistItems by remember { mutableStateOf<List<WishlistItem>>(emptyList()) }
    val isLoading     by remember { mutableStateOf(false) }
    val itemCount      = wishlistItems.size

    Scaffold(
        containerColor = PageBg,

        // ── Top bar (mirrors NotificationScreen) ──────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Wishlist",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        // Item count badge
                        AnimatedVisibility(
                            visible = itemCount > 0,
                            enter   = fadeIn(),
                            exit    = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF1F2))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "$itemCount",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color(0xFFE11D48)
                                )
                            }
                        }
                    }
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
                actions = {
                    AnimatedVisibility(
                        visible = itemCount > 0,
                        enter   = fadeIn(),
                        exit    = fadeOut()
                    ) {
                        TextButton(onClick = { /* TODO: clear wishlist */ }) {
                            Text(
                                "Clear all",
                                fontSize   = 13.sp,
                                color      = Color(0xFFE11D48),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg)
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ── Loading ───────────────────────────────────────────────────────
            if (isLoading) {
                CircularProgressIndicator(
                    color    = AccentBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Empty state ───────────────────────────────────────────────────
            else if (wishlistItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF1F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter            = painterResource(com.cyril.replug.R.drawable.wishlist),
                            contentDescription = "Empty wishlist",
                            modifier           = Modifier.size(56.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Your wishlist is empty",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Save items you love and find them here.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick  = { navController.navigate(ROUTE_HOME) },
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor   = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            "Browse items",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Wishlist items list ───────────────────────────────────────────
            else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(wishlistItems, key = { it.id }) { item ->
                        WishlistItemCard(
                            item      = item,
                            onRemove  = { /* TODO: remove from Firebase wishlist */ },
                            onClick   = { navController.navigate("productDetail/${item.productId}") }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Wishlist item card ───────────────────────────────────────────────────────
@Composable
private fun WishlistItemCard(
    item    : WishlistItem,
    onRemove: () -> Unit,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Product image placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccentBlueSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.PhoneAndroid,
                contentDescription = null,
                tint     = AccentBlue,
                modifier = Modifier.size(28.dp)
            )
        }

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                item.name,
                fontSize      = 14.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = TextPrimary,
                letterSpacing = (-0.2).sp
            )
            Text(
                item.category,
                fontSize = 12.sp,
                color    = TextSecondary
            )
            Text(
                "Ksh ${item.price}",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A6BF5)
            )
        }

        // Remove button
        IconButton(
            onClick  = onRemove,
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Remove from wishlist",
                    tint     = Color(0xFFE11D48),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun WishlistScreenPreview() {
    WishlistScreen(rememberNavController())
}