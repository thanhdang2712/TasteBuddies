package hu.ait.tastebuddies.ui.screen.cravelist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import hu.ait.tastebuddies.data.food.FoodItem

class CravelistViewModel: ViewModel() {
    private var _savedList = mutableStateListOf<FoodItem>()

    fun addFoodItem(food: FoodItem) {
        _savedList.add(food)

    }

    fun removeFood(food: FoodItem) {
        _savedList.remove(food)
    }

    fun isEmpty(): Boolean {
        return _savedList.isNullOrEmpty()
    }

    fun getAllFood(): List<FoodItem> {
        return _savedList
    }
}