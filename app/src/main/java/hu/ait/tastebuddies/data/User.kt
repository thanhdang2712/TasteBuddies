package hu.ait.tastebuddies.data

import hu.ait.tastebuddies.data.food.FoodItem
import java.util.Date

data class User(
    var email: String = "",
    var name: String = "",
    var age: Int = -1,
    var favFoods: List<FoodItem?> = listOf(null, null, null),
    var profilePic: String = "",
    var dateJoined: Date = Date(System.currentTimeMillis()),
    var bio: String = "",
    var posts: List<Post> = emptyList()
)

data class UserWithID(
    val userId: String,
    val user: User
)