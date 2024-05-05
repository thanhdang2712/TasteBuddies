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
import hu.ait.tastebuddies.network.FoodAPI
import hu.ait.tastebuddies.ui.screen.repository.FoodUiState
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
                val result = foodAPI.getFoodRecipes(apiKey, query, "2")
                foodUiState = FoodUiState.Success(result)
            } catch (e: Exception) {
                foodUiState = FoodUiState.Error(e.message!!)
            }
        }

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