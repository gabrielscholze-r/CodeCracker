package com.scholze.codecracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import com.scholze.codecracker.ui.components.LoginPage
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.scholze.codecracker.ui.components.CreateAccount
import com.scholze.codecracker.ui.components.HomePage
import com.scholze.codecracker.ui.components.Profile
import com.scholze.codecracker.ui.theme.CodecrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            CodecrackerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home/Hn7bcpQLoKT7hH3Ly4UptqAYKQr1") {
                    composable("login") {
                        LoginPage(navController)
                    }
                    composable(
                        route = "profile/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        Profile(navController)
                    }
                    composable("createAccount") {
                        CreateAccount(navController)
                    }
                    composable(
                        route = "home/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        HomePage(navController)
                    }
                }

            }
        }
    }
}