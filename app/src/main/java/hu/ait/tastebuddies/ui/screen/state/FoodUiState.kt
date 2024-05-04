package hu.ait.tastebuddies.ui.screen.state
import hu.ait.tastebuddies.data.food.FoodRecipes

sealed interface FoodUiState {
    object Init : FoodUiState
    object Loading : FoodUiState
    data class Success(val foodRecipes: FoodRecipes) : FoodUiState
    data class Error(val errorMsg: String) : FoodUiState
}