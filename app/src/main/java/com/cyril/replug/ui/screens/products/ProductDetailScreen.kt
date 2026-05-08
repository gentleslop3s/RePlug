package com.cyril.replug.ui.screens.products

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cyril.replug.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId    : String,
    navController: NavController,
    onBuyClick   : (Product) -> Unit = {}
) {
    val context    = LocalContext.current
    var product    by remember { mutableStateOf<Product?>(null) }
    var isLoading  by remember { mutableStateOf(true) }
    var wishlisted by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .child(productId)
            .get()
            .addOnSuccessListener { snapshot ->
                product   = snapshot.getValue(Product::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_LONG).show()
            }
    }

    // ── Loading ───────────────────────────────────────────────────────────────
    if (isLoading) {
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
                title = { },
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
                    IconButton(onClick = { wishlisted = !wishlisted }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = if (wishlisted) Icons.Rounded.Favorite
                                else Icons.Rounded.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint     = if (wishlisted) Color(0xFFEF4444) else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(onClick = { }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.ShoppingCart,
                                contentDescription = "Cart",
                                tint     = TextSecondary,
                                modifier = Modifier.size(18.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ── Message Seller button ─────────────────────────────────
                    OutlinedButton(
                        onClick = {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                            if (currentUserId == null) {
                                Toast.makeText(context, "Please log in to message the seller", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }

                            val sellerId = product!!.sellerId

                            if (sellerId.isNullOrEmpty()) {
                                Toast.makeText(context, "Seller info unavailable for this listing", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }

                            if (currentUserId == sellerId) {
                                Toast.makeText(context, "This is your own listing", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }

                            val chatId = listOf(currentUserId, sellerId, productId)
                                .sorted()
                                .joinToString("_")

                            val metaRef = FirebaseDatabase.getInstance()
                                .getReference("Chats/$chatId/metadata")

                            metaRef.get()
                                .addOnSuccessListener { snap ->
                                    if (!snap.exists()) {
                                        metaRef.setValue(mapOf(
                                            "buyerId"       to currentUserId,
                                            "sellerId"      to sellerId,
                                            "productId"     to productId,
                                            "productName"   to (product!!.name ?: ""),
                                            "lastMessage"   to "",
                                            "lastTimestamp" to System.currentTimeMillis()
                                        ))
                                    }
                                    navController.navigate("chat/$chatId/${product!!.name ?: "Product"}")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Could not open chat. Try again.", Toast.LENGTH_SHORT).show()
                                }
                        },
                        shape  = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(
                            Icons.Rounded.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Message", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // ── Buy Now button ────────────────────────────────────────
                    Button(
                        onClick  = { onBuyClick(product!!) },
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor   = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(
                            Icons.Rounded.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Buy Now", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Product image ─────────────────────────────────────────────────
            AsyncImage(
                model              = product!!.imageUrl,
                contentDescription = product!!.name,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            // ── Details card ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                // Category chip
                Box(
                    modifier = Modifier
                        .background(AccentBlueSurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = product!!.category ?: "Device",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = AccentBlue
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Product name
                Text(
                    text          = product!!.name ?: "",
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextPrimary,
                    letterSpacing = (-0.5).sp,
                    lineHeight    = 28.sp
                )

                Spacer(Modifier.height(8.dp))

                // Brand row
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Verified,
                        contentDescription = null,
                        tint     = AccentBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text     = product!!.brand ?: "Unknown brand",
                        fontSize = 13.sp,
                        color    = TextSecondary
                    )
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                Spacer(Modifier.height(20.dp))

                // Description heading
                Text(
                    "Description",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                // Description body
                Text(
                    text       = product!!.description ?: "",
                    fontSize   = 14.sp,
                    color      = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                Spacer(Modifier.height(20.dp))

                // Specs row
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SpecChip(label = "Category", value = product!!.category ?: "N/A")
                    SpecChip(label = "Brand",    value = product!!.brand    ?: "N/A")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─── Small spec chip ──────────────────────────────────────────────────────────
@Composable
private fun SpecChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PageBg)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}