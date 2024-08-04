package hu.ait.tastebuddies.ui.screen.profile

import android.util.Log
import androidx.collection.arraySetOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.User
import hu.ait.tastebuddies.data.food.FoodItem

class ProfileViewModel : ViewModel() {
    var profileUiState: ProfileUiState by mutableStateOf(ProfileUiState.Init)

    private val db = Firebase.firestore
    var currentUser: User? = null

    var favFoodList: Array<FoodItem?> = arrayOf(null, null, null)
    var foodCardNum = 0

    fun initializeProfile() {
        profileUiState = ProfileUiState.Loading
        val docRef = db.collection("users").document(DataManager.email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    currentUser = document.toObject(User::class.java)
                    Log.d("USER", currentUser.toString())
                    for (i in 0 .. 2) {
                        favFoodList[i] = currentUser?.favFoods?.get(i)
                    }
                    DataManager.name = currentUser!!.name
                    profileUiState = ProfileUiState.Success
                }
            }
            .addOnFailureListener { e ->
                profileUiState = ProfileUiState.Error(e.message!!)
            }
    }

    private fun updateFavFoodList(isRemove: Boolean = false) {
        db.collection("users").document(DataManager.email)
            .update("favFoods", favFoodList.toList())
            .addOnSuccessListener { if (isRemove) initializeProfile() }
            .addOnFailureListener {
                // do something
            }
    }

    fun addFoodToList(foodItem: FoodItem) {
        favFoodList[foodCardNum] = foodItem
        updateFavFoodList()
    }

    fun removeFoodFromList(index: Int) {
        favFoodList[index] = null
        updateFavFoodList(true)
    }
}

sealed interface ProfileUiState {
    object Init: ProfileUiState
    object Loading: ProfileUiState
    object Success: ProfileUiState
    data class Error(val errorMsg: String): ProfileUiState
}
