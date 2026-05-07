package com.cyril.replug.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cyril.replug.models.Product
import com.cyril.replug.navigation.ROUTE_SEARCH
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ProductViewModel : ViewModel() {

    // ── Cloudinary config ──────────────────────────────────────────────────────
    // Replace these with your actual Cloudinary values from the dashboard
    private val CLOUD_NAME = "dcmvkp3hl"
    private val UPLOAD_PRESET = "replug_upload"

    private val _products = mutableStateListOf<Product>()
    val products: List<Product> = _products

    // 🔥 UPLOAD PRODUCT
    fun uploadProduct(
        imageUri: Uri?,
        name: String,
        category: String,
        brand: String,
        price: String,
        description: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (imageUri == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                // ✅ Upload image to Cloudinary (free, no Firebase Storage needed)
                val imageUrl = uploadImageToCloudinary(imageUri, context)

                val ref = FirebaseDatabase.getInstance()
                    .getReference("Products")
                    .push()

                val productData = mapOf(
                    "id"          to ref.key,
                    "name"        to name,
                    "category"    to category,
                    "brand"       to brand,
                    "price"       to price,
                    "description" to description,
                    "imageUrl"    to imageUrl   // Cloudinary secure URL
                )

                ref.setValue(productData).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Product saved successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_SEARCH)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Shows the real error so you can debug easily
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ☁️ CLOUDINARY IMAGE UPLOAD (replaces Firebase Storage)
    private suspend fun uploadImageToCloudinary(uri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot read image — URI may have expired")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "product_${System.currentTimeMillis()}.jpg",
                    bytes.toRequestBody("image/jpeg".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
                ?: throw Exception("Empty response from Cloudinary")

            if (!response.isSuccessful) {
                throw Exception("Cloudinary upload failed: $responseBody")
            }

            // Parse and return the hosted image URL
            JSONObject(responseBody).getString("secure_url")
        }
    }

    // 🔥 REAL-TIME FETCH
    fun fetchProducts(context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("Products")

        ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {

            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                _products.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    product?.let {
                        it.id = child.key
                        _products.add(it)
                    }
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_LONG).show()
            }
        })
    }

    // 🔥 DELETE PRODUCT
    fun deleteProduct(productId: String, context: Context) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("Products")
            .child(productId)

        ref.removeValue()
            .addOnSuccessListener {
                _products.removeAll { it.id == productId }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Product not deleted", Toast.LENGTH_LONG).show()
            }
    }

    // 🔥 UPDATE PRODUCT
    fun updateProduct(
        productId: String,
        imageUri: Uri?,
        name: String,
        category: String,
        brand: String,
        price: String,
        description: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updateMap = mutableMapOf<String, Any>(
                    "id"          to productId,
                    "name"        to name,
                    "category"    to category,
                    "brand"       to brand,
                    "price"       to price,
                    "description" to description
                )

                // ✅ Only re-upload if user picked a new image
                if (imageUri != null) {
                    val imageUrl = uploadImageToCloudinary(imageUri, context)
                    updateMap["imageUrl"] = imageUrl
                }

                FirebaseDatabase.getInstance()
                    .getReference("Products")
                    .child(productId)
                    .setValue(updateMap)
                    .await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_SEARCH)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}