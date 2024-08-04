package hu.ait.tastebuddies.ui.screen.cravelist

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.User
import hu.ait.tastebuddies.data.food.CraveItem
import hu.ait.tastebuddies.data.food.FoodItem
import hu.ait.tastebuddies.ui.screen.profile.ProfileUiState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class CravelistViewModel: ViewModel() {
    var cravelistUiState: CravelistUiState by mutableStateOf(CravelistUiState.Init)
    private val db = Firebase.firestore
    var cravelist: List<CraveItem> = emptyList()
    private var nextIndex = -1

    fun addFoodItem(craveItem: CraveItem) {
        val mutableCraveList = cravelist.toMutableList()
        mutableCraveList.add(craveItem)
        cravelist = mutableCraveList.toList()
        db.collection("users").document(DataManager.email)
            .update("cravelist", cravelist)
            .addOnSuccessListener {  }
            .addOnFailureListener {
                // do something
            }
    }

    fun getNextIndex(): Int {
        return nextIndex++
    }

    fun updateItemBookmark(craveItem: CraveItem, state: Boolean) {
        val mutableCraveList = cravelist.toMutableList()
        mutableCraveList[craveItem.id].hasEaten = state
        cravelist = mutableCraveList.toList()
        db.collection("users").document(DataManager.email)
            .update("cravelist", cravelist)
            .addOnSuccessListener {  }
            .addOnFailureListener {
                // do something
            }
    }

    fun getCravelist() {
        cravelistUiState = CravelistUiState.Loading
        db.collection("users").document(DataManager.email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentUser = document.toObject(User::class.java)
                    cravelist = currentUser?.cravelist!!
                    Log.d("CRAVELIST", cravelist.toString())
                    nextIndex = cravelist.size
                    cravelistUiState = CravelistUiState.Success
                }
            }
            .addOnFailureListener { e ->
                cravelistUiState = CravelistUiState.Error(e.message!!)
            }
    }
}

sealed interface CravelistUiState {
    object Init: CravelistUiState
    object Loading: CravelistUiState
    object Success: CravelistUiState
    data class Error(val errorMsg: String): CravelistUiState
}