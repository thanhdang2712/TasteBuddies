package hu.ait.tastebuddies.ui.screen.diary

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firestore.v1.Write
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.tastebuddies.data.food.FoodRecipes
import hu.ait.tastebuddies.network.FoodAPI
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    var foodAPI: FoodAPI
) : ViewModel() {
    var foodUiState: FoodUiState by mutableStateOf(FoodUiState.Init)
    var writePostUiState: WritePostUiState by mutableStateOf(WritePostUiState.Init)

    fun getFoodRecipes(query: String, apiKey: String, number: String) {
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

//    fun uploadPost(
//        postTitle: String, postBody: String, imgUrl: String = ""
//    ) {
//        writePostUiState = WritePostUiState.LoadingPostUpload
//        val newPost = Post(
//            FirebaseAuth.getInstance().uid!!,
//            FirebaseAuth.getInstance().currentUser?.email!!,
//            postTitle,
//            postBody,
//            imgUrl
//        )
//        val postsCollection = FirebaseFirestore.getInstance().collection(
//            "posts")
//        postsCollection.add(newPost)
//            .addOnSuccessListener{
//                writePostUiState = WritePostUiState.PostUploadSuccess
//            }
//            .addOnFailureListener{
//                writePostUiState = WritePostUiState.ErrorDuringPostUpload(
//                    "Post upload failed")
//            }
//    }

    @RequiresApi(Build.VERSION_CODES.P)
    public fun uploadPostImage(
        contentResolver: ContentResolver, imageUri: Uri,
        title: String, postBody: String
    ) {
        viewModelScope.launch {
            writePostUiState = WritePostUiState.LoadingImageUpload

            val source = ImageDecoder.createSource(contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageInBytes = baos.toByteArray()

            // prepare the empty file in the cloud
            val storageRef = FirebaseStorage.getInstance().getReference()
            val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
            val newImagesRef = storageRef.child("images/$newImage")

            // upload the jpeg byte array to the created empty file
            newImagesRef.putBytes(imageInBytes)
                .addOnFailureListener { e ->
                    writePostUiState = WritePostUiState.ErrorDuringImageUpload(e.message)
                }.addOnSuccessListener { taskSnapshot ->
                    writePostUiState = WritePostUiState.ImageUploadSuccess


                    newImagesRef.downloadUrl.addOnCompleteListener(
                        object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {
                                // the public URL of the image is: task.result.toString()
                                // uploadPost(title, postBody, task.result.toString())
                            }
                        })
                }
        }
    }


}

sealed interface WritePostUiState {
    object Init : WritePostUiState
    object LoadingPostUpload : WritePostUiState
    object PostUploadSuccess : WritePostUiState
    data class ErrorDuringPostUpload(val error: String?) : WritePostUiState

    object LoadingImageUpload : WritePostUiState
    data class ErrorDuringImageUpload(val error: String?) : WritePostUiState
    object ImageUploadSuccess : WritePostUiState
}

sealed interface FoodUiState {
    object Init : FoodUiState
    object Loading : FoodUiState
    data class Success(val foodRecipes: FoodRecipes) : FoodUiState
    data class Error(val errorMsg: String) : FoodUiState
}