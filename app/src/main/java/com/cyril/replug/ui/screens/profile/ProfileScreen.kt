package com.cyril.replug.ui.screens.profile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.cyril.replug.navigation.*
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

// ─── Simple data class mirroring your Firebase user node ─────────────────────
data class UserProfile(
    val name: String  = "",
    val email: String = "",
    val phone: String = "",
    val location: String = ""
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    var selectedIndex by remember { mutableIntStateOf(4) }

    // ── Firebase state ───────────────────────────────────────────────────────
    val auth    = remember { FirebaseAuth.getInstance() }
    val fireUser = auth.currentUser

    var profile  by remember { mutableStateOf(UserProfile()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Pull user node from Realtime Database (adjust path to match your schema)
    LaunchedEffect(fireUser?.uid) {
        fireUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance()
                .getReference("users/$uid")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        profile = UserProfile(
                            name     = snapshot.child("name").getValue(String::class.java)
                                ?: fireUser.displayName ?: "No name",
                            email    = snapshot.child("email").getValue(String::class.java)
                                ?: fireUser.email ?: "No email",
                            phone    = snapshot.child("phone").getValue(String::class.java) ?: "",
                            location = snapshot.child("location").getValue(String::class.java) ?: ""
                        )
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        containerColor = PageBg,

        // ── Bottom nav ───────────────────────────────────────────────────────
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, BorderLight, RoundedCornerShape(0.dp))
            ) {
                data class NavItem(val label: String, val icon: ImageVector, val route: String, val index: Int)
                val items = listOf(
                    NavItem("Home",     Icons.Rounded.Home,            ROUTE_HOME,    0),
                    NavItem("Search",   Icons.Rounded.Search,          ROUTE_SEARCH,  1),
                    NavItem("Add",      Icons.Rounded.Add,             ROUTE_ADD,     2),
                    NavItem("Inbox",    Icons.Rounded.Inbox,          ROUTE_CHAT_INBOX, 3),
                    NavItem("Profile",  Icons.Rounded.Person,          ROUTE_PROFILE, 4),
                )
                items.forEach { item ->
                    val isSelected = selectedIndex == item.index
                    NavigationBarItem(
                        icon = {
                            if (item.index == 2) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(CircleShape).background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(item.icon, contentDescription = item.label, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp))
                            }
                        },
                        label = {
                            Text(item.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        },
                        selected = isSelected,
                        onClick = { selectedIndex = item.index; navController.navigate(item.route) },
                        colors = NavigationBarItemDefaults.colors(
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Hero header ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(top = 48.dp, bottom = 52.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(targetState = imageUri, label = "avatar") { uri ->
                            if (uri != null) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Profile photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else if (fireUser?.photoUrl != null) {
                                AsyncImage(
                                    model = fireUser.photoUrl,
                                    contentDescription = "Profile photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                // Initials fallback
                                val initials = profile.name
                                    .split(" ")
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                    .take(2)
                                    .joinToString("")
                                    .ifEmpty { "?" }
                                Text(
                                    text = initials,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Camera badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                                .border(2.dp, SurfaceDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = profile.name.ifEmpty { "Loading…" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = profile.email.ifEmpty { "" },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            // ── Stats row ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-1).dp)
                    .background(Color.White)
                    .border(bottom = 1.dp, color = BorderLight),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "0", label = "Listings")
                StatDivider()
                StatItem(value = "0", label = "Sold")
                StatDivider()
                StatItem(value = "0", label = "Recycled")
            }

            Spacer(Modifier.height(16.dp))

            // ── Info card ────────────────────────────────────────────────────
            ProfileSectionCard {
                ProfileSectionLabel("Account info")
                Spacer(Modifier.height(4.dp))

                ProfileInfoRow(
                    icon = Icons.Rounded.Person,
                    label = "Full name",
                    value = profile.name.ifEmpty { "—" }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileInfoRow(
                    icon = Icons.Rounded.Email,
                    label = "Email",
                    value = profile.email.ifEmpty { "—" }
                )
                if (profile.phone.isNotEmpty()) {
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    ProfileInfoRow(
                        icon = Icons.Rounded.Phone,
                        label = "Phone",
                        value = profile.phone
                    )
                }
                if (profile.location.isNotEmpty()) {
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    ProfileInfoRow(
                        icon = Icons.Rounded.LocationOn,
                        label = "Location",
                        value = profile.location
                    )
                }
            }

            // ── Actions card ─────────────────────────────────────────────────
            ProfileSectionCard {
                ProfileSectionLabel("Settings")
                Spacer(Modifier.height(4.dp))

                ProfileActionRow(
                    icon  = Icons.Rounded.Edit,
                    label = "Edit profile",
                    onClick = { navController.navigate(ROUTE_EPROFILE) }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileActionRow(
                    icon  = Icons.Rounded.Notifications,
                    label = "Notifications",
                    onClick = { navController.navigate(ROUTE_NOTIFICATION) }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileActionRow(
                    icon  = Icons.Rounded.Lock,
                    label = "Privacy & security"
                )
            }

            ProfileSectionCard {
                ProfileSectionLabel("My History")
                Spacer(Modifier.height(4.dp))

                ProfileActionRow(
                    icon  = Icons.Rounded.Inventory2,
                    label = "My listings",
                    onClick = { navController.navigate(ROUTE_MY_LISTINGS) }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileActionRow(
                    icon  = Icons.Rounded.Receipt,
                    label = "My Order History",
                    onClick = { navController.navigate(ROUTE_ORDERS) }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileActionRow(
                    icon  = Icons.Rounded.Recycling,
                    label = "My Recycled History",
                    onClick = { navController.navigate(ROUTE_RECYCLED_PRODUCTS) }
                )
            }
            ProfileSectionCard {
                ProfileSectionLabel("More")
                Spacer(Modifier.height(4.dp))

                ProfileActionRow(
                    icon  = Icons.Rounded.Info,
                    label = "About Us",
                    onClick = { navController.navigate(ROUTE_ABOUT) }
                )
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                ProfileActionRow(
                    icon  = Icons.Rounded.Call,
                    label = "Contact Us",
                    onClick = { navController.navigate(ROUTE_CONTACT) }
                )
            }

            // ── Sign-out ─────────────────────────────────────────────────────
            ProfileSectionCard {
                ProfileActionRow(
                    icon       = Icons.Rounded.Logout,
                    label      = "Sign out",
                    labelColor = Color(0xFFDC2626),
                    iconColor  = Color(0xFFDC2626),
                    showChevron = false,
                    onClick = {
                        auth.signOut()
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun RowScope.StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(BorderLight)
            .align(Alignment.CenterVertically)
    )
}

@Composable
private fun ProfileSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), content = content)
    }
}

@Composable
private fun ProfileSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    labelColor: Color = TextPrimary,
    iconColor: Color = AccentBlue,
    showChevron: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        if (showChevron) {
            Icon(
                Icons.Rounded.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ─── Helper to add a bottom border to a Row ───────────────────────────────────
private fun Modifier.border(bottom: Dp, color: Color) = drawBehind {
    val thickness = bottom.toPx()
    val y = size.height - thickness / 2
    drawLine(
        color = color,
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = thickness
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}