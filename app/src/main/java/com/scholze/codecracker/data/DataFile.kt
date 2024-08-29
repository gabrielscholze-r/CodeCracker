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

data class Option(
    val answer: Boolean,
    val option: String
)

data class Question(
    val question_number: Int,
    val question: String,
    val options: List<Option>,
)

data class LanguageTrivia(
    val name: String,
    val questions: List<Question>
)