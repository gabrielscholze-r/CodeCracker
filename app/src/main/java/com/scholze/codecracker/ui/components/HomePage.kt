package com.scholze.codecracker.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.google.gson.Gson
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.scholze.codecracker.data.Question
import com.scholze.codecracker.data.BottomNavItem
import com.scholze.codecracker.data.LanguageTrivia

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavController, selected: Int, onSelectedChange: (Int) -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val score = remember { mutableStateOf<Map<String, Long>>(value = mapOf()) }
    val triviaList = remember { mutableStateListOf<LanguageTrivia>() }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(triviaList) {
        println("Iniciando fetchTriviaData")
        val data = fetchTriviaData()
        triviaList.addAll(data)
        println("Trivias carregadas: $triviaList")
        user?.email?.let { email ->
            firestore.collection("user")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        score.value=document.get("scores") as Map<String, Long>
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Profile", "Error fetching score: ", exception)
                }
        }

    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, bottomNavItem ->
                    NavigationBarItem(
                        selected = index == selected,
                        onClick = {
                            onSelectedChange(index)
                            navController.navigate(bottomNavItem.route + "/" + auth.currentUser?.uid)
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (bottomNavItem.badges != 0) {
                                        Badge {
                                            Text(
                                                text = bottomNavItem.badges.toString()
                                            )
                                        }
                                    } else if (bottomNavItem.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector =
                                    if (index == selected)
                                        bottomNavItem.selectedIcon
                                    else
                                        bottomNavItem.unselectedIcon,
                                    contentDescription = bottomNavItem.title
                                )
                            }
                        },
                        label = {
                            Text(text = bottomNavItem.title)
                        }
                    )
                }
            }
        },
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "CODECRACKER",

                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (triviaList.isEmpty()) {
                Text(
                    text = "Carregando trivias...",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Column (
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    triviaList.forEach { trivia ->
                        Button(
                            enabled = !(score.value[trivia.name]?.toInt()==trivia.questions.size),
                            onClick = {
                                val triviaJson = Gson().toJson(trivia)
                                val encodedJson = Uri.encode(triviaJson)
                                navController.navigate("language/$encodedJson")
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(60.dp)
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 8.dp)

                        ) {
                            Text(text = trivia.name)
                        }
                    }
                }

            }
        }

    }
}

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        route = "home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false,
        badges = 0,
    ),
    BottomNavItem(
        title = "Profile",
        route = "profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        hasNews = false,
        badges = 0,
    ),
)

suspend fun fetchTriviaData(): List<LanguageTrivia> {
    val languageCollection = FirebaseFirestore.getInstance().collection("language")
    val d = languageCollection.get().await()

    val triviaList = mutableListOf<LanguageTrivia>()
    for (i in d) {
        val name = i.getString("Name") ?: ""
        val text = i.getString("Questions") ?: ""
        val questionsType = object : TypeToken<List<Question>>() {}.type
        val questions: List<Question> = Gson().fromJson(text, questionsType)
        val languageTrivia = LanguageTrivia(name, questions)
        triviaList.add(languageTrivia)
    }
    return triviaList
}