package hu.ait.tastebuddies.data

import java.util.Date

data class Post(
    var uid: String = "",
    var author: String = "",
    var title: String = "",
    var body: String = "",
    var imgUrl: String = "",
    var date: Date = Date(System.currentTimeMillis()),
    var rating: Int = 0
)

data class PostWithId(
    val postId: String,
    val post: Post
)

enum class PostType(val type: String) {
    ATE("Ate"), MADE("Made"), CRAVE("Crave");
}