package com.cyril.replug.ui.screens.profile

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.data.ProductViewModel
import com.cyril.replug.models.Product
import com.google.firebase.auth.FirebaseAuth

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val AccentBlue    = Color(0xFF1A6BF5)
private val AccentRed     = Color(0xFFDC2626)
private val AccentRedSurface = Color(0xFFFEF2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(navController: NavController) {

    val context   = LocalContext.current
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val viewModel: ProductViewModel = viewModel(
        viewModelStoreOwner = context as androidx.activity.ComponentActivity
    )

    // Only show this seller's products
    val myProducts = viewModel.products.filter { it.sellerId == currentUid }

    // Delete confirmation dialog state
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor   = Color.White,
            shape            = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AccentRedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint     = AccentRed,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    "Delete listing?",
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextPrimary,
                    textAlign     = TextAlign.Center
                )
            },
            text = {
                Text(
                    "\"${product.name}\" will be permanently removed from the marketplace.",
                    fontSize  = 14.sp,
                    color     = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        product.id?.let { viewModel.deleteProduct(it, context) }
                        productToDelete = null
                    },
                    colors    = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape     = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { productToDelete = null },
                    shape   = RoundedCornerShape(12.dp),
                    border  = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ──────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Listings",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        AnimatedVisibility(visible = myProducts.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            Text(
                                "${myProducts.size} listing${if (myProducts.size != 1) "s" else ""}",
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

            // ── Empty state ───────────────────────────────────────────────────
            if (myProducts.isEmpty()) {
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
                            .background(Color(0xFFEFF4FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Inventory2,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "No listings yet",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Products you list for sale will\nappear here.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick   = { navController.navigate(com.cyril.replug.navigation.ROUTE_SELL) },
                        modifier  = Modifier.fillMaxWidth().height(52.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("List an item", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Grid of listings ──────────────────────────────────────────────
            else {
                LazyVerticalGrid(
                    columns             = GridCells.Fixed(2),
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    items(myProducts, key = { it.id ?: it.hashCode().toString() }) { product ->
                        MyListingCard(
                            product   = product,
                            onDelete  = { productToDelete = product },
                            onEdit    = {
                                product.id?.let { id ->
                                    navController.navigate("editProduct/$id")
                                }
                            }
                        )
                    }
                    // Bottom spacer item
                    item { Spacer(Modifier.height(16.dp)) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Listing card ─────────────────────────────────────────────────────────────
@Composable
private fun MyListingCard(
    product  : Product,
    onDelete : () -> Unit,
    onEdit   : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column {

            // ── Image with action buttons overlay ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Product image
                if (!product.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model              = product.imageUrl,
                        contentDescription = product.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(Color(0xFFE8ECF4), Color(0xFFCDD5E8))),
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.ImageNotSupported, contentDescription = null, tint = Color(0xFFB0BAD0), modifier = Modifier.size(28.dp))
                    }
                }

                // ── Action buttons: Edit + Delete ─────────────────────────────
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Edit button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.92f))
                            .border(0.5.dp, BorderLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = onEdit,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = "Edit",
                                tint     = AccentBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Delete button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.92f))
                            .border(0.5.dp, BorderLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = onDelete,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = "Delete",
                                tint     = AccentRed,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // ── Product info ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text          = product.name ?: "No name",
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextPrimary,
                    maxLines      = 1,
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text       = "Ksh ${product.price}",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = AccentBlue
                )
                Text(
                    text     = product.category ?: "",
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
fun MyListingsScreenPreview() {
    MyListingsScreen(rememberNavController())
}