package com.cyril.replug.ui.screens.payment

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cyril.replug.models.Product
import com.google.firebase.database.FirebaseDatabase

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    productId    : String,
    navController: NavController,
    onPaymentDone: () -> Unit = {}
) {
    val context        = LocalContext.current
    var product        by remember { mutableStateOf<Product?>(null) }
    var selectedMethod by remember { mutableStateOf("") }
    var loading        by remember { mutableStateOf(true) }
    var isProcessing   by remember { mutableStateOf(false) }

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

        // ── Top bar ──────────────────────────────────────────────────────────
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg)
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
                            fontSize      = 20.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextPrimary,
                            letterSpacing = (-0.4).sp
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedMethod.isEmpty()) {
                                Toast.makeText(context, "Select a payment method", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isProcessing = true
                            val orderRef  = FirebaseDatabase.getInstance().getReference("Orders").push()
                            val orderData = mapOf(
                                "orderId"       to orderRef.key,
                                "productId"     to productId,
                                "productName"   to (product!!.name ?: ""),
                                "price"         to (product!!.price ?: ""),
                                "paymentMethod" to selectedMethod,
                                "status"        to "Pending",
                                "imageUrl"      to (product!!.imageUrl ?: "")  // ← saved for OrdersScreen
                            )
                            orderRef.setValue(orderData)
                                .addOnSuccessListener {
                                    isProcessing = false
                                    Toast.makeText(context, "Order placed! 🎉", Toast.LENGTH_SHORT).show()
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
                            containerColor         = SurfaceDark,
                            contentColor           = Color.White,
                            disabledContainerColor = SurfaceDark.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Pay Now", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Product image hero ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (!product!!.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model              = product!!.imageUrl,
                        contentDescription = "Product image",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(Color(0xFFE8ECF4), Color(0xFFCDD5E8)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.ImageNotSupported, contentDescription = null, tint = Color(0xFFB0BAD0), modifier = Modifier.size(48.dp))
                    }
                }

                // Bottom scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))
                        )
                )

                // Product name + category over image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Text(
                        text          = product!!.name ?: "—",
                        fontSize      = 20.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.4).sp
                    )
                    if (!product!!.brand.isNullOrEmpty()) {
                        Text(
                            text     = "${product!!.brand}  ·  ${product!!.category ?: ""}",
                            fontSize = 13.sp,
                            color    = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // ── Order summary card ────────────────────────────────────────────
            CheckoutCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                CheckoutSectionLabel("Order summary")
                Spacer(Modifier.height(12.dp))

                SummaryRow(label = "Product",   value = product!!.name     ?: "—")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow(label = "Category",  value = product!!.category ?: "—")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow(label = "Brand",     value = product!!.brand    ?: "—")
                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Total row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(
                        "Ksh ${product!!.price}",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = AccentBlue,
                        letterSpacing = (-0.3).sp
                    )
                }
            }

            // ── Payment method section ────────────────────────────────────────
            CheckoutCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                CheckoutSectionLabel("Payment method")
                Spacer(Modifier.height(12.dp))

                PaymentMethodCard(
                    emoji        = "📱",
                    title        = "M-Pesa",
                    subtitle     = "Pay via Safaricom M-Pesa",
                    isSelected   = selectedMethod == "M-Pesa",
                    accentColor  = AccentGreen,
                    surfaceColor = AccentGreenSurface,
                    onClick      = { selectedMethod = "M-Pesa" }
                )
                Spacer(Modifier.height(10.dp))
                PaymentMethodCard(
                    emoji        = "💳",
                    title        = "Visa / Card",
                    subtitle     = "Pay with debit or credit card",
                    isSelected   = selectedMethod == "Visa / Card",
                    accentColor  = AccentBlue,
                    surfaceColor = AccentBlueSurface,
                    onClick      = { selectedMethod = "Visa / Card" }
                )
            }

            // ── Security note ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    "Payments are encrypted and secure",
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Checkout card wrapper ────────────────────────────────────────────────────
@Composable
private fun CheckoutCard(
    modifier: Modifier = Modifier,
    content : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

// ─── Section label ────────────────────────────────────────────────────────────
@Composable
private fun CheckoutSectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color         = TextSecondary
    )
}

// ─── Summary row ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

// ─── Payment method card ──────────────────────────────────────────────────────
@Composable
private fun PaymentMethodCard(
    emoji       : String,
    title       : String,
    subtitle    : String,
    isSelected  : Boolean,
    accentColor : Color,
    surfaceColor: Color,
    onClick     : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) surfaceColor else Color(0xFFF7F8FA))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else BorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Emoji badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }

        // Text
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title,    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, letterSpacing = (-0.2).sp)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }

        // Radio indicator
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) accentColor else BorderLight),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
    }
}