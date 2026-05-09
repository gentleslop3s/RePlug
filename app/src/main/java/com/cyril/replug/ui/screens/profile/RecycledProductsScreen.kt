package com.cyril.replug.ui.screens.profile

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
private val SurfaceDark        = Color(0xFF0F1923)
private val PageBg             = Color(0xFFF0F2F5)
private val BorderLight        = Color(0xFFE2E6EC)
private val TextPrimary        = Color(0xFF111827)
private val TextSecondary      = Color(0xFF6B7280)
private val AccentBlue         = Color(0xFF1A6BF5)
private val AccentGreen        = Color(0xFF16A34A)
private val AccentGreenSurface = Color(0xFFDCFCE7)
private val AccentAmber        = Color(0xFFD97706)
private val AccentAmberSurface = Color(0xFFFEF3C7)
private val AccentRed          = Color(0xFFDC2626)
private val AccentRedSurface   = Color(0xFFFEF2F2)

// ─── Data class ───────────────────────────────────────────────────────────────
data class RecycleRequest(
    val id               : String = "",
    val userId           : String = "",
    val deviceName       : String = "",
    val condition        : String = "",
    val action           : String = "",
    val collectionMethod : String = "",
    val imageUri         : String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycledProductsScreen(navController: NavController) {

    var requests  by remember { mutableStateOf<List<RecycleRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ── Real-time listener ────────────────────────────────────────────────────
    DisposableEffect(Unit) {
        val ref      = FirebaseDatabase.getInstance().getReference("RecyclingRequests")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<RecycleRequest>()
                snapshot.children.forEach { child ->
                    list.add(
                        RecycleRequest(
                            id               = child.child("id").getValue(String::class.java) ?: "",
                            userId           = child.child("userId").getValue(String::class.java) ?: "",
                            deviceName       = child.child("deviceName").getValue(String::class.java) ?: "Unknown device",
                            condition        = child.child("condition").getValue(String::class.java) ?: "—",
                            action           = child.child("action").getValue(String::class.java) ?: "—",
                            collectionMethod = child.child("collectionMethod").getValue(String::class.java) ?: "—",
                            imageUri         = child.child("imageUri").getValue(String::class.java) ?: ""
                        )
                    )
                }
                requests  = list.reversed()
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar — identical pattern to MyListingsScreen ───────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Recycled Devices",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        AnimatedVisibility(
                            visible = !isLoading && requests.isNotEmpty(),
                            enter   = fadeIn(),
                            exit    = fadeOut()
                        ) {
                            Text(
                                "${requests.size} request${if (requests.size != 1) "s" else ""}",
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
            else if (requests.isEmpty()) {
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
                            Icons.Rounded.Recycling,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "No recycled devices",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Devices you submit for recycling\nwill appear here.",
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
                        Icon(Icons.Rounded.Recycling, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Recycle a device", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── List — same padding/spacing as MyListingsScreen grid ──────────
            else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        RecycleRequestCard(request)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Card — same shape/elevation/border as MyListingCard ─────────────────────
@Composable
private fun RecycleRequestCard(request: RecycleRequest) {

    // Condition → colour
    val (condColor, condSurface) = when (request.condition) {
        "Working" -> AccentGreen to AccentGreenSurface
        "Damaged" -> AccentAmber to AccentAmberSurface
        else      -> AccentRed   to AccentRedSurface
    }

    // Action → emoji
    val actionEmoji = when (request.action) {
        "Donate"         -> "🤝"
        "Recycle safely" -> "♻️"
        "Sell for parts" -> "🔩"
        else             -> "📦"
    }

    // Collection → emoji
    val collectionEmoji = when (request.collectionMethod) {
        "Pickup"   -> "🚚"
        "Drop-off" -> "📦"
        else       -> "📍"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column {

            // ── Image with condition chip overlay — mirrors MyListingCard image box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (request.imageUri.isNotEmpty()) {
                    AsyncImage(
                        model              = request.imageUri,
                        contentDescription = "Device photo",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    // Placeholder gradient — same as MyListingCard
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
                            Icons.Rounded.DevicesOther,
                            contentDescription = null,
                            tint     = Color(0xFFB0BAD0),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Condition chip — top-end overlay, same position as action buttons in MyListingCard
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(condSurface.copy(alpha = 0.95f))
                        .border(0.5.dp, condColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (request.condition) {
                            "Working" -> "✅  ${request.condition}"
                            "Damaged" -> "⚠️  ${request.condition}"
                            else      -> "💀  ${request.condition}"
                        },
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = condColor
                    )
                }
            }

            // ── Info section — same padding as MyListingCard product info ─────
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Device name
                Text(
                    text          = request.deviceName,
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextPrimary,
                    maxLines      = 1,
                    letterSpacing = (-0.2).sp
                )

                // ID — same role as price line in MyListingCard
                Text(
                    text       = "#${request.id.takeLast(6).uppercase()}",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = AccentBlue
                )

                // Action + collection — same role as category line
                Text(
                    text     = "$actionEmoji ${request.action}  ·  $collectionEmoji ${request.collectionMethod}",
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
fun RecycledProductsScreenPreview() {
    RecycledProductsScreen(rememberNavController())
}