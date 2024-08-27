package com.scholze.codecracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavController, selected: Int, onSelectedChange: (Int) -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val triviaList = remember { mutableStateListOf<LanguageTrivia>() }
    LaunchedEffect(triviaList) {
        println("Iniciando fetchTriviaData")
        val data = fetchTriviaData()
        triviaList.addAll(data)
        println("Trivias carregadas: $triviaList")
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
        if (triviaList.isEmpty()) {
            Text(
                text = "Carregando trivias...",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            triviaList.forEach { trivia ->
                Button(
                    onClick = { println(trivia.questions) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = trivia.name)
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

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badges: Int
)
data class Question(
    val question_number: Int,
    val question: String,
    val a1: String,
    val a2: String,
    val a3: String,
    val a4: String,
    val answer: String
)

data class LanguageTrivia(
    val name: String,
    val questions: List<Question>
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