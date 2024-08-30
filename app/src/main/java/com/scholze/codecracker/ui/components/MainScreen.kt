import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scholze.codecracker.ui.components.HomePage
import com.scholze.codecracker.ui.components.Profile

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selected by remember {
        mutableIntStateOf(0)
    }
    //Routes used in the bottom navigator
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePage(navController = navController, selected = selected, onSelectedChange = { selected = it })
        }
        composable("profile/{userId}") {
            Profile(navController = navController, selected = selected, onSelectedChange = { selected = it })
        }
    }
}
