package hu.ait.tastebuddies.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainNavigation (
    val route: String
) {
    object Login : MainNavigation(route = "loginscreen")
    object Main : MainNavigation(route = "mainscreen")
    object Register: MainNavigation(route = "registerscreen")
}