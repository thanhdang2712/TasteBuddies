package hu.ait.tastebuddies.ui.screen.diary

import android.os.Parcelable
import hu.ait.tastebuddies.data.PostType
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

@Parcelize
data class DiaryNote(
    val id: Int = Random.nextInt(),
    var date: String? = "",
    var noteType: PostType? = null,
    var title: String? = "",
    var body: String? = "",
    var rating: Float? = 0f,
    var image: String = ""
): Parcelable {
    constructor( date: String?, noteType: PostType?, title: String?, body: String?, rating: Float?) :
    this(Random.nextInt(), date, noteType, title, body, rating)
}