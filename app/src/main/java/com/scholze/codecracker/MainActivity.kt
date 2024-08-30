package com.scholze.codecracker

import TriviaPage
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.scholze.codecracker.ui.components.LoginPage
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.scholze.codecracker.data.LanguageTrivia
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
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginPage(navController)
                    }
                    composable(
                        route = "profile/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        var selected = 1
                        Profile(navController, selected) { selected = it }
                    }
                    composable("createAccount") {
                        CreateAccount(navController)
                    }
                    composable(
                        route = "home/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        var selected = 0
                        HomePage(navController, selected) { selected = it }
                    }
                    composable(
                        route = "language/{triviaJson}",
                        arguments = listOf(
                            navArgument("triviaJson") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val triviaJson = Uri.decode(backStackEntry.arguments?.getString("triviaJson"))  // Decodifica a string JSON
                        val trivia = Gson().fromJson(triviaJson, LanguageTrivia::class.java)  // Desserializa para o objeto

                        TriviaPage(navController, trivia)
                    }
                }

            }
        }
    }
}