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
private val AccentAmber        = Color(0xFFD97706)
private val AccentAmberSurface = Color(0xFFFEF3C7)

// ─── Data class (mirrors RecycleScreen's Firebase write) ─────────────────────
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

    // ── Real-time listener on /RecyclingRequests ─────────────────────────────
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
                requests  = list.reversed()   // newest first
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
                    color    = AccentGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Empty state ───────────────────────────────────────────────────
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
                            .background(AccentGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♻️", fontSize = 46.sp, textAlign = TextAlign.Center)
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
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen,
                            contentColor   = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("♻️  Recycle a device", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Requests list ─────────────────────────────────────────────────
            else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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

// ─── Request card ─────────────────────────────────────────────────────────────
@Composable
private fun RecycleRequestCard(request: RecycleRequest) {

    // Condition → colour
    val (condColor, condSurface) = when (request.condition) {
        "Working" -> AccentGreen  to AccentGreenSurface
        "Damaged" -> AccentAmber  to AccentAmberSurface
        else      -> Color(0xFFDC2626) to Color(0xFFFEF2F2)   // Dead → red
    }

    // Action → emoji
    val actionEmoji = when (request.action) {
        "Donate"          -> "🤝"
        "Recycle safely"  -> "♻️"
        "Sell for parts"  -> "🔩"
        else              -> "📦"
    }

    // Collection → emoji
    val collectionEmoji = when (request.collectionMethod) {
        "Pickup"   -> "🚚"
        "Drop-off" -> "📦"
        else       -> "📍"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
    ) {

        // ── Device image (if available) ───────────────────────────────────────
        if (request.imageUri.isNotEmpty()) {
            AsyncImage(
                model              = request.imageUri,
                contentDescription = "Device photo",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            )
        }

        Column(
            modifier            = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        request.deviceName,
                        fontSize      = 15.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = TextPrimary,
                        letterSpacing = (-0.2).sp,
                        maxLines      = 1
                    )
                    Text(
                        "ID #${request.id.takeLast(6).uppercase()}",
                        fontSize = 11.sp,
                        color    = TextSecondary
                    )
                }

                // Condition chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(condSurface)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        when (request.condition) {
                            "Working" -> "✅"
                            "Damaged" -> "⚠️"
                            else      -> "💀"
                        },
                        fontSize = 11.sp
                    )
                    Text(
                        request.condition,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = condColor
                    )
                }
            }

            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

            // ── Detail badges ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action
                DetailBadge(
                    modifier = Modifier.weight(1f),
                    emoji    = actionEmoji,
                    label    = "Action",
                    value    = request.action,
                    bgColor  = AccentGreenSurface
                )

                // Collection method
                DetailBadge(
                    modifier = Modifier.weight(1f),
                    emoji    = collectionEmoji,
                    label    = "Collection",
                    value    = request.collectionMethod,
                    bgColor  = PageBg
                )
            }
        }
    }
}

// ─── Detail badge ─────────────────────────────────────────────────────────────
@Composable
private fun DetailBadge(
    modifier : Modifier,
    emoji    : String,
    label    : String,
    value    : String,
    bgColor  : Color
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 20.sp, textAlign = TextAlign.Center)
        Text(
            label,
            fontSize   = 10.sp,
            color      = TextSecondary,
            textAlign  = TextAlign.Center
        )
        Text(
            value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary,
            textAlign  = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun RecycledProductsScreenPreview() {
    RecycledProductsScreen(rememberNavController())
}