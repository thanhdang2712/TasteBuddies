package hu.ait.tastebuddies.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.User
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    suspend fun registerUser(email: String, password: String): AuthResult? {
        loginUiState = LoginUiState.Loading
        try {
            var result = auth.createUserWithEmailAndPassword(email,password).await()
            if (result.user != null) {
                val newUser = User(email)
                db.collection("users").document(email).set(newUser)
                loginUiState = LoginUiState.RegisterSuccess
            } else {
                loginUiState = LoginUiState.Error("Register failed!")
            }
            return result
        } catch (e: Exception) {
            loginUiState = LoginUiState.Error(e.message)
            return null
        }
    }

    suspend fun loginUser(email: String, password: String): AuthResult? {
        loginUiState = LoginUiState.Loading

        try {
            val result = auth.signInWithEmailAndPassword(email,password).await()
            if (result.user != null) {
                loginUiState = LoginUiState.LoginSuccess
            } else {
                loginUiState = LoginUiState.Error("Login failed")
            }
            return result
        } catch (e: Exception) {
            loginUiState = LoginUiState.Error(e.message)
            return null
        }
    }

}

sealed interface LoginUiState {
    object Init : LoginUiState
    object Loading : LoginUiState
    object LoginSuccess : LoginUiState
    object RegisterSuccess : LoginUiState
    data class Error(val error: String?) : LoginUiState
}