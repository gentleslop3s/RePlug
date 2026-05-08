package com.cyril.replug.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)
private val ErrorRed          = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {

    val context  = LocalContext.current
    val auth     = remember { FirebaseAuth.getInstance() }
    val fireUser = auth.currentUser
    val dbRef    = remember {
        FirebaseDatabase.getInstance().getReference("users/${fireUser?.uid ?: "unknown"}")
    }

    // ── Pre-fill fields from Firebase ────────────────────────────────────────
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(fireUser?.uid) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name     = snapshot.child("name").getValue(String::class.java)
                    ?: fireUser?.displayName ?: ""
                email    = snapshot.child("email").getValue(String::class.java)
                    ?: fireUser?.email ?: ""
                phone    = snapshot.child("phone").getValue(String::class.java) ?: ""
                location = snapshot.child("location").getValue(String::class.java) ?: ""
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ── Save handler ─────────────────────────────────────────────────────────
    fun saveChanges() {
        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(context, "Name and email are required", Toast.LENGTH_SHORT).show()
            return
        }
        isSaving = true
        val updates = mapOf(
            "name"     to name.trim(),
            "email"    to email.trim(),
            "phone"    to phone.trim(),
            "location" to location.trim()
        )
        dbRef.updateChildren(updates)
            .addOnSuccessListener {
                isSaving = false
                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            .addOnFailureListener {
                isSaving = false
                Toast.makeText(context, "Failed to save. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
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
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Edit profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Changes are saved to your account",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Personal info card ───────────────────────────────────────────────
        EditSectionCard {
            EditSectionLabel("Personal info")
            Spacer(Modifier.height(14.dp))

            EditField(
                value         = name,
                onValueChange = { name = it },
                label         = "Full name",
                icon          = Icons.Rounded.Person,
                keyboardType  = KeyboardType.Text
            )
            Spacer(Modifier.height(12.dp))
            EditField(
                value         = email,
                onValueChange = { email = it },
                label         = "Email address",
                icon          = Icons.Rounded.Email,
                keyboardType  = KeyboardType.Email
            )
        }

        // ── Contact card ─────────────────────────────────────────────────────
        EditSectionCard {
            EditSectionLabel("Contact & location")
            Spacer(Modifier.height(14.dp))

            EditField(
                value         = phone,
                onValueChange = { phone = it },
                label         = "Phone number",
                icon          = Icons.Rounded.Phone,
                placeholder   = "e.g. +254 712 345 678",
                keyboardType  = KeyboardType.Phone
            )
            Spacer(Modifier.height(12.dp))
            EditField(
                value         = location,
                onValueChange = { location = it },
                label         = "Location",
                icon          = Icons.Rounded.LocationOn,
                placeholder   = "e.g. Nairobi, Kenya",
                keyboardType  = KeyboardType.Text
            )
        }

        // ── Save button ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick  = { saveChanges() },
                enabled  = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor   = Color.White,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color     = Color.White,
                        modifier  = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Save changes",
                        fontSize     = 15.sp,
                        fontWeight   = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }

        // ── Discard link ─────────────────────────────────────────────────────
        TextButton(
            onClick  = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                "Discard changes",
                fontSize  = 14.sp,
                color     = TextSecondary
            )
        }
    }
}

// ─── Reusable card wrapper ────────────────────────────────────────────────────
@Composable
private fun EditSectionCard(content: @Composable ColumnScope.() -> Unit) {
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

// ─── Section label ────────────────────────────────────────────────────────────
@Composable
private fun EditSectionLabel(text: String) {
    Text(
        text         = text.uppercase(),
        fontSize     = 11.sp,
        fontWeight   = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color        = TextSecondary
    )
}

// ─── Styled text field ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        placeholder   = if (placeholder.isNotEmpty()) {
            { Text(placeholder, fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) }
        } else null,
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
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderLight,
            focusedBorderColor   = AccentBlue,
            unfocusedLabelColor  = TextSecondary,
            focusedLabelColor    = AccentBlue,
            cursorColor          = AccentBlue
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(rememberNavController())
}