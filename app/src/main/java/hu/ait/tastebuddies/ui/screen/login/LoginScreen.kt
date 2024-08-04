package hu.ait.tastebuddies.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.ui.theme.Green40
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("lequang@grinnell.edu") }
    var password by rememberSaveable { mutableStateOf("123456") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.apple_wallpaper),
                contentScale = ContentScale.FillBounds,
                alpha = 0.25f)
    ) {
        Text(
            text = "TasteBuddies",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Green40
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "E-mail")
                },
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, null)
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "Password")
                },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation =
                if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.Clear, null)
                        } else {
                            Icon(Icons.Default.Info, null)
                        }
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        val result = loginViewModel.loginUser(email, password)
                        if (result?.user != null) {
                            onLoginSuccess()
                        }
                    }
                    DataManager.email = email
                }) {
                    Text(text = "Login")
                }
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        val result = loginViewModel.registerUser(email, password)
                        if (result != null) onRegisterSuccess()
                    }
                    DataManager.email = email
                }) {
                    Text(text = "Register")
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginViewModel.loginUiState) {
                is LoginUiState.Init -> {}
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.RegisterSuccess -> Text(text = "Register OK")
                is LoginUiState.LoginSuccess -> Text(text = "Login OK")
                is LoginUiState.Error -> Text(
                    text = "Error: ${(loginViewModel.loginUiState as LoginUiState.Error).error}"
                )
            }
        }
    }
}