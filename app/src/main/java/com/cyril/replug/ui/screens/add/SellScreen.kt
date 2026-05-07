package com.cyril.replug.ui.screens.add

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.cyril.replug.data.ProductViewModel
import com.cyril.replug.ui.theme.mainBlue

@Composable
fun SellScreen(navController: NavController) {

    val context = LocalContext.current

    // ✅ Same activity-scoped instance as HomeScreen — real-time list updates immediately
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

    // ✅ Persistent URI permission — prevents expiry on Android 10+
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                    // Some providers don't support persistable permissions — safe to ignore
                }
            }
            imageUris = uris
            Toast.makeText(context, "${uris.size} image(s) selected", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = imageUris.firstOrNull(),
                label = "Image Picker Animation"
            ) { targetUri ->
                AsyncImage(
                    model = targetUri ?: R.drawable.ic_launcher_background,
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Item Title") },
            modifier = Modifier.width(330.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text("Brand") },
            modifier = Modifier.width(330.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g. Phone, Laptop)") },
            modifier = Modifier.width(330.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Condition")

        Row {
            listOf("New", "Used", "Broken").forEach { text ->
                val isSelected = condition == text
                AssistChip(
                    onClick = { condition = text },
                    label = {
                        Text(text, color = if (isSelected) Color.White else Color.Black)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) mainBlue else Color.LightGray,
                        labelColor = if (isSelected) Color.White else Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (KES)") },
            leadingIcon = { Text("KSh") },
            modifier = Modifier.width(330.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.width(330.dp),
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (title.isEmpty() || price.isEmpty() || condition.isEmpty() || imageUris.isEmpty()) {
                    Toast.makeText(context, "Fill all fields & add an image", Toast.LENGTH_SHORT).show()
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
                .width(330.dp)
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
        ) {
            Text("Post Item")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SellScreenPreview() {
    SellScreen(rememberNavController())
}