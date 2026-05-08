package com.cyril.replug.ui.screens.payment

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyril.replug.models.Product
import com.google.firebase.database.FirebaseDatabase

// ─── Colour tokens (shared) ───────────────────────────────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)
private val AccentGreen       = Color(0xFF16A34A)
private val AccentGreenSurface= Color(0xFFDCFCE7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    productId: String,
    navController: NavController,
    onPaymentDone: () -> Unit = {}
) {
    val context         = LocalContext.current
    var product         by remember { mutableStateOf<Product?>(null) }
    var selectedMethod  by remember { mutableStateOf("") }
    var loading         by remember { mutableStateOf(true) }
    var isProcessing    by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .child(productId)
            .get()
            .addOnSuccessListener {
                product = it.getValue(Product::class.java)
                loading = false
            }
            .addOnFailureListener {
                loading = false
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_LONG).show()
            }
    }

    // ── Loading ───────────────────────────────────────────────────────────────
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
        return
    }

    // ── Not found ─────────────────────────────────────────────────────────────
    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found", color = TextSecondary)
        }
        return
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ───────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.Bold,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PageBg
                )
            )
        },

        // ── Bottom bar ────────────────────────────────────────────────────────
        bottomBar = {
            Surface(
                color          = Color.White,
                tonalElevation = 0.dp,
                modifier       = Modifier.border(1.dp, BorderLight, RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            "Ksh ${product!!.price}",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedMethod.isEmpty()) {
                                Toast.makeText(context, "Select a payment method", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isProcessing = true
                            val orderRef = FirebaseDatabase.getInstance()
                                .getReference("Orders").push()
                            val orderData = mapOf(
                                "orderId"       to orderRef.key,
                                "productId"     to productId,
                                "productName"   to product!!.name,
                                "price"         to product!!.price,
                                "paymentMethod" to selectedMethod,
                                "status"        to "Pending"
                            )
                            orderRef.setValue(orderData)
                                .addOnSuccessListener {
                                    isProcessing = false
                                    Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                                    onPaymentDone()
                                }
                                .addOnFailureListener {
                                    isProcessing = false
                                    Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show()
                                }
                        },
                        enabled  = !isProcessing,
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor   = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color    = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Pay Now",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            // ── Order summary card ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Order Summary",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                SummaryRow(label = "Product",  value = product!!.name  ?: "—")
                SummaryRow(label = "Category", value = product!!.category ?: "—")
                SummaryRow(label = "Brand",    value = product!!.brand ?: "—")

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Total",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary
                    )
                    Text(
                        "Ksh ${product!!.price}",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AccentBlue
                    )
                }
            }

            // ── Payment method heading ────────────────────────────────────────
            Text(
                "Payment Method",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary
            )

            // ── M-Pesa card ───────────────────────────────────────────────────
            PaymentMethodCard(
                emoji      = "📱",
                title      = "M-Pesa",
                subtitle   = "Pay via Safaricom M-Pesa",
                isSelected = selectedMethod == "M-Pesa",
                accentColor   = AccentGreen,
                surfaceColor  = AccentGreenSurface,
                onClick    = { selectedMethod = "M-Pesa" }
            )

            // ── Visa / Card ───────────────────────────────────────────────────
            PaymentMethodCard(
                emoji      = "💳",
                title      = "Visa / Card",
                subtitle   = "Pay with debit or credit card",
                isSelected = selectedMethod == "Visa / Card",
                accentColor   = AccentBlue,
                surfaceColor  = AccentBlueSurface,
                onClick    = { selectedMethod = "Visa / Card" }
            )
        }
    }
}

// ─── Summary row ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

// ─── Payment method card (mirrors ActionCard from AddScreen) ──────────────────
@Composable
private fun PaymentMethodCard(
    emoji: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) accentColor else BorderLight
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji badge
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }

        // Text
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                title,
                fontSize      = 15.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = TextPrimary,
                letterSpacing = (-0.2).sp
            )
            Text(
                subtitle,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 18.sp
            )
        }

        // Selection indicator
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) accentColor else BorderLight),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}