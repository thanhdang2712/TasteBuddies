package hu.ait.tastebuddies.ui.screen.profile

import androidx.collection.arraySetOf
import androidx.lifecycle.ViewModel
import hu.ait.tastebuddies.data.food.FoodItem

// TODO: Change type of food list to FoodItem instead in String
class ProfileViewModel : ViewModel() {
    // var favFoodList = arrayOfNulls<String>(3)
    var favFoodList = arrayOf(FoodItem(1, "Pasta with Cheese", "https://cheeseknees.com/wp-content/uploads/2022/06/Cheese-Pasta-sq.jpg"), null, null)
    var foodCardNum = 0

    fun addFoodToList(foodItem: FoodItem) {
        favFoodList[foodCardNum] = foodItem
    }

    fun removeFoodFromList(index: Int) {
        favFoodList[index] = null
    }
}
