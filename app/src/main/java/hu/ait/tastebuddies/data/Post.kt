package hu.ait.tastebuddies.data

import java.util.Date

data class Post(
    var uid: String = "",
    var authorName: String = "",
    var authorEmail: String = "",
    var title: String = "",
    var body: String = "",
    var imgUrl: String = "",
    var date: Date = Date(System.currentTimeMillis()),
    var rating: Int = 0,
    var likes: List<String> = emptyList(),
    var postType: PostType = PostType.ATE
)

data class PostWithId(
    val postId: String,
    val post: Post
)

enum class PostType(val type: String) {
    ATE("ate"), MADE("made"), CRAVE("craves");
}