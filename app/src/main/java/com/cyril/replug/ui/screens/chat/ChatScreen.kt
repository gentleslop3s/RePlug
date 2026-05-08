package com.cyril.replug.ui.screens.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)
private val PageBg        = Color(0xFFF0F2F5)
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val AccentBlue    = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)

data class Message(
    val messageId : String = "",
    val senderId  : String = "",
    val text      : String = "",
    val timestamp : Long   = 0L
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId      : String,
    productName : String,
    navController: NavController
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var messages  by remember { mutableStateOf<List<Message>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()
    val dbRef      = FirebaseDatabase.getInstance().getReference("Chats/$chatId/messages")

    // ── Listen for messages in real time ──────────────────────────────────────
    LaunchedEffect(chatId) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.map { snap ->
                    Message(
                        messageId = snap.key ?: "",
                        senderId  = snap.child("senderId").value?.toString()  ?: "",
                        text      = snap.child("text").value?.toString()      ?: "",
                        timestamp = snap.child("timestamp").value as? Long    ?: 0L
                    )
                }.sortedBy { it.timestamp }
                messages = list
                if (list.isNotEmpty()) {
                    scope.launch { listState.animateScrollToItem(list.lastIndex) }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        val msgRef = dbRef.push()
        val msgData = mapOf(
            "senderId"  to currentUserId,
            "text"      to text,
            "timestamp" to System.currentTimeMillis()
        )
        msgRef.setValue(msgData)
        // Update metadata last message
        FirebaseDatabase.getInstance()
            .getReference("Chats/$chatId/metadata")
            .updateChildren(mapOf(
                "lastMessage"   to text,
                "lastTimestamp" to System.currentTimeMillis()
            ))
        inputText = ""
    }

    Scaffold(
        containerColor = PageBg,

        // ── Top bar ───────────────────────────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            productName,
                            fontSize      = 15.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextPrimary,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            "Tap to view product",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        // ── Message input bar ─────────────────────────────────────────────────
        bottomBar = {
            Surface(
                color    = Color.White,
                modifier = Modifier.border(1.dp, BorderLight, RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value         = inputText,
                        onValueChange = { inputText = it },
                        placeholder   = {
                            Text("Type a message...", color = TextSecondary, fontSize = 14.sp)
                        },
                        shape   = RoundedCornerShape(24.dp),
                        colors  = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = AccentBlue,
                            unfocusedBorderColor    = BorderLight,
                            focusedContainerColor   = PageBg,
                            unfocusedContainerColor = PageBg
                        ),
                        modifier  = Modifier.weight(1f),
                        singleLine = false,
                        maxLines   = 4
                    )

                    // Send button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (inputText.trim().isEmpty()) BorderLight else SurfaceDark)
                            .then(
                                if (inputText.trim().isNotEmpty())
                                    Modifier.then(Modifier) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = { sendMessage() },
                            enabled  = inputText.trim().isNotEmpty(),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Send,
                                contentDescription = "Send",
                                tint     = if (inputText.trim().isEmpty()) TextSecondary else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            state           = listState,
            modifier        = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding  = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                val isMine = message.senderId == currentUserId
                MessageBubble(message = message, isMine = isMine)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        // Avatar for other user
        if (!isMine) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentBlueSurface)
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint     = AccentBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (isMine) 18.dp else 4.dp,
                            bottomEnd   = if (isMine) 4.dp  else 18.dp
                        )
                    )
                    .background(if (isMine) SurfaceDark else Color.White)
                    .border(
                        width = if (isMine) 0.dp else 1.dp,
                        color = if (isMine) Color.Transparent else BorderLight,
                        shape = RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (isMine) 18.dp else 4.dp,
                            bottomEnd   = if (isMine) 4.dp  else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .widthIn(max = 260.dp)
            ) {
                Text(
                    text      = message.text,
                    fontSize  = 14.sp,
                    color     = if (isMine) Color.White else TextPrimary,
                    lineHeight = 20.sp
                )
            }
        }

        if (isMine) Spacer(Modifier.width(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() = ChatScreen("chatId", "iPhone 16", rememberNavController())