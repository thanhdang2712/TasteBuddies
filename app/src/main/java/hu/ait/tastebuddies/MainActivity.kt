package hu.ait.tastebuddies

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle
import hu.ait.tastebuddies.ui.navigation.Screen
import hu.ait.tastebuddies.ui.screen.diary.DiaryScreen
import hu.ait.tastebuddies.ui.screen.login.LoginScreen
import hu.ait.tastebuddies.ui.screen.profile.ProfileScreen
import hu.ait.tastebuddies.ui.theme.TasteBuddiesTheme
import kotlin.reflect.KClass

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
                    var showNavBar by rememberSaveable { mutableStateOf(false) }
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val navigationItems = Screen::class.sealedSubclasses.map {
                        it.objectInstance as Screen
                    }
                    var selectedItem by remember { mutableIntStateOf(0) }
                    Log.d("NAV", "Testing")
                    if (currentRoute != null) {
                        Log.d("NAV", currentRoute)
                    }
                    Scaffold(
                        bottomBar = {
                            if (showNavBar)
                                AnimatedBottomBar(
                                    selectedItem = selectedItem,
                                    itemSize = navigationItems.take(3).size,
                                    containerColor = Color.LightGray,
                                    indicatorStyle = IndicatorStyle.FILLED,
                                    indicatorColor = Color.White
                                ) {
                                    navigationItems.take(3).forEachIndexed { index, navigationItem ->
                                        BottomBarItem(
                                            selected = currentRoute == navigationItem.route,
                                            onClick = {
                                                if (currentRoute == null) return@BottomBarItem
                                                if (currentRoute != navigationItem.route) {
                                                    selectedItem = index
                                                    navController.popBackStack()
                                                    navController.navigate(navigationItem.route) {
                                                        navController.graph.startDestinationRoute?.let { route ->
                                                            popUpTo(route) {
                                                                saveState = true
                                                            }
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            imageVector = navigationItem.icon,
                                            label = navigationItem.route,
                                            containerColor = Color.Transparent
                                        )
                                    }
                                }
                        }
                    ) {
                        NavGraph(navController, showNavBar = { showNavBar = it })
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    showNavBar: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                showNavBar(true)
                navController.navigate(Screen.Profile.route)
            })
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Diary.route) {
            DiaryScreen()
        }
    }
}