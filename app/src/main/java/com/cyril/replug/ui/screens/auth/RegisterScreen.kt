package com.cyril.replug.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
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
import com.cyril.replug.navigation.ROUTE_LOGIN

// ─── Colour tokens (shared) ───────────────────────────────────────────────────
private val SurfaceDark       = Color(0xFF0F1923)
private val PageBg            = Color(0xFFF0F2F5)
private val BorderLight       = Color(0xFFE2E6EC)
private val TextPrimary       = Color(0xFF111827)
private val TextSecondary     = Color(0xFF6B7280)
private val AccentBlue        = Color(0xFF1A6BF5)
private val AccentBlueSurface = Color(0xFFEFF4FF)
private val ErrorRed          = Color(0xFFEF4444)

@Composable
fun RegisterScreen(navController: NavController) {

    val context  = LocalContext.current
    val auth     = com.google.firebase.auth.FirebaseAuth.getInstance()
    val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference

    var username        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword    by remember { mutableStateOf(false) }
    var showConfirm     by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    // ── Inline validation states ──────────────────────────────────────────────
    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

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

            Spacer(Modifier.height(60.dp))

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
                    painter            = painterResource(R.drawable.register),
                    contentDescription = "Register",
                    modifier           = Modifier.size(52.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Heading ───────────────────────────────────────────────────────
            Text(
                text          = "Create your account",
                fontSize      = 26.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextPrimary,
                textAlign     = TextAlign.Center,
                letterSpacing = (-0.5).sp,
                lineHeight    = 32.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text      = "Join us and start your journey today",
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

                // Username
                AuthTextField(
                    value         = username,
                    onValueChange = { username = it },
                    label         = "Username",
                    leadingIcon   = Icons.Rounded.Person,
                    keyboardType  = KeyboardType.Text
                )

                // Email
                AuthTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = "Email address",
                    leadingIcon   = Icons.Rounded.Email,
                    keyboardType  = KeyboardType.Email
                )

                // Password
                AuthTextField(
                    value               = password,
                    onValueChange       = { password = it },
                    label               = "Password",
                    leadingIcon         = Icons.Rounded.Lock,
                    keyboardType        = KeyboardType.Password,
                    isPassword          = true,
                    showPassword        = showPassword,
                    onTogglePassword    = { showPassword = !showPassword }
                )

                // Confirm password
                AuthTextField(
                    value               = confirmPassword,
                    onValueChange       = { confirmPassword = it },
                    label               = "Confirm password",
                    leadingIcon         = Icons.Rounded.Lock,
                    keyboardType        = KeyboardType.Password,
                    isPassword          = true,
                    showPassword        = showConfirm,
                    onTogglePassword    = { showConfirm = !showConfirm },
                    isError             = passwordMismatch,
                    errorMessage        = "Passwords do not match"
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Register button ───────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        username.isEmpty() || email.isEmpty() ||
                                password.isEmpty() || confirmPassword.isEmpty() -> {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isLoading = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid
                                        val userMap = mapOf(
                                            "username" to username,
                                            "email"    to email,
                                            "uid"      to userId
                                        )
                                        if (userId != null) {
                                            database.child("Users").child(userId)
                                                .setValue(userMap)
                                                .addOnCompleteListener {
                                                    isLoading = false
                                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                                    navController.navigate(ROUTE_HOME)
                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            task.exception?.message ?: "Registration failed",
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
                        "Create Account",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Login link ────────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Already have an account? ",
                    fontSize = 14.sp,
                    color    = TextSecondary
                )
                TextButton(
                    onClick      = { navController.navigate(ROUTE_LOGIN) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Sign in",
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

// ─── Reusable auth text field ─────────────────────────────────────────────────
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = {
                Text(label, fontSize = 13.sp)
            },
            leadingIcon   = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint     = if (isError) ErrorRed else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon  = if (isPassword) ({
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector        = if (showPassword) Icons.Rounded.VisibilityOff
                        else Icons.Rounded.Visibility,
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint               = TextSecondary,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }) else null,
            visualTransformation = if (isPassword && !showPassword)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError         = isError,
            singleLine      = true,
            shape           = RoundedCornerShape(14.dp),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = AccentBlue,
                unfocusedBorderColor    = BorderLight,
                errorBorderColor        = ErrorRed,
                focusedLabelColor       = AccentBlue,
                unfocusedLabelColor     = TextSecondary,
                focusedLeadingIconColor = AccentBlue,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor     = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                fontSize = 12.sp,
                color    = ErrorRed,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(rememberNavController())
}