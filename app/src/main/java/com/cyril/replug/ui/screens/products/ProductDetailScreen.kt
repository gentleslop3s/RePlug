package com.cyril.replug.ui.screens.products

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cyril.replug.models.Product
import com.cyril.replug.ui.screens.home.HomeScreen
import com.cyril.replug.ui.theme.mainBlue
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ProductDetailScreen(
    productId: String,
    onBuyClick: (Product) -> Unit = {}
) {

    val context = LocalContext.current
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 🔥 Fetch single product from Firebase
    LaunchedEffect(productId) {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .child(productId)
            .get()
            .addOnSuccessListener { snapshot ->
                product = snapshot.getValue(Product::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_LONG).show()
            }
    }

    // 🔄 Loading state
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // ❌ If product not found
    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Product not found")
        }
        return
    }

    // ✅ UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 15.dp, end = 15.dp )
    ) {

        // 🔥 Product Image
        AsyncImage(
            model = product!!.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 Product Name
        Text(
            text = product!!.name ?: "",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 Price
        Text(
            text = "Ksh ${product!!.price}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔥 Category + Brand
        Text(text = "Category: ${product!!.category ?: "N/A"}")
        Text(text = "Brand: ${product!!.brand ?: "N/A"}")

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 Description
        Text(
            text = product!!.description ?: "",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 🔥 BUY BUTTON
        Button(
            onClick = {
                onBuyClick(product!!)
            },
            modifier = Modifier
                .width(330.dp)
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
        ) {
            Text("Buy Now")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview(){
}