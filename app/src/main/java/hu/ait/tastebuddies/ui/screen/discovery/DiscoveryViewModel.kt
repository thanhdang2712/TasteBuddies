package hu.ait.tastebuddies.ui.screen.discovery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.tastebuddies.data.Post
import hu.ait.tastebuddies.data.PostWithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class DiscoveryViewModel: ViewModel() {
    var messagesUiState: DiscoveryUIState by mutableStateOf(DiscoveryUIState.Init)

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