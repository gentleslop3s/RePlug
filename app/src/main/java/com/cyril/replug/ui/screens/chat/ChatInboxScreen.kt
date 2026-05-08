package com.cyril.replug.ui.screens.chat

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.cyril.replug.navigation.*

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val AccentBlue    = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)

data class ChatPreview(
    val chatId: String = "",
    val productId: String = "",
    val productName: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInboxScreen(navController: NavController) {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedIndex by remember { mutableStateOf(-1) }

    // ── Load all chats where currentUser is buyer or seller ───────────────────
    LaunchedEffect(currentUserId) {
        if (currentUserId.isEmpty()) return@LaunchedEffect
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ChatPreview>()
                snapshot.children.forEach { chatSnap ->
                    val meta = chatSnap.child("metadata")
                    val buyerId  = meta.child("buyerId").value?.toString()  ?: ""
                    val sellerId = meta.child("sellerId").value?.toString() ?: ""
                    if (buyerId == currentUserId || sellerId == currentUserId) {
                        val otherUserId = if (buyerId == currentUserId) sellerId else buyerId
                        list.add(
                            ChatPreview(
                                chatId        = chatSnap.key ?: "",
                                productId     = meta.child("productId").value?.toString()    ?: "",
                                productName   = meta.child("productName").value?.toString()  ?: "",
                                otherUserId   = otherUserId,
                                otherUserName = meta.child("otherUserName").value?.toString() ?: "User",
                                lastMessage   = meta.child("lastMessage").value?.toString()  ?: "",
                                lastTimestamp = meta.child("lastTimestamp").value as? Long    ?: 0L
                            )
                        )
                    }
                }
                chats     = list.sortedByDescending { it.lastTimestamp }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ───────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messages",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        // ── Bottom nav ────────────────────────────────────────────────────────
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, BorderLight, RoundedCornerShape(0.dp))
            ) {
                data class NavItem(val label: String, val icon: ImageVector, val route: String, val index: Int)
                val items = listOf(
                    NavItem("Home",     Icons.Rounded.Home,           ROUTE_HOME,     0),
                    NavItem("Search",   Icons.Rounded.Search,         ROUTE_SEARCH,   1),
                    NavItem("Add",      Icons.Rounded.Add,            ROUTE_ADD,      2),
                    NavItem("Inbox",    Icons.Rounded.Inbox,          ROUTE_CHAT_INBOX, 3),
                    NavItem("Profile",  Icons.Rounded.Person,         ROUTE_PROFILE,  4),
                )
                items.forEach { item ->
                    val isSelected = selectedIndex == item.index
                    NavigationBarItem(
                        icon = {
                            if (item.index == 2) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(item.icon, contentDescription = item.label,
                                        tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.label,
                                    modifier = Modifier.size(22.dp))
                            }
                        },
                        label = {
                            Text(item.label, fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        },
                        selected = isSelected,
                        onClick  = { selectedIndex = item.index; navController.navigate(item.route) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor   = SurfaceDark,
                            selectedTextColor   = SurfaceDark,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }

            chats.isEmpty() -> {
                // ── Empty state ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(AccentBlueSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.ChatBubbleOutline,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "No messages yet",
                        fontSize      = 20.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Start a conversation from a product listing",
                        fontSize  = 14.sp,
                        color     = TextSecondary
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chats) { chat ->
                        ChatPreviewCard(
                            chat    = chat,
                            onClick = {
                                navController.navigate("chat/${chat.chatId}/${chat.productName}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatPreviewCard(chat: ChatPreview, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AccentBlueSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = chat.otherUserName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize  = 18.sp,
                fontWeight = FontWeight.Bold,
                color     = AccentBlue
            )
        }

        // Text content
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text       = chat.otherUserName,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary
            )
            Text(
                text     = chat.productName,
                fontSize = 12.sp,
                color    = AccentBlue,
                fontWeight = FontWeight.Medium
            )
            Text(
                text     = chat.lastMessage.ifEmpty { "No messages yet" },
                fontSize = 13.sp,
                color    = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Chevron
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint     = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInboxPreview() = ChatInboxScreen(rememberNavController())