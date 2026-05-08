package com.cyril.replug.ui.screens.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

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
private val AccentRed          = Color(0xFFDC2626)
private val AccentRedSurface   = Color(0xFFFEF2F2)
private val UnreadDot          = Color(0xFF1A6BF5)

// ─── Notification types ───────────────────────────────────────────────────────
enum class NotifType { ORDER, RECYCLE, MESSAGE, PROMO, SYSTEM }

// ─── Data class ───────────────────────────────────────────────────────────────
data class AppNotification(
    val id        : String       = "",
    val userId    : String       = "",
    val type      : String       = "SYSTEM",
    val title     : String       = "",
    val body      : String       = "",
    val timestamp : Long         = 0L,
    val isRead    : Boolean      = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {

    val auth          = remember { FirebaseAuth.getInstance() }
    val uid           = auth.currentUser?.uid ?: "unknown"
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(true) }

    val dbRef = remember {
        FirebaseDatabase.getInstance().getReference("Notifications").child(uid)
    }

    // ── Real-time listener ───────────────────────────────────────────────────
    DisposableEffect(uid) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<AppNotification>()
                snapshot.children.forEach { child ->
                    list.add(
                        AppNotification(
                            id        = child.child("id").getValue(String::class.java) ?: "",
                            userId    = uid,
                            type      = child.child("type").getValue(String::class.java) ?: "SYSTEM",
                            title     = child.child("title").getValue(String::class.java) ?: "",
                            body      = child.child("body").getValue(String::class.java) ?: "",
                            timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L,
                            isRead    = child.child("isRead").getValue(Boolean::class.java) ?: false
                        )
                    )
                }
                notifications = list.sortedByDescending { it.timestamp }
                isLoading     = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        }
        dbRef.addValueEventListener(listener)
        onDispose { dbRef.removeEventListener(listener) }
    }

    // ── Mark all as read helper ───────────────────────────────────────────────
    fun markAllRead() {
        notifications.filter { !it.isRead }.forEach { notif ->
            dbRef.child(notif.id).child("isRead").setValue(true)
        }
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ──────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Notifications",
                                fontSize      = 18.sp,
                                fontWeight    = FontWeight.SemiBold,
                                color         = TextPrimary,
                                letterSpacing = (-0.3).sp
                            )
                            // Unread badge
                            AnimatedVisibility(visible = unreadCount > 0, enter = fadeIn(), exit = fadeOut()) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(AccentBlue)
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "$unreadCount",
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White
                                    )
                                }
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
                    AnimatedVisibility(visible = unreadCount > 0, enter = fadeIn(), exit = fadeOut()) {
                        TextButton(onClick = { markAllRead() }) {
                            Text(
                                "Mark all read",
                                fontSize   = 13.sp,
                                color      = AccentBlue,
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
            else if (notifications.isEmpty()) {
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
                            Icons.Rounded.NotificationsNone,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "All caught up!",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "You have no notifications right now.\nCheck back later.",
                        fontSize   = 14.sp,
                        color      = TextSecondary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── Notification list ─────────────────────────────────────────────
            else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Unread section
                    val unread = notifications.filter { !it.isRead }
                    val read   = notifications.filter { it.isRead }

                    if (unread.isNotEmpty()) {
                        item {
                            SectionHeader(label = "New")
                        }
                        items(unread, key = { it.id }) { notif ->
                            NotificationCard(
                                notif   = notif,
                                onClick = {
                                    dbRef.child(notif.id).child("isRead").setValue(true)
                                }
                            )
                        }
                    }

                    if (read.isNotEmpty()) {
                        item {
                            SectionHeader(label = "Earlier")
                        }
                        items(read, key = { it.id }) { notif ->
                            NotificationCard(notif = notif)
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Section header ───────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(label: String) {
    Text(
        text          = label.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color         = TextSecondary,
        modifier      = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
    )
}

// ─── Notification card ────────────────────────────────────────────────────────
@Composable
private fun NotificationCard(
    notif   : AppNotification,
    onClick : () -> Unit = {}
) {
    val (iconEmoji, accentColor, surface) = when (notif.type) {
        "ORDER"   -> Triple("🛒", AccentBlue,  AccentBlueSurface)
        "RECYCLE" -> Triple("♻️", AccentGreen, AccentGreenSurface)
        "MESSAGE" -> Triple("💬", AccentAmber, AccentAmberSurface)
        "PROMO"   -> Triple("🎉", AccentRed,   AccentRedSurface)
        else      -> Triple("🔔", AccentBlue,  AccentBlueSurface)   // SYSTEM
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (notif.isRead) Color.White else Color.White)
            .border(
                width = if (notif.isRead) 1.dp else 1.5.dp,
                color = if (notif.isRead) BorderLight else accentColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(surface),
            contentAlignment = Alignment.Center
        ) {
            Text(iconEmoji, fontSize = 20.sp)
        }

        // Text content
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                notif.title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
                letterSpacing = (-0.2).sp
            )
            Text(
                notif.body,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                formatTimestamp(notif.timestamp),
                fontSize = 11.sp,
                color    = TextSecondary.copy(alpha = 0.65f)
            )
        }

        // Unread dot
        if (!notif.isRead) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(UnreadDot)
            )
        }
    }
}

// ─── Timestamp formatter ──────────────────────────────────────────────────────
private fun formatTimestamp(ts: Long): String {
    if (ts == 0L) return ""
    val now  = System.currentTimeMillis()
    val diff = now - ts
    return when {
        diff < 60_000              -> "Just now"
        diff < 3_600_000           -> "${diff / 60_000}m ago"
        diff < 86_400_000          -> "${diff / 3_600_000}h ago"
        diff < 86_400_000 * 7      -> "${diff / 86_400_000}d ago"
        else                       -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(ts))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen(rememberNavController())
}