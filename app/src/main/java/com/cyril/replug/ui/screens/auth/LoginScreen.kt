package com.cyril.replug.ui.screens.auth

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.ROUTE_HOME
import com.cyril.replug.navigation.ROUTE_REGISTER

// ─── Colour tokens (shared) ───────────────────────────────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController) {

    val context  = LocalContext.current
    val auth     = com.google.firebase.auth.FirebaseAuth.getInstance()
    val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference

    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading    by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(72.dp))

            // ── Logo badge ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AccentBlueSurface)
                    .border(1.dp, BorderLight, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(R.drawable.login),
                    contentDescription = "Login",
                    modifier           = Modifier.size(52.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Heading ───────────────────────────────────────────────────────
            Text(
                text          = "Welcome back",
                fontSize      = 26.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextPrimary,
                textAlign     = TextAlign.Center,
                letterSpacing = (-0.5).sp,
                lineHeight    = 32.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text      = "Sign in to continue to RePlug",
                fontSize  = 14.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // ── Form card ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Email field
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Email address", fontSize = 13.sp) },
                    leadingIcon   = {
                        Icon(
                            Icons.Rounded.Email,
                            contentDescription = null,
                            tint     = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine      = true,
                    shape           = RoundedCornerShape(14.dp),
                    colors          = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AccentBlue,
                        unfocusedBorderColor    = BorderLight,
                        focusedLabelColor       = AccentBlue,
                        unfocusedLabelColor     = TextSecondary,
                        focusedLeadingIconColor = AccentBlue,
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password field
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it },
                    label         = { Text("Password", fontSize = 13.sp) },
                    leadingIcon   = {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = null,
                            tint     = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon  = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector        = if (showPassword) Icons.Rounded.VisibilityOff
                                else Icons.Rounded.Visibility,
                                contentDescription = if (showPassword) "Hide password"
                                else "Show password",
                                tint     = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine      = true,
                    shape           = RoundedCornerShape(14.dp),
                    colors          = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AccentBlue,
                        unfocusedBorderColor    = BorderLight,
                        focusedLabelColor       = AccentBlue,
                        unfocusedLabelColor     = TextSecondary,
                        focusedLeadingIconColor = AccentBlue,
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Forgot password
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(
                        onClick        = { /* TODO: forgot password */ },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Forgot password?",
                            fontSize   = 13.sp,
                            color      = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Login button ──────────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        email.isEmpty() || password.isEmpty() -> {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid
                                        if (userId != null) {
                                            database.child("Users").child(userId)
                                                .get()
                                                .addOnSuccessListener { snapshot ->
                                                    isLoading = false
                                                    val username = snapshot.child("username").value.toString()
                                                    Toast.makeText(context, "Welcome $username!", Toast.LENGTH_SHORT).show()
                                                    navController.navigate(ROUTE_HOME)
                                                }
                                                .addOnFailureListener {
                                                    isLoading = false
                                                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            task.exception?.message ?: "Login failed",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }
                },
                enabled  = !isLoading,
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark,
                    contentColor   = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Sign In",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Register link ─────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Don't have an account? ",
                    fontSize = 14.sp,
                    color    = TextSecondary
                )
                TextButton(
                    onClick        = { navController.navigate(ROUTE_REGISTER) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Register",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = AccentBlue
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}