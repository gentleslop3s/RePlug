package com.cyril.replug.ui.screens.add

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// ─── Colour tokens (shared with SellScreen) ───────────────────────────────────
private val SurfaceDark    = Color(0xFF0F1923)
private val AccentGreen    = Color(0xFF16A34A)   // recycling-themed accent
private val AccentGreenLight = Color(0xFFDCFCE7)
private val BorderLight    = Color(0xFFE2E6EC)
private val TextPrimary    = Color(0xFF111827)
private val TextSecondary  = Color(0xFF6B7280)
private val ChipUnselected = Color(0xFFEEF0F4)

@Composable
fun RecycleScreen(navController: NavController) {

    val context  = LocalContext.current
    val auth     = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("RecyclingRequests")

    var imageUris        by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var deviceName       by remember { mutableStateOf("") }
    var condition        by remember { mutableStateOf("") }
    var action           by remember { mutableStateOf("") }
    var collectionMethod by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> if (uris.isNotEmpty()) imageUris = uris }

    fun saveData() {
        val userId    = auth.currentUser?.uid ?: "unknown"
        val requestId = database.push().key!!
        val data = mapOf(
            "id"               to requestId,
            "userId"           to userId,
            "deviceName"       to deviceName,
            "condition"        to condition,
            "action"           to action,
            "collectionMethod" to collectionMethod,
            "imageUri"         to (imageUris.firstOrNull()?.toString() ?: "")
        )
        database.child(requestId).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Request submitted!", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to submit. Try again.", Toast.LENGTH_LONG).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .verticalScroll(rememberScrollState())
    ) {

        // ── Dark header ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Recycling badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentGreen.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "♻️  Eco Drop",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF86EFAC),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Recycle your device",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Give electronics a second life",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        }

        // ── Device photo card ────────────────────────────────────────────────
        RecycleSectionCard(modifier = Modifier.padding(top = 16.dp)) {
            RecycleSectionLabel("Device photo")
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEEF0F4))
                    .border(
                        width = 1.5.dp,
                        color = if (imageUris.isEmpty()) BorderLight else AccentGreen.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = imageUris.firstOrNull(),
                    label = "photo-anim"
                ) { uri ->
                    if (uri != null) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Device photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tap to upload a photo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                "Helps us assess the device",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = imageUris.isNotEmpty(),
                enter = fadeIn(), exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Photo added",
                        fontSize = 13.sp,
                        color = AccentGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Device info card ─────────────────────────────────────────────────
        RecycleSectionCard {
            RecycleSectionLabel("Device info")
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Device name", fontSize = 13.sp) },
                placeholder = { Text("e.g. iPhone 6s, Samsung A32", fontSize = 13.sp, color = TextSecondary.copy(0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderLight,
                    focusedBorderColor   = AccentGreen,
                    unfocusedLabelColor  = TextSecondary,
                    focusedLabelColor    = AccentGreen,
                    cursorColor          = AccentGreen
                )
            )
        }

        // ── Condition card ───────────────────────────────────────────────────
        RecycleSectionCard {
            RecycleSectionLabel("Device condition")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeviceCondition.entries.forEach { opt ->
                    val selected = condition == opt.label
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) AccentGreen else ChipUnselected)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = if (selected) Color.Transparent else BorderLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { condition = opt.label }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(opt.emoji, fontSize = 22.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            opt.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            opt.hint,
                            fontSize = 10.sp,
                            color = if (selected) Color.White.copy(0.75f) else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // ── Desired action card ───────────────────────────────────────────────
        RecycleSectionCard {
            RecycleSectionLabel("What would you like to do?")
            Spacer(Modifier.height(10.dp))
            DesiredAction.entries.forEach { opt ->
                val selected = action == opt.label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) AccentGreenLight else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (selected) AccentGreen.copy(alpha = 0.4f) else BorderLight,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { action = opt.label }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(opt.emoji, fontSize = 20.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            opt.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) AccentGreen else TextPrimary
                        )
                        Text(
                            opt.hint,
                            fontSize = 12.sp,
                            color = if (selected) AccentGreen.copy(alpha = 0.75f) else TextSecondary
                        )
                    }
                    if (selected) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ── Collection method card ────────────────────────────────────────────
        RecycleSectionCard {
            RecycleSectionLabel("Collection method")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CollectionMethod.entries.forEach { opt ->
                    val selected = collectionMethod == opt.label
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) AccentGreen else ChipUnselected)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = if (selected) Color.Transparent else BorderLight,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { collectionMethod = opt.label }
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(opt.emoji, fontSize = 28.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            opt.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            opt.hint,
                            fontSize = 11.sp,
                            color = if (selected) Color.White.copy(0.75f) else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // ── Submit CTA ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = {
                    if (deviceName.isEmpty() || condition.isEmpty() || action.isEmpty() || collectionMethod.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (auth.currentUser == null) {
                        auth.signInAnonymously()
                            .addOnSuccessListener { saveData() }
                            .addOnFailureListener {
                                Toast.makeText(context, "Auth failed", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        saveData()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    "Submit recycling request",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ─── Data enums ───────────────────────────────────────────────────────────────
private enum class DeviceCondition(val label: String, val emoji: String, val hint: String) {
    Working("Working", "✅", "Powers on fine"),
    Damaged("Damaged", "⚠️", "Cracked / faulty"),
    Dead("Dead",       "💀", "Won't power on")
}

private enum class DesiredAction(val label: String, val emoji: String, val hint: String) {
    Donate("Donate",          "🤝", "Give to someone in need"),
    Recycle("Recycle safely", "♻️", "Responsible e-waste disposal"),
    Parts("Sell for parts",   "🔩", "Recover value from components")
}

private enum class CollectionMethod(val label: String, val emoji: String, val hint: String) {
    Pickup("Pickup",    "🚚", "We come to you"),
    DropOff("Drop-off", "📦", "Visit a collection point")
}

// ─── Reusable card wrapper ────────────────────────────────────────────────────
@Composable
private fun RecycleSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

// ─── Section label ────────────────────────────────────────────────────────────
@Composable
private fun RecycleSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = TextSecondary
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun RecycleScreenPreview() {
    RecycleScreen(rememberNavController())
}