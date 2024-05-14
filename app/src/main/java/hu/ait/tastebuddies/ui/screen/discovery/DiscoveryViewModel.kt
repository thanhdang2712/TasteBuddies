package hu.ait.tastebuddies.ui.screen.discovery

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.Post
import hu.ait.tastebuddies.data.PostWithId
import hu.ait.tastebuddies.data.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class DiscoveryViewModel: ViewModel() {
    private var likesList: List<String> = emptyList()
    private val db = Firebase.firestore

    fun getLikes(postID: String) = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("posts").document(postID)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val post = snapshot.toObject(Post::class.java)
                        likesList = post!!.likes
                    } else {

                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose { // when we navigate out from the screen,
            // the flow stosp and we stop here the firebase listener
            snapshotListener.remove()
        }
    }

    fun updateLikes(postID: String, likes: List<String>) {
        val mutableLikesList = likes.toMutableList()
        if (mutableLikesList.contains(DataManager.email)) {
            Log.d("TEST", "email in like list")
            mutableLikesList.remove(DataManager.email)
        } else {
            mutableLikesList.add(DataManager.email)
        }
        val docRef = db.collection("posts").document(postID)
        docRef
            .update("likes", mutableLikesList.toList())
            .addOnSuccessListener { Log.d("INCREMENT LIKES", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("INCREMENT LIKES", "Error updating document", e) }

    }

    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("posts")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        val postWithIdList = mutableListOf<PostWithId>()

                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                        }

                        DiscoveryUIState.Success(
                            postWithIdList
                        )
                    } else {
                        DiscoveryUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose { // when we navigate out from the screen,
            // the flow stosp and we stop here the firebase listener
            snapshotListener.remove()
        }
    }

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            "posts"
        ).document(postKey).delete()
    }
}

sealed interface DiscoveryUIState {
    object Init : DiscoveryUIState
    object Loading : DiscoveryUIState
    data class Success(val postList: List<PostWithId>) : DiscoveryUIState
    data class Error(val error: String?) : DiscoveryUIState
}

sealed interface LikesUiState {
    object Init : LikesUiState
    data class Success(val likesList: List<String>): LikesUiState
}