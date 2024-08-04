package hu.ait.tastebuddies

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.tastebuddies.ui.navigation.MainNavigation
import hu.ait.tastebuddies.ui.screen.MainScreen
import hu.ait.tastebuddies.ui.screen.cravelist.CravelistScreen
import hu.ait.tastebuddies.ui.screen.diary.DiaryScreen
import hu.ait.tastebuddies.ui.screen.login.LoginScreen
import hu.ait.tastebuddies.ui.screen.register.RegisterScreen
import hu.ait.tastebuddies.ui.theme.TasteBuddiesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasteBuddiesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavGraph()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = MainNavigation.Login.route
    ) {
        composable(MainNavigation.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(MainNavigation.Main.route) },
                onRegisterSuccess = { navController.navigate(MainNavigation.Register.route) }
            )
        }

        composable(MainNavigation.Register.route) {
            RegisterScreen(onRegisterSuccess = { navController.navigate(MainNavigation.Main.route) })
        }

        composable(MainNavigation.Diary.route) {
            DiaryScreen()
        }

        composable(MainNavigation.Cravelist.route) {
            CravelistScreen()
        }

        composable(MainNavigation.Main.route) {
            MainScreen()
        }

    }
}