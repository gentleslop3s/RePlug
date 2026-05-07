package com.cyril.replug.ui.screens.payment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cyril.replug.R
import com.cyril.replug.models.Product
import com.cyril.replug.ui.theme.mainBlue
import com.google.firebase.database.FirebaseDatabase

@Composable
fun PaymentScreen(productId: String, onPaymentDone: () -> Unit = {}) {

    val context = LocalContext.current

    var product by remember { mutableStateOf<Product?>(null) }
    var selectedMethod by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    // 🔥 Load product
    LaunchedEffect(productId) {
        FirebaseDatabase.getInstance()
            .getReference("Products")
            .child(productId)
            .get()
            .addOnSuccessListener {
                product = it.getValue(Product::class.java)
                loading = false
            }
            .addOnFailureListener {
                loading = false
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_LONG).show()
            }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.replugbgimg), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Text("Checkout", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text("Product: ${product!!.name}")
        Text("Price: Ksh ${product!!.price}")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Select Payment Method")

        Spacer(modifier = Modifier.height(10.dp))

        // 💳 Payment Options
        listOf("M-Pesa", "Visa / Card").forEach { method ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMethod == method,
                        onClick = { selectedMethod = method }
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = selectedMethod == method,
                    onClick = { selectedMethod = method }
                )
                Text(method, modifier = Modifier.padding(start = 5.dp))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                if (selectedMethod.isEmpty()) {
                    Toast.makeText(context, "Select payment method", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val orderRef = FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .push()

                val orderData = mapOf(
                    "orderId" to orderRef.key,
                    "productId" to productId,
                    "productName" to product!!.name,
                    "price" to product!!.price,
                    "paymentMethod" to selectedMethod,
                    "status" to "Pending"
                )

                orderRef.setValue(orderData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Order placed successfully", Toast.LENGTH_LONG).show()
                        onPaymentDone()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to place order", Toast.LENGTH_LONG).show()
                    }
            },
            modifier = Modifier
                .width(330.dp)
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
        ) {
            Text("Pay Now")
        }
    }
}