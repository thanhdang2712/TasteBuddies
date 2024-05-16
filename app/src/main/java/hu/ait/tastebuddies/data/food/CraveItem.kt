package hu.ait.tastebuddies.data.food

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CraveItem(
    val id: Int = 0,
    val name: String = "",
    val image: String = "",
    var hasEaten: Boolean = false
): Parcelable