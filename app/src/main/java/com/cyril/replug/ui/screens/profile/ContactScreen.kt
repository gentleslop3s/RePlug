package com.cyril.replug.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
private val AccentAmber        = Color(0xFFD97706)
private val AccentAmberSurface = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {

    val context    = LocalContext.current
    var name       by remember { mutableStateOf("") }
    var email      by remember { mutableStateOf("") }
    var message    by remember { mutableStateOf("") }
    var isSending  by remember { mutableStateOf(false) }
    var isSent     by remember { mutableStateOf(false) }

    fun sendMessage() {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        isSending = true
        val ref  = FirebaseDatabase.getInstance().getReference("ContactMessages").push()
        val data = mapOf(
            "id"        to ref.key,
            "name"      to name.trim(),
            "email"     to email.trim(),
            "message"   to message.trim(),
            "timestamp" to System.currentTimeMillis()
        )
        ref.setValue(data)
            .addOnSuccessListener {
                isSending = false
                isSent    = true
                name      = ""
                email     = ""
                message   = ""
            }
            .addOnFailureListener {
                isSending = false
                Toast.makeText(context, "Failed to send. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        containerColor = PageBg,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contact Us",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.SemiBold,
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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Hero strip ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(vertical = 28.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.SupportAgent,
                            contentDescription = null,
                            tint     = AccentBlue,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "We'd love to hear from you",
                        fontSize      = 18.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.3).sp,
                        textAlign     = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Send us a message and we'll get back\nto you within 24 hours.",
                        fontSize  = 13.sp,
                        color     = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Quick contact tiles ───────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickContactTile(
                    modifier = Modifier.weight(1f),
                    emoji    = "📧",
                    label    = "Email",
                    value    = "hello@\nreplug.co.ke",
                    bgColor  = AccentBlueSurface,
                    onClick  = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:hello@replug.co.ke"))
                        context.startActivity(intent)
                    }
                )
                QuickContactTile(
                    modifier = Modifier.weight(1f),
                    emoji    = "📱",
                    label    = "WhatsApp",
                    value    = "+254 712\n345 678",
                    bgColor  = AccentGreenSurface,
                    onClick  = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254712345678"))
                        context.startActivity(intent)
                    }
                )
                QuickContactTile(
                    modifier = Modifier.weight(1f),
                    emoji    = "🐦",
                    label    = "Twitter",
                    value    = "@replug\nke",
                    bgColor  = AccentAmberSurface,
                    onClick  = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/replugke"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Success state ─────────────────────────────────────────────────
            if (isSent) {
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(containerColor = AccentGreenSurface),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✅", fontSize = 36.sp)
                        Text(
                            "Message sent!",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AccentGreen
                        )
                        Text(
                            "We'll get back to you within 24 hours.",
                            fontSize  = 13.sp,
                            color     = AccentGreen.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { isSent = false }) {
                            Text("Send another message", color = AccentGreen, fontSize = 13.sp)
                        }
                    }
                }
            }

            // ── Message form ──────────────────────────────────────────────────
            else {
                ContactCard {
                    ContactSectionLabel("Send a message")
                    Spacer(Modifier.height(14.dp))

                    ContactField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = "Your name",
                        icon          = Icons.Rounded.Person,
                        keyboardType  = KeyboardType.Text
                    )
                    Spacer(Modifier.height(12.dp))
                    ContactField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = "Email address",
                        icon          = Icons.Rounded.Email,
                        keyboardType  = KeyboardType.Email
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = message,
                        onValueChange = { message = it },
                        label         = { Text("Your message", fontSize = 13.sp) },
                        placeholder   = { Text("Tell us how we can help…", fontSize = 13.sp, color = TextSecondary.copy(0.6f)) },
                        modifier      = Modifier.fillMaxWidth(),
                        minLines      = 5,
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BorderLight,
                            focusedBorderColor   = AccentBlue,
                            unfocusedLabelColor  = TextSecondary,
                            focusedLabelColor    = AccentBlue,
                            cursorColor          = AccentBlue
                        )
                    )
                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick   = { sendMessage() },
                        enabled   = !isSending,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor         = SurfaceDark,
                            contentColor           = Color.White,
                            disabledContainerColor = SurfaceDark.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                color       = Color.White,
                                modifier    = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Rounded.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Send message",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ── Office info card ──────────────────────────────────────────────
            ContactCard {
                ContactSectionLabel("Find us")
                Spacer(Modifier.height(4.dp))

                ContactInfoRow(icon = Icons.Rounded.LocationOn, label = "Nairobi, Kenya")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ContactInfoRow(icon = Icons.Rounded.Schedule,   label = "Mon – Fri, 8 AM – 6 PM EAT")
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ContactInfoRow(icon = Icons.Rounded.Language,   label = "www.replug.co.ke")
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun QuickContactTile(
    modifier : Modifier,
    emoji    : String,
    label    : String,
    value    : String,
    bgColor  : Color,
    onClick  : () -> Unit
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(emoji, fontSize = 22.sp, textAlign = TextAlign.Center)
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, textAlign = TextAlign.Center)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Medium,   color = TextPrimary,   textAlign = TextAlign.Center, lineHeight = 15.sp)
    }
}

@Composable
private fun ContactCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun ContactSectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color         = TextSecondary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactField(
    value         : String,
    onValueChange : (String) -> Unit,
    label         : String,
    icon          : ImageVector,
    keyboardType  : KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        leadingIcon   = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlueSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
            }
        },
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors          = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderLight,
            focusedBorderColor   = AccentBlue,
            unfocusedLabelColor  = TextSecondary,
            focusedLabelColor    = AccentBlue,
            cursorColor          = AccentBlue
        )
    )
}

@Composable
private fun ContactInfoRow(icon: ImageVector, label: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlueSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
        }
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun ContactScreenPreview() {
    ContactScreen(rememberNavController())
}