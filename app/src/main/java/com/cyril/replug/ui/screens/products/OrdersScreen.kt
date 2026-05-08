package com.cyril.replug.ui.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ─── Colour tokens ────────────────────────────────────────────────────────────
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
    val status        : String = "Pending"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {

    var orders    by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ── Real-time listener on /Orders ────────────────────────────────────────
    DisposableEffect(Unit) {
        val ref      = FirebaseDatabase.getInstance().getReference("Orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Order>()
                snapshot.children.forEach { child ->
                    val order = Order(
                        orderId       = child.child("orderId").getValue(String::class.java) ?: "",
                        productId     = child.child("productId").getValue(String::class.java) ?: "",
                        productName   = child.child("productName").getValue(String::class.java) ?: "Unknown item",
                        price         = child.child("price").getValue(String::class.java) ?: "—",
                        paymentMethod = child.child("paymentMethod").getValue(String::class.java) ?: "—",
                        status        = child.child("status").getValue(String::class.java) ?: "Pending"
                    )
                    list.add(order)
                }
                // Most recent first
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

        // ── Top bar ──────────────────────────────────────────────────────────
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

            // ── Empty state ───────────────────────────────────────────────────
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
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor   = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Browse listings",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Orders list ───────────────────────────────────────────────────
            else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(orders, key = { it.orderId }) { order ->
                        OrderCard(order = order)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Order card ───────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(order: Order) {

    val (statusColor, statusSurface, statusIcon) = when (order.status) {
        "Completed" -> Triple(AccentGreen, AccentGreenSurface, Icons.Rounded.CheckCircle)
        "Cancelled" -> Triple(AccentRed,   AccentRedSurface,   Icons.Rounded.Cancel)
        else        -> Triple(AccentAmber, AccentAmberSurface, Icons.Rounded.HourglassTop)
    }

    val methodEmoji = when (order.paymentMethod) {
        "M-Pesa"     -> "📱"
        "Visa / Card"-> "💳"
        else         -> "💰"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── Header row ────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Product name + ID
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text          = order.productName,
                    fontSize      = 15.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextPrimary,
                    letterSpacing = (-0.2).sp,
                    maxLines      = 1
                )
                Text(
                    text     = "Order #${order.orderId.takeLast(6).uppercase()}",
                    fontSize = 11.sp,
                    color    = TextSecondary
                )
            }

            Spacer(Modifier.width(12.dp))

            // Status chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusSurface)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    statusIcon,
                    contentDescription = null,
                    tint     = statusColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    order.status,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = statusColor
                )
            }
        }

        HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

        // ── Detail row ────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Payment method badge
            Column(
                modifier            = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PageBg)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(methodEmoji, fontSize = 22.sp)
                Text(
                    order.paymentMethod,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextSecondary,
                    textAlign  = TextAlign.Center
                )
            }

            // Price badge
            Column(
                modifier            = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlueSurface)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Ksh ${order.price}",
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = AccentBlue,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Total paid",
                    fontSize = 11.sp,
                    color    = AccentBlue.copy(alpha = 0.65f)
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