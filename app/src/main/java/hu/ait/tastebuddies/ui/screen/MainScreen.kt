package hu.ait.tastebuddies.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.tastebuddies.ui.navigation.InnerNavigation
import hu.ait.tastebuddies.ui.screen.diary.DiaryScreen
import hu.ait.tastebuddies.ui.screen.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var selectedBottomTab by remember { mutableStateOf(0) }
    var innerNavController: NavHostController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar(content = {
                NavigationBar(
                    //containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    NavigationBarItem(selected = selectedBottomTab == 0,
                        onClick = {
                            selectedBottomTab = 0
                            innerNavController.navigate(InnerNavigation.Profile.route) {
                                innerNavController.popBackStack()
                            }
                        },
                        label = {
                            Text(
                                text = InnerNavigation.Profile.title,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = InnerNavigation.Profile.icon,
                                contentDescription = InnerNavigation.Profile.title,
                            )
                        })
                    NavigationBarItem(selected = selectedBottomTab == 1,
                        onClick = {
                            selectedBottomTab = 1
                            innerNavController.navigate(InnerNavigation.Diary.route) {
                                innerNavController.popBackStack()
                            }
                        },
                        label = {
                            Text(
                                text = InnerNavigation.Diary.title,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = InnerNavigation.Diary.icon,
                                contentDescription = InnerNavigation.Diary.title,
                            )
                        })
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            NavHost(
                navController = innerNavController,
                startDestination = InnerNavigation.Profile.route
            ) {
                composable(InnerNavigation.Profile.route) {
                    ProfileScreen()
                }
                composable(InnerNavigation.Diary.route) {
                    DiaryScreen()
                }
            }
        }
    }
}