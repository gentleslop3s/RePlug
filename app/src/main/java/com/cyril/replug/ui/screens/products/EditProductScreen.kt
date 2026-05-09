package com.cyril.replug.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cyril.replug.data.ProductViewModel

// ─── Colour tokens (matches MyListingsScreen) ─────────────────────────────────
private val ES_SurfaceDark   = Color(0xFF0F1923)
private val ES_PageBg        = Color(0xFFF0F2F5)
private val ES_BorderLight   = Color(0xFFE2E6EC)
private val ES_TextPrimary   = Color(0xFF111827)
private val ES_TextSecondary = Color(0xFF6B7280)
private val ES_AccentBlue    = Color(0xFF1A6BF5)
private val ES_FieldBg       = Color(0xFFF8F9FB)
private val ES_ErrorRed      = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productId    : String?
) {
    val context   = LocalContext.current
    val viewModel : ProductViewModel = viewModel(
        viewModelStoreOwner = context as androidx.activity.ComponentActivity
    )

    // ── Find the product ───────────────────────────────────────────────────
    val product = remember(productId, viewModel.products) {
        viewModel.products.firstOrNull { it.id == productId }
    }

    // ── Form state pre-filled from product ────────────────────────────────
    var name        by remember(product) { mutableStateOf(product?.name        ?: "") }
    var category    by remember(product) { mutableStateOf(product?.category    ?: "") }
    var brand       by remember(product) { mutableStateOf(product?.brand       ?: "") }
    var price       by remember(product) { mutableStateOf(product?.price       ?: "") }
    var description by remember(product) { mutableStateOf(product?.description ?: "") }

    // ── New image picked by user (null = keep existing) ───────────────────
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) newImageUri = uri }

    // ── Validation ─────────────────────────────────────────────────────────
    val priceError = price.isNotBlank() && price.toDoubleOrNull() == null
    val canSave    = name.isNotBlank() && price.isNotBlank() && !priceError

    // ── Saving state ───────────────────────────────────────────────────────
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ES_PageBg,

        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Edit Listing",
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = ES_TextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        AnimatedVisibility(
                            visible = product != null,
                            enter   = fadeIn(),
                            exit    = fadeOut()
                        ) {
                            Text(
                                product?.name ?: "",
                                fontSize = 12.sp,
                                color    = ES_TextSecondary,
                                maxLines = 1
                            )
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
                                .border(1.dp, ES_BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint     = ES_TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ES_PageBg)
            )
        },

        bottomBar = {
            if (product != null) {
                Surface(
                    color          = ES_PageBg,
                    tonalElevation = 0.dp,
                    modifier       = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!canSave || isSaving) return@Button
                                isSaving = true
                                product.id?.let { id ->
                                    viewModel.updateProduct(
                                        productId     = id,
                                        imageUri      = newImageUri,
                                        name          = name.trim(),
                                        category      = category.trim(),
                                        brand         = brand.trim(),
                                        price         = price.trim(),
                                        description   = description.trim(),
                                        context       = context,
                                        navController = navController
                                    )
                                }
                            },
                            enabled   = canSave && !isSaving,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape     = RoundedCornerShape(14.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor         = ES_SurfaceDark,
                                contentColor           = Color.White,
                                disabledContainerColor = ES_SurfaceDark.copy(alpha = 0.4f),
                                disabledContentColor   = Color.White.copy(alpha = 0.6f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(18.dp),
                                    color       = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Saving…", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Save changes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        // ── Product not found ──────────────────────────────────────────────
        if (product == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.SearchOff,
                        contentDescription = null,
                        tint     = ES_ErrorRed,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Listing not found",
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = ES_TextPrimary,
                    letterSpacing = (-0.4).sp,
                    textAlign     = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "This listing may have been deleted\nor is no longer available.",
                    fontSize   = 14.sp,
                    color      = ES_TextSecondary,
                    textAlign  = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick   = { navController.popBackStack() },
                    modifier  = Modifier.fillMaxWidth().height(52.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor = ES_SurfaceDark,
                        contentColor   = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Go back", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            return@Scaffold
        }

        // ── Edit form ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Image picker ───────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                border    = androidx.compose.foundation.BorderStroke(1.dp, ES_BorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Product image",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = ES_TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            newImageUri != null -> {
                                AsyncImage(
                                    model              = newImageUri,
                                    contentDescription = "New image",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                                ChangeBadge(Modifier.align(Alignment.BottomEnd))
                            }
                            !product.imageUrl.isNullOrEmpty() -> {
                                AsyncImage(
                                    model              = product.imageUrl,
                                    contentDescription = product.name,
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                                ChangeBadge(Modifier.align(Alignment.BottomEnd))
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFE8ECF4), Color(0xFFCDD5E8))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint     = ES_AccentBlue,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text("Tap to add image", fontSize = 13.sp, color = ES_TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            EditField(
                label         = "Product name",
                value         = name,
                onValueChange = { name = it },
                placeholder   = "e.g. iPhone 13 Pro",
                leadingIcon   = Icons.Rounded.Label
            )
            EditField(
                label         = "Brand",
                value         = brand,
                onValueChange = { brand = it },
                placeholder   = "e.g. Apple",
                leadingIcon   = Icons.Rounded.Storefront
            )
            EditField(
                label         = "Price (Ksh)",
                value         = price,
                onValueChange = { price = it },
                placeholder   = "e.g. 45000",
                keyboardType  = KeyboardType.Number,
                isError       = priceError,
                errorText     = "Enter a valid number",
                leadingIcon   = Icons.Rounded.AttachMoney
            )
            EditField(
                label         = "Category",
                value         = category,
                onValueChange = { category = it },
                placeholder   = "e.g. Electronics",
                leadingIcon   = Icons.Rounded.Category
            )
            EditField(
                label         = "Description",
                value         = description,
                onValueChange = { description = it },
                placeholder   = "Describe your item…",
                singleLine    = false,
                minLines      = 4,
                leadingIcon   = Icons.Rounded.Notes
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── "Tap to change" overlay badge ────────────────────────────────────────────
@Composable
private fun ChangeBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F1923).copy(alpha = 0.75f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text("Tap to change", fontSize = 11.sp, color = Color.White)
    }
}

// ─── Reusable labelled text field ─────────────────────────────────────────────
@Composable
private fun EditField(
    label         : String,
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String       = "",
    keyboardType  : KeyboardType = KeyboardType.Text,
    isError       : Boolean      = false,
    errorText     : String       = "",
    singleLine    : Boolean      = true,
    minLines      : Int          = 1,
    leadingIcon   : ImageVector
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text       = label,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = ES_TextPrimary
        )
        OutlinedTextField(
            value           = value,
            onValueChange   = onValueChange,
            placeholder     = { Text(placeholder, color = ES_TextSecondary, fontSize = 14.sp) },
            leadingIcon     = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint     = if (isError) ES_ErrorRed else ES_AccentBlue,
                    modifier = Modifier.size(18.dp)
                )
            },
            isError         = isError,
            singleLine      = singleLine,
            minLines        = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape           = RoundedCornerShape(12.dp),
            colors          = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = ES_BorderLight,
                focusedBorderColor      = ES_AccentBlue,
                errorBorderColor        = ES_ErrorRed,
                unfocusedContainerColor = ES_FieldBg,
                focusedContainerColor   = Color.White,
                errorContainerColor     = Color(0xFFFEF2F2),
                cursorColor             = ES_AccentBlue,
                focusedTextColor        = ES_TextPrimary,
                unfocusedTextColor      = ES_TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = isError && errorText.isNotEmpty()) {
            Text(errorText, fontSize = 12.sp, color = ES_ErrorRed)
        }
    }
}