package com.cyril.replug.ui.screens.auth

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
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyril.replug.R
import com.cyril.replug.navigation.ROUTE_HOME
import com.cyril.replug.navigation.ROUTE_LOGIN
import com.cyril.replug.ui.theme.mainBlue

@Composable
fun RegisterScreen(navController: NavController){

    val context = LocalContext.current

    // Firebase instances
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference

    Column(

        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(com.cyril.replug.R.drawable.replugbgimg), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.register),
            contentDescription = "product",
            modifier = Modifier.size(150.dp)

        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Join Us and start your journey today",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        //Variables

        var username by remember{ mutableStateOf("") }
        var email by remember{ mutableStateOf("") }
        var password by remember{ mutableStateOf("") }
        var confirmpassword by remember{ mutableStateOf("") }


        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            label = { Text(text = "username")},
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = mainBlue,
                focusedBorderColor = mainBlue,
                unfocusedLeadingIconColor = mainBlue,

                )
        )

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
            onValueChange = { password = it },
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

        OutlinedTextField(
            value = confirmpassword,
            onValueChange = { confirmpassword = it },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            label = { Text(text = "confirm password")},
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "")},
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
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                    android.widget.Toast.makeText(context, "Fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (password != confirmpassword) {
                    android.widget.Toast.makeText(context, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Firebase Auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val userId = auth.currentUser?.uid

                            val userMap = mapOf(
                                "username" to username,
                                "email" to email,
                                "uid" to userId
                            )

                            // Save to Realtime Database
                            if (userId != null) {
                                database.child("Users").child(userId)
                                    .setValue(userMap)
                                    .addOnCompleteListener {

                                        android.widget.Toast.makeText(context, "Registration Successful", android.widget.Toast.LENGTH_SHORT).show()

                                        navController.navigate(ROUTE_HOME)
                                    }
                            }

                        } else {
                            android.widget.Toast.makeText(
                                context,
                                task.exception?.message ?: "Registration Failed",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            Modifier.width(width = 250.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mainBlue
            )
        ) {
            Text(text = "Register Here")
        }

        Spacer(modifier = Modifier.height(30.dp))

        TextButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
            Text(text = "Already have an account? Login")


        }













    }



}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview(){

    RegisterScreen(rememberNavController())
}