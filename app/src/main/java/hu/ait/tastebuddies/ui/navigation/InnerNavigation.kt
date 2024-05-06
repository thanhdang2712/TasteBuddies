package hu.ait.tastebuddies.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class InnerNavigation (
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Profile : InnerNavigation(
        route = "proflescreen",
        title = "Profile",
        icon = Icons.Outlined.AccountCircle)
    object Diary : InnerNavigation(
        route = "diaryscreen",
        title = "Diary",
        icon = Icons.Outlined.Create)
    object Discovery : InnerNavigation(
        route = "discoveryscreen",
        title = "Discovery",
        icon = Icons.Outlined.Home)
    object CraveList : InnerNavigation(
        route = "cravelistscreen",
        title = "Cravelist",
        icon = Icons.Outlined.FavoriteBorder)
}