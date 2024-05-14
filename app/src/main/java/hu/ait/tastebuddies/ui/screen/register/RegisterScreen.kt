package hu.ait.tastebuddies.ui.screen.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.tastebuddies.ui.screen.login.LoginUiState
import hu.ait.tastebuddies.ui.screen.profile.BioDescription
import hu.ait.tastebuddies.ui.screen.profile.FavFoodDialog
import hu.ait.tastebuddies.ui.screen.profile.FavoriteFoods
import hu.ait.tastebuddies.ui.screen.profile.ProfileImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create your profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
//                    IconButton(
//                        onClick = { }
//                    ) {
//                        Icon(Icons.Filled.Info, contentDescription = "Info")
//                    }
                }
            )
        },
    ) { contentPadding ->
        // Screen content
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            var firstName by rememberSaveable { mutableStateOf ("") }
            var lastName by rememberSaveable { mutableStateOf ("") }
            var age by rememberSaveable { mutableStateOf ("") }
            var bio by rememberSaveable { mutableStateOf("") }
            Text("Personal Information", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            UpdatedOutlinedTextField(onChange = {firstName = it}, label = "First name", isDecimal = false)
            UpdatedOutlinedTextField(onChange = {lastName = it}, label = "Last name", isDecimal = false)
            UpdatedOutlinedTextField(onChange = {age = it}, label = "Age", isDecimal = true)

            Text("About yourself", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            UpdatedOutlinedTextField(onChange = {bio = it}, label = "Bio", isDecimal = false)
            
            Button(
                enabled = !(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty()),
                onClick = {
                    registerViewModel.updateUserProfile(firstName, lastName, age.toInt(), bio)
                    onRegisterSuccess()
                }
            ) {
                Text("Create")
            }
            when (registerViewModel.registerUiState) {
                is RegisterUiState.Init -> {}
                is RegisterUiState.Loading -> CircularProgressIndicator()
                is RegisterUiState.Success -> Text(text = "Profile created!")
                is RegisterUiState.Error -> Text(
                    text = "Error: ${(registerViewModel.registerUiState as RegisterUiState.Error).errorMsg}"
                )
            }
        }
    }
}

@Composable
fun UpdatedOutlinedTextField(
    editText: String = "",
    onChange: (String) -> Unit,
    label: String,
    isDecimal: Boolean
){
    var inputText by remember { mutableStateOf(editText) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    OutlinedTextField(value = inputText,
        isError = isError,
        label = {
            Text(text = label)
        },
        keyboardOptions = if (!isDecimal) KeyboardOptions.Default else KeyboardOptions(keyboardType = KeyboardType.Decimal),
        supportingText = {
            if (isError) {
                Text(text = errorText)
            }
        },
        onValueChange = {
            inputText = it
            isError = inputText.isEmpty()
            errorText = "Error: Input is missing!"
            if (isDecimal && inputText.toFloatOrNull() == null) {
                errorText = "Error: Input needs to be a float!"
                isError = true
            }
            onChange(inputText)
        },
        trailingIcon = {
            if (isError)
                Icon(
                    Icons.Filled.Warning,
                    "Error: Input is missing!",
                    tint = MaterialTheme.colorScheme.error)
        }
    )
}