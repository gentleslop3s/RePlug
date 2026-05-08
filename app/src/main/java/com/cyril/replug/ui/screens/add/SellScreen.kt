package com.cyril.replug.ui.screens.add

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.cyril.replug.data.ProductViewModel
import com.cyril.replug.ui.theme.mainBlue

// ─── Colour tokens ───────────────────────────────────────────────────────────
private val SurfaceDark   = Color(0xFF0F1923)   // header background
private val SurfaceCard   = Color(0xFFF7F8FA)   // input card background
private val AccentBlue    = Color(0xFF1A6BF5)   // CTA / selected chip
private val BorderLight   = Color(0xFFE2E6EC)
private val TextPrimary   = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val ChipUnselected= Color(0xFFEEF0F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(navController: NavController) {

    val context = LocalContext.current

    val viewModel: ProductViewModel = viewModel(
        viewModelStoreOwner = context as androidx.activity.ComponentActivity
    )

    var title       by remember { mutableStateOf("") }
    var price       by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("") }
    var brand       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var condition   by remember { mutableStateOf("") }
    var imageUris   by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}
            }
            imageUris = uris
            Toast.makeText(context, "${uris.size} photo(s) added", Toast.LENGTH_SHORT).show()
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
            // Back button
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
                Text(
                    text = "List an item",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Reach buyers instantly",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        }

        // ── Photo picker card ────────────────────────────────────────────────
        SectionCard(modifier = Modifier.padding(top = 16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SectionLabel("Photos")
                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEEF0F4))
                        .border(
                            width = 1.5.dp,
                            color = if (imageUris.isEmpty()) BorderLight else AccentBlue.copy(alpha = 0.4f),
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
                                contentDescription = "Product photo",
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
                                    tint = AccentBlue,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Tap to add photos",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    "JPG or PNG · up to 10 images",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Photo count badge
                AnimatedVisibility(
                    visible = imageUris.isNotEmpty(),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "${imageUris.size} photo(s) selected",
                            fontSize = 13.sp,
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ── Item details card ────────────────────────────────────────────────
        SectionCard {
            SectionLabel("Item details")
            Spacer(Modifier.height(14.dp))

            SellTextField(value = title, onValueChange = { title = it }, label = "Item title")
            Spacer(Modifier.height(12.dp))
            SellTextField(value = brand, onValueChange = { brand = it }, label = "Brand")
            Spacer(Modifier.height(12.dp))
            SellTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category",
                placeholder = "e.g. Phone, Laptop, Console"
            )
        }

        // ── Condition card ───────────────────────────────────────────────────
        SectionCard {
            SectionLabel("Condition")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConditionOption.entries.forEach { opt ->
                    val selected = condition == opt.label
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) AccentBlue else ChipUnselected)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = if (selected) Color.Transparent else BorderLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { condition = opt.label }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = opt.emoji,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = opt.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = opt.hint,
                            fontSize = 11.sp,
                            color = if (selected) Color.White.copy(alpha = 0.75f) else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // ── Pricing card ─────────────────────────────────────────────────────
        SectionCard {
            SectionLabel("Pricing")
            Spacer(Modifier.height(14.dp))
            SellTextField(
                value = price,
                onValueChange = { price = it },
                label = "Asking price",
                prefix = "KSh",
                keyboardType = KeyboardType.Number
            )
        }

        // ── Description card ─────────────────────────────────────────────────
        SectionCard {
            SectionLabel("Description")
            Spacer(Modifier.height(14.dp))
            SellTextField(
                value = description,
                onValueChange = { description = it },
                label = "Describe your item",
                placeholder = "Condition details, accessories included, reason for selling…",
                minLines = 4
            )
        }

        // ── CTA ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = {
                    if (title.isEmpty() || price.isEmpty() || condition.isEmpty() || imageUris.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please fill all fields and add a photo",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    viewModel.uploadProduct(
                        imageUri      = imageUris.first(),
                        name          = title,
                        category      = category,
                        brand         = brand,
                        price         = price,
                        description   = description,
                        context       = context,
                        navController = navController
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "Post listing",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ─── Condition options ────────────────────────────────────────────────────────
private enum class ConditionOption(val label: String, val emoji: String, val hint: String) {
    New("New",    "✨", "Sealed / unused"),
    Used("Used",  "👌", "Good condition"),
    Broken("Broken","🔧", "For parts / repair")
}

// ─── Reusable card wrapper ────────────────────────────────────────────────────
@Composable
private fun SectionCard(
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
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = TextSecondary
    )
}

// ─── Styled text field ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    prefix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.7f)) }
        } else null,
        leadingIcon = if (prefix != null) {
            {
                Text(
                    prefix,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentBlue
                )
            }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
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
fun SellScreenPreview() {
    SellScreen(rememberNavController())
}