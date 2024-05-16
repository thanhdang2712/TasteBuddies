package hu.ait.tastebuddies.data.food

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodItem(
    val id: Int = -1,
    val name: String = "",
    val image: String = ""
) : Parcelable