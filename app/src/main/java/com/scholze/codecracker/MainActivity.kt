package com.scholze.codecracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import com.scholze.codecracker.ui.components.LoginPage
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.scholze.codecracker.ui.components.CreateAccount
import com.scholze.codecracker.ui.theme.CodecrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            CodecrackerTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "login") {
                    composable("login") { LoginPage(navController) }
                    composable("createAccount") { CreateAccount(navController) }
                }
            }
        }
    }
}