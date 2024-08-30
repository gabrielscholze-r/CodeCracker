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

    // Get the Trivias and set the current score
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
                    Log.e("TriviaPage", "Error fetching score: ", exception)
                    score.value = 0
                }
        }
    }

    val index = score.value ?: 0
    val question = language.questions[index]
    var feedback = remember { mutableStateOf<String?>(null) }

    //Update the score on the database
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
                        //Check if user has concluded the current trivia
                        if (newScore.toInt()==language.questions.size)
                        {
                            navController.popBackStack()
                        }
                        scoresMap[languageName] = newScore
                        //update
                        firestore.collection("user")
                            .document(email)
                            .update("scores", scoresMap)
                            .addOnSuccessListener {
                                if (newScore.toInt()<language.questions.size) {
                                    score.value = newScore.toInt()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("ERROR", "Error updating score: ", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERROR", "Error fetching current scores: ", exception)
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
            //Map the questions in random order to show
            question.options.shuffled().forEach { option ->
                Button(
                    onClick = {
                        //Check if was the right answer
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
                // Generate a progress bar for the trivia based on the current score
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