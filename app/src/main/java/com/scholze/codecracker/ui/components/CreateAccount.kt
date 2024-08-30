package com.scholze.codecracker.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreateAccount(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.padding(16.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                //Try to Create Account
                if (password.value.length >= 6 && email.value.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                db.collection("language").get()
                                    .addOnSuccessListener { documents ->
                                        val scores = mutableMapOf<String, Int>()
                                        // Create Scores for the new user
                                        for (document in documents) {
                                            val languageName = document.getString("Name") ?: ""
                                            if (languageName.isNotEmpty()) {
                                                scores[languageName] = 0
                                            }
                                        }

                                        val user = mapOf(
                                            "scores" to scores
                                        )
                                        //Add scores for new user
                                        db.collection("user").document(email.value)
                                            .set(user)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Failed to create user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to retrieve languages: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "Account Creation Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Invalid Fields", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = "Create Account")
        }
    }
}
