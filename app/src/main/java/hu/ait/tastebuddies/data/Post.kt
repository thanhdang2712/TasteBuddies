package hu.ait.tastebuddies.data

import androidx.annotation.DrawableRes
import hu.ait.tastebuddies.R
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

enum class PostType(val type: String, @DrawableRes val icon: Int) {
    ATE("ate", R.drawable.ate),
    MADE("made", R.drawable.made),
    CRAVE("craves", R.drawable.crave);
}