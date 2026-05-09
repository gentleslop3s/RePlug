package com.cyril.replug.ui.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.database.*

// ─── Colour tokens (matches MyListingsScreen exactly) ─────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)
private val AccentGreen       = Color(0xFF16A34A)
private val AccentGreenSurface= Color(0xFFDCFCE7)
private val AccentAmber       = Color(0xFFD97706)
private val AccentAmberSurface= Color(0xFFFEF3C7)
private val AccentRed         = Color(0xFFDC2626)
private val AccentRedSurface  = Color(0xFFFEF2F2)

// ─── Order data class ─────────────────────────────────────────────────────────
data class Order(
    val orderId       : String = "",
    val productId     : String = "",
    val productName   : String = "",
    val price         : String = "",
    val paymentMethod : String = "",
    val status        : String = "Pending",
    val imageUrl      : String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {

    var orders    by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val ref      = FirebaseDatabase.getInstance().getReference("Orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Order>()
                snapshot.children.forEach { child ->
                    list.add(
                        Order(
                            orderId       = child.child("orderId").getValue(String::class.java) ?: "",
                            productId     = child.child("productId").getValue(String::class.java) ?: "",
                            productName   = child.child("productName").getValue(String::class.java) ?: "Unknown item",
                            price         = child.child("price").getValue(String::class.java) ?: "—",
                            paymentMethod = child.child("paymentMethod").getValue(String::class.java) ?: "—",
                            status        = child.child("status").getValue(String::class.java) ?: "Pending",
                            imageUrl      = child.child("imageUrl").getValue(String::class.java) ?: ""
                        )
                    )
                }
                orders    = list.reversed()
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar — identical to MyListingsScreen ───────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Orders",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        AnimatedVisibility(
                            visible = !isLoading && orders.isNotEmpty(),
                            enter   = fadeIn(),
                            exit    = fadeOut()
                        ) {
                            Text(
                                "${orders.size} order${if (orders.size != 1) "s" else ""}",
                                fontSize = 12.sp,
                                color    = TextSecondary
                            )
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

            // ── Empty state — mirrors MyListingsScreen empty state exactly ────
            else if (orders.isEmpty()) {
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
                            .background(AccentBlueSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.ReceiptLong,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "No orders yet",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "When you buy something, your\norders will show up here.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick   = { navController.popBackStack() },
                        modifier  = Modifier.fillMaxWidth().height(52.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor   = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Browse listings", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── 2-column grid — same as MyListingsScreen ──────────────────────
            else {
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(2),
                    modifier              = Modifier.fillMaxSize(),
                    contentPadding        = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.orderId }) { order ->
                        OrderCard(order = order)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Order card — same shape/elevation/border as MyListingCard ───────────────
@Composable
private fun OrderCard(order: Order) {

    val (statusColor, statusSurface) = when (order.status) {
        "Completed" -> AccentGreen to AccentGreenSurface
        "Cancelled" -> AccentRed   to AccentRedSurface
        else        -> AccentAmber to AccentAmberSurface
    }

    val methodEmoji = when (order.paymentMethod) {
        "M-Pesa"      -> "📱"
        "Visa / Card" -> "💳"
        else          -> "💰"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column {

            // ── Image box — same 140dp height as MyListingCard ────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (order.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model              = order.imageUrl,
                        contentDescription = "Product image",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    // Gradient placeholder — same as MyListingCard
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(Color(0xFFE8ECF4), Color(0xFFCDD5E8))),
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.ImageNotSupported,
                            contentDescription = null,
                            tint     = Color(0xFFB0BAD0),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Status chip — top-end overlay, same position as action buttons in MyListingCard
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusSurface.copy(alpha = 0.95f))
                        .border(0.5.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (order.status) {
                            "Completed" -> "✅  ${order.status}"
                            "Cancelled" -> "❌  ${order.status}"
                            else        -> "⏳  ${order.status}"
                        },
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = statusColor
                    )
                }
            }

            // ── Info section — same padding/spacing as MyListingCard ──────────
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Product name — same as product.name line
                Text(
                    text          = order.productName,
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextPrimary,
                    maxLines      = 1,
                    letterSpacing = (-0.2).sp
                )
                // Price — same style as "Ksh ${product.price}"
                Text(
                    text       = "Ksh ${order.price}",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = AccentBlue
                )
                // Payment + order ID — same role as category line
                Text(
                    text     = "$methodEmoji ${order.paymentMethod}  ·  #${order.orderId.takeLast(5).uppercase()}",
                    fontSize = 11.sp,
                    color    = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen(rememberNavController())
}