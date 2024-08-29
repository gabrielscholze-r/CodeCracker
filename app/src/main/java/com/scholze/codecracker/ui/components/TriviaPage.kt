import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
                        val scoresMap = document.get("scores") as? Map<String, Long>
                        val languageName = language.name
                        val languageScore = scoresMap?.get(languageName)?.toInt() ?: 0

                        score.value = languageScore
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Profile", "Error fetching score: ", exception)
                    score.value = 0
                }
        }
    }

    val index = score.value ?: 0
    val question = language.questions[index]
    var feedback = remember { mutableStateOf<String?>(null) }

    fun updateScore() {
        val languageName = language.name

        user?.email?.let { email ->
            firestore.collection("user")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val scoresMap = document.get("scores") as? MutableMap<String, Long> ?: mutableMapOf()
                        val currentScore = scoresMap[languageName] ?: 0
                        val newScore = currentScore + 1

                        scoresMap[languageName] = newScore

                        firestore.collection("user")
                            .document(email)
                            .update("scores", scoresMap)
                            .addOnSuccessListener {
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = language.name, fontSize = 16.sp, color = Color.Gray)
            Text(
                text = question.question,
                fontSize = 20.sp,
                modifier = Modifier.padding(24.dp)
            )

            question.options.shuffled().forEach { option ->
                Button(
                    onClick = {
                        feedback.value = if (option.answer) {
                            updateScore()
                            "Correto!"
                        } else {
                            "Incorreto. Tente novamente."
                        }
                        Toast.makeText(context, feedback.value, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = option.option, color = Color.White)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { navController.navigate("home/${auth.currentUser?.uid}") },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .background(Color.Gray, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                val totalQuestions = language.questions.size
                val progress = (index + 1).toFloat() / totalQuestions

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(200.dp)
                        .background(Color.Gray, shape = MaterialTheme.shapes.small)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Color.Blue, shape = MaterialTheme.shapes.small)
                    )
                }
            }

            Text(
                text = "${index + 1}/${language.questions.size}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}