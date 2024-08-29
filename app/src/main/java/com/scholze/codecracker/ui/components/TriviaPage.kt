package com.scholze.codecracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.scholze.codecracker.data.LanguageTrivia
import com.scholze.codecracker.data.BottomNavItem
import com.scholze.codecracker.data.Question
@Composable
fun TriviaPage(navController: NavController, language: LanguageTrivia) {
    val question = "Qual a capital da França?"
    val options = listOf("Paris", "Londres", "Berlim", "Roma")
    val correctAnswer = "Paris"

    // Estado para armazenar a resposta selecionada e o feedback
    var selectedAnswer = remember { mutableStateOf<String?>(null) }
    var feedback = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pergunta
        Text(text = language.name, fontSize = 16.sp, color = Color.Gray)
        Text(
            text = question,
            fontSize = 20.sp,
            modifier = Modifier.padding(24.dp)
        )

        // Botões de opções
        options.forEach { option ->
            Button(
                onClick = {
                    selectedAnswer.value = option
                    feedback.value = if (option == correctAnswer) {
                        "Correto!"
                    } else {
                        "Incorreto. Tente novamente."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                BasicText(text = option)
            }
        }

        // Feedback
//        selectedAnswer.value.let {
//            BasicText(
//                text = feedback ?: "",
//                fontSize = 18.sp,
//                modifier = Modifier.padding(24.dp)
//            )
//        }
    }
}