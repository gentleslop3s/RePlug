package com.cyril.replug.ui.screens.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.ROUTE_HOME
import com.cyril.replug.navigation.ROUTE_REGISTER
import com.cyril.replug.ui.theme.mainBlue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController){

    val context = androidx.compose.ui.platform.LocalContext.current

    // Firebase instances
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference

    Column(

        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.replugbgimg), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        var email by remember{ mutableStateOf("") }
        var password by remember{ mutableStateOf("") }


        Image(
            painter = painterResource(R.drawable.login),
            contentDescription = "product",
            modifier = Modifier.size(130.dp)

        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Login",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            label = { Text(text = "email address")},
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue,
                unfocusedLeadingIconColor = mainBlue,

                )
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            label = { Text(text = "password")},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue,
                unfocusedLeadingIconColor = mainBlue,
            ),
            visualTransformation = PasswordVisualTransformation()

        )

        Spacer(modifier = Modifier.height(30.dp))



        Button(
            onClick = {


                // Validation
                if (email.isEmpty() || password.isEmpty()) {
                    android.widget.Toast.makeText(context, "Fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Firebase Login
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val userId = auth.currentUser?.uid

                            if (userId != null) {

                                // Retrieve user data from Realtime DB
                                database.child("Users").child(userId)
                                    .get()
                                    .addOnSuccessListener { snapshot ->

                                        val username = snapshot.child("username").value.toString()
                                        val userEmail = snapshot.child("email").value.toString()

                                        android.widget.Toast.makeText(
                                            context,
                                            "Welcome $username",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()

                                        // Navigate to Home after success
                                        navController.navigate(ROUTE_HOME)

                                    }
                                    .addOnFailureListener {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Failed to load user data",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        } else {
                            android.widget.Toast.makeText(
                                context,
                                task.exception?.message ?: "Login Failed",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            },
            Modifier.width(width = 250.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mainBlue
            )
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(30.dp))

        TextButton(onClick = {navController.navigate(ROUTE_REGISTER)}) {
            Text(text = "Don't have an account? Register")
        }

        TextButton(onClick = {navController.navigate(ROUTE_HOME)}) {
            Text(text = "Go to Home")
        }



    }



}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){

    LoginScreen(rememberNavController())
}