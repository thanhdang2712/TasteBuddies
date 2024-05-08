package hu.ait.tastebuddies.ui.screen.profile

import androidx.collection.arraySetOf
import androidx.lifecycle.ViewModel
import hu.ait.tastebuddies.data.food.FoodItem

// TODO: Change type of food list to FoodItem instead in String
class ProfileViewModel : ViewModel() {
    // var favFoodList = arrayOfNulls<String>(3)
    var favFoodList = arrayOf("Pasta with cheese", null, null)
    var foodCardNum = 0

    fun addFoodToList(foodItem: String) {
        favFoodList[foodCardNum] = foodItem
    }

    fun removeFoodFromList(index: Int) {
        favFoodList[index] = null
    }
}
