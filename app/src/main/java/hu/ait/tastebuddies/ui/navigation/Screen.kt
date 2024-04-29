package hu.ait.tastebuddies.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen (
    val route: String,
    val icon: ImageVector
) {
    object Login : Screen(
        route = "login",
        icon = Icons.Outlined.Clear)
    object Profile : Screen(
        route = "profile",
        icon = Icons.Outlined.AccountCircle)
    object Diary : Screen(
        route = "diary",
        icon = Icons.Outlined.Create)
//    object Discovery : Screen(
//        route = "discovery",
//        icon = Icons.Outlined.Home)
//    object CraveList : Screen(
//        route = "crave list",
//        icon = Icons.Outlined.FavoriteBorder)
}