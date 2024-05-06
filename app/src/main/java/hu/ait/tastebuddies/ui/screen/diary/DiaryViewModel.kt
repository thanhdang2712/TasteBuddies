package hu.ait.tastebuddies.ui.screen.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.tastebuddies.data.Post
import hu.ait.tastebuddies.data.food.FoodRecipes
import hu.ait.tastebuddies.network.FoodAPI
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    var foodAPI: FoodAPI
) : ViewModel() {
    var foodUiState: FoodUiState by mutableStateOf(FoodUiState.Init)
    var diaryUiState: DiaryUiState by mutableStateOf(DiaryUiState.Init)

    fun getFoodRecipes(query: String, apiKey: String, number: String) { // Change to get Dropdown or something
        foodUiState = FoodUiState.Loading
        viewModelScope.launch {
            try {
                // Starts the network communication and returns a roverPhotos object
                val result = foodAPI.getFoodRecipes(apiKey, query, number)
                foodUiState = FoodUiState.Success(result)
            } catch (e: Exception) {
                foodUiState = FoodUiState.Error(e.message!!)
            }
        }
    }

    fun getFoodNames(foodRecipes: FoodRecipes): List<String> {
        val foodNames = mutableListOf<String>()
        // Iterate over the recipes
        foodRecipes.searchResults?.get(0)?.results?.forEach { result ->
            result?.name?.let { foodNames.add(it) }
        }

        // Return the list of food names
        return foodNames
    }

    fun uploadDiaryPost(postTitle: String, postBody: String, imgUrl: String = "") {
        diaryUiState = DiaryUiState.LoadingPostUpload
        val newPost = Post(
            FirebaseAuth.getInstance().uid!!,
            FirebaseAuth.getInstance().currentUser?.email!!,
            postTitle,
            postBody,
            imgUrl
        )
        val postsCollection = FirebaseFirestore.getInstance().collection("posts")
        postsCollection.add(newPost)
            .addOnSuccessListener {
                diaryUiState = DiaryUiState.PostUploadSuccess
            }
            .addOnFailureListener{
                diaryUiState = DiaryUiState.ErrorDuringPostUpload(
                    "Diary post upload failed")
            }
    }
}

sealed interface DiaryUiState {
    object Init : DiaryUiState
    object LoadingPostUpload : DiaryUiState
    object PostUploadSuccess : DiaryUiState
    data class ErrorDuringPostUpload(val error: String?) : DiaryUiState

    object LoadingImageUpload : DiaryUiState
    data class ErrorDuringImageUpload(val error: String?) : DiaryUiState
    object ImageUploadSuccess : DiaryUiState
}

sealed interface FoodUiState {
    object Init : FoodUiState
    object Loading : FoodUiState
    data class Success(val foodRecipes: FoodRecipes) : FoodUiState
    data class Error(val errorMsg: String) : FoodUiState
}