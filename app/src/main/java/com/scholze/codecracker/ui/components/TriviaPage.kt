package com.scholze.codecracker.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.scholze.codecracker.data.LanguageTrivia

@Composable
fun TriviaPage(navController: NavController, language: LanguageTrivia) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val context = LocalContext.current
    val score = remember { mutableStateOf<Int?>(null) }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(user?.email) {
        user?.email?.let { email ->
            firestore.collection("user")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Recupera o mapa de scores
                        val scoresMap = document.get("scores") as? Map<String, Long>

                        // Obtém o score com base no nome da linguagem
                        val languageName = language.name // Supondo que você tem esse valor
                        val languageScore = scoresMap?.get(languageName)?.toInt() ?: 0

                        // Atualiza o valor do score
                        score.value = languageScore
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Profile", "Error fetching score: ", exception)
                    // Define score como 0 em caso de falha
                    score.value = 0
                }
        }
    }

    val index = score.value ?: 0

    val question = language.questions[index]

    var feedback = remember { mutableStateOf<String?>(null) }

    fun updateScore() {
        // Nome da linguagem que você deseja atualizar
        val languageName = language.name // Supondo que você tem essa variável

        user?.email?.let { email ->
            firestore.collection("user")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Recupera o mapa de scores atual
                        val scoresMap = document.get("scores") as? MutableMap<String, Long> ?: mutableMapOf()

                        // Pega o score atual para a linguagem ou usa 0 se não existir
                        val currentScore = scoresMap[languageName] ?: 0

                        // Incrementa o score
                        val newScore = currentScore + 1

                        // Atualiza o mapa de scores
                        scoresMap[languageName] = newScore

                        // Atualiza o documento no Firestore com o novo mapa de scores
                        firestore.collection("user")
                            .document(email)
                            .update("scores", scoresMap)
                            .addOnSuccessListener {
                                // Atualiza o valor local do score após a atualização no Firestore
                                score.value = newScore.toInt()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("ERRRO", "Error updating score: ", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERRRO", "Error fetching current scores: ", exception)
                }
        }
    }


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
            text = question.question,
            fontSize = 20.sp,
            modifier = Modifier.padding(24.dp)
        )

        // Botões de opções
        question.options.forEach { option ->
            Button(
                onClick = {
                    feedback.value = if (option.answer) {
                        updateScore()
                        "Correto!"
                    } else {
                        "Incorreto. Tente novamente."
                    }
                    Toast.makeText(context,feedback.value,Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                BasicText(text = option.option)
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