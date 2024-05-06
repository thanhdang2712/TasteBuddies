package hu.ait.tastebuddies.ui.screen.diary

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hu.ait.tastebuddies.data.food.FoodRecipes
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun DiaryScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var postTitle by rememberSaveable { mutableStateOf("") }
    var postBody by rememberSaveable { mutableStateOf("") }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var foodNames by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val context = LocalContext.current

    LaunchedEffect(foodNames) {
        println("foodNames: $foodNames")
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Text(text = "Add a Diary Entry", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = postTitle,
            label = { Text(text = "Today I ate...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            onValueChange = {
                postTitle = it
            }
        )
        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = {
                showDropdown = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            foodNames.forEach { name ->
                DropdownMenuItem(
                    text = { Text(text = name, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)) },
                    onClick = {
                        postTitle = name
                        showDropdown = false
                    }
                )
            }
        }
//        SpinnerSample(
//            foodNames,
//            preselected = postTitle,
//            onSelectionChanged = { postTitle = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//        )
        Spacer(modifier = Modifier.height(16.dp))
        if (!showDropdown) {
            Button(onClick = { }) {
                Text(text = "Cancel")
            }
            Button(onClick = {
                // Call your search function here
                diaryViewModel.getFoodRecipes(postTitle, "9d3cc85171a74f679f647ab3dc919805", "10")
                showDropdown = true
            }) {
                Text(text = "Search")
            }
        }

        when (diaryViewModel.foodUiState) {
            is FoodUiState.Init -> {}
            is FoodUiState.Loading -> CircularProgressIndicator()
            is FoodUiState.Success ->
            {
                foodNames = diaryViewModel.getFoodNames((diaryViewModel.foodUiState as FoodUiState.Success).foodRecipes)
            }

            is FoodUiState.Error -> Text(
                text = "Error: " +
                        "${(diaryViewModel.foodUiState as FoodUiState.Error).errorMsg}"
            )
        }


//    Column(
//        modifier = Modifier.padding(20.dp)
//    ) {
//        OutlinedTextField(value = postTitle,
//            modifier = Modifier.fillMaxWidth(),
//            label = { Text(text = "Today I ate") },
//            onValueChange = {
//                postTitle = it
//            }
//        )
//        OutlinedTextField(value = postBody,
//            modifier = Modifier.fillMaxWidth(),
//            label = { Text(text = "Add a diary entry...") },
//            onValueChange = {
//                postBody = it
//            }
//        )
//
//        Button(onClick = {
//            diaryViewModel.uploadDiaryPost(postTitle, postBody)
//        }) {
//            Text(text = "Upload")
//        }
//    }
    }
}