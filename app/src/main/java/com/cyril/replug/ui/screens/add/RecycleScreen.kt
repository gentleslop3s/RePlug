package com.cyril.replug.ui.screens.add

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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.R
import com.cyril.replug.ui.theme.mainBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RecycleScreen(navController: NavController){

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("RecyclingRequests")

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var deviceName by remember{ mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var collectionMethod by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris
    }

    // ✅ FUNCTION TO SAVE DATA (Defined here so it's visible to the UI components below)
    fun saveData() {
        val userId = auth.currentUser?.uid ?: "unknown"

        val requestId = database.push().key!!

        val data = mapOf(
            "id" to requestId,
            "userId" to userId,
            "deviceName" to deviceName,
            "condition" to condition,
            "action" to action,
            "collectionMethod" to collectionMethod,
            "imageUri" to (imageUris.firstOrNull()?.toString() ?: "")
        )

        database.child(requestId).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Request submitted successfully", Toast.LENGTH_LONG).show()
                navController.popBackStack() // Optional: go back after success
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to submit", Toast.LENGTH_LONG).show()
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

        Text(
            text = "Recycle Your Device ♻️",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Give your old electronics a second life or dispose of them safely.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .clickable {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent<Uri?>(
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

            if (imageUris.isEmpty()) {
                Text("Upload device image")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = deviceName,
            onValueChange = { deviceName = it },
            label = { Text("Device (e.g. iPhone 6s)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue,
                unfocusedLeadingIconColor = mainBlue,
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text("Condition")

        Row {
            listOf("Working", "Damaged", "Dead").forEach { cond ->

                val isSelected = condition == cond

                AssistChip(
                    onClick = { condition = cond },
                    label = { Text(
                        cond,
                        color = if (isSelected) Color.White else Color.Black) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (condition == cond) mainBlue else Color.LightGray,
                        labelColor = if (condition == cond) Color.White else Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("What do you want to do?")

        Column {
            listOf(
                "Donate",
                "Recycle safely",
                "Sell for parts"
            ).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = action == item, onClick = { action = item })
                    Text(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Collection Method")

        Row {
            listOf("Pickup", "Drop-off").forEach { method ->

                val isSelected = collectionMethod == method

                AssistChip(
                    onClick = { collectionMethod = method },
                    label = { Text(
                        method,
                        color = if (isSelected) Color.White else Color.Black) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (condition == method) mainBlue else Color.LightGray,
                        labelColor = if (condition == method) Color.White else Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // 🔴 Validation
                if (deviceName.isEmpty() || condition.isEmpty() || action.isEmpty() || collectionMethod.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // ✅ Anonymous login (if not logged in)
                if (auth.currentUser == null) {
                    auth.signInAnonymously()
                        .addOnSuccessListener {
                            saveData()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Auth failed", Toast.LENGTH_LONG).show()
                        }
                } else {
                    saveData()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mainBlue
            )
        ) {
            Text("Request Recycling")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecycleScreenPreview(){
    RecycleScreen(rememberNavController())
}
