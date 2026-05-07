package com.cyril.replug.data

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import com.cyril.replug.models.User
import com.cyril.replug.navigation.ROUTE_HOME
import com.cyril.replug.navigation.ROUTE_REGISTER
import com.cyril.replug.navigation.ROUTE_SELL


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(var navController: NavController, var context: Context){
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(username:String, email:String, password:String, confirmpassword:String){

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        } else if (password != confirmpassword) {
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){

                    val uid = mAuth.currentUser!!.uid

                    // DEFAULT role = "user"
                    val role = "user"

                    val userdata = User(
                        username = username,
                        email = email,
                        password = password,
                        uid = uid,
                        role = role
                    )

                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    regRef.setValue(userdata).addOnCompleteListener { result ->

                        if (result.isSuccessful){
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "${result.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_REGISTER)
                        }

                    }

                } else {
                    navController.navigate(ROUTE_REGISTER)
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid

                    // Read the user type from Firebase
                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value?.toString() ?: "user"

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

                        if (role == "admin") {
                            navController.navigate(ROUTE_SELL)   // <-- change to your actual route
                        }


                        else {
                            navController.navigate(ROUTE_HOME)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_HOME)
    }


    fun isLoggedIn(): Boolean = mAuth.currentUser != null

}