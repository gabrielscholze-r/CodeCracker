package com.scholze.codecracker.data

import androidx.compose.ui.graphics.vector.ImageVector

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