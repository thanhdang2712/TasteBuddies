package hu.ait.tastebuddies.data

import java.util.Date

data class User(
    var uid: String = "",
    var name: String = "",
    var age: Int = -1,
    var favFoods: List<String> = emptyList(),
    var profilePic: String = "",
    var dateJoined: Date = Date(System.currentTimeMillis()),
    var bio: String = ""
)

data class UserWithID(
    val userId: String,
    val user: User
)