package hu.ait.tastebuddies.ui.screen.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.food.FoodRecipes

class RegisterViewModel: ViewModel() {
    val db = Firebase.firestore
    var registerUiState: RegisterUiState by mutableStateOf(RegisterUiState.Init)
    fun updateUserProfile(firstName: String, lastName: String, age: Int, bio: String) {
        registerUiState = RegisterUiState.Loading
        val docRef = db.collection("users").document(DataManager.email)
        val name = "$firstName $lastName"
        docRef
            .update("name", name, "age", age, "bio", bio)
            .addOnSuccessListener { registerUiState = RegisterUiState.Success }
            .addOnFailureListener { e -> registerUiState = RegisterUiState.Error(errorMsg = e.message!!) }
    }
}

sealed interface RegisterUiState {
    object Init : RegisterUiState
    object Loading : RegisterUiState
    object Success : RegisterUiState
    data class Error(val errorMsg: String) : RegisterUiState
}