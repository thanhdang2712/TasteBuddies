package hu.ait.tastebuddies.ui.screen.diary

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hu.ait.tastebuddies.data.food.FoodRecipes
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun DiaryScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var postTitle by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var foodNames by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var showDiaryEntryScreen by rememberSaveable{ mutableStateOf(false) }
    val context = LocalContext.current

//    // Debug
//    LaunchedEffect(foodNames) {
//        println("foodNames: $foodNames")
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Diary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(shape = MaterialTheme.shapes.medium) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Add a Diary Entry",
                            )
                            Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                OutlinedTextField(
                                    value = postTitle,
                                    label = { Text(text = "Today I ate...") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = "Search Icon"
                                        )
                                    },
                                    onValueChange = {
                                        postTitle = it
                                    }
                                )
                                DropdownMenu(
                                    expanded = showDropdown,
                                    onDismissRequest = {
                                        showDropdown = false
                                    }
                                ) {
                                    foodNames.forEach { name ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = name, modifier = Modifier
                                                        .fillMaxWidth()
                                                        .align(Alignment.Start)
                                                )
                                            },
                                            onClick = {
                                                postTitle = name
                                                showDropdown = false
                                                showDialog = false
                                                showDiaryEntryScreen = true
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            if (!showDropdown) {
                                Button(onClick = { }) {
                                    Text(text = "Cancel")
                                }
                                Button(onClick = {
                                    // Call your search function here
                                    diaryViewModel.getFoodRecipes(
                                        postTitle,
                                        "9d3cc85171a74f679f647ab3dc919805",
                                        "10"
                                    )
                                    showDropdown = true
                                }) {
                                    Text(text = "Search")
                                }
                            }

                            when (diaryViewModel.foodUiState) {
                                is FoodUiState.Init -> {}
                                is FoodUiState.Loading -> CircularProgressIndicator()
                                is FoodUiState.Success -> {
                                    foodNames =
                                        diaryViewModel.getFoodNames((diaryViewModel.foodUiState as FoodUiState.Success).foodRecipes)
                                }

                                is FoodUiState.Error -> Text(
                                    text = "Error: " +
                                            "${(diaryViewModel.foodUiState as FoodUiState.Error).errorMsg}"
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showDiaryEntryScreen) {
            DiaryEntryScreen(postTitle, diaryViewModel)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryEntryScreen(
    postTitle: String,
    diaryViewModel: DiaryViewModel
) {
    val currentDate by rememberSaveable { mutableStateOf(LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("dd-MMM-yyyy"))) }
    var postBody by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(20.dp).padding(top = 56.dp)
    ) {
        Text(text = postTitle, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        Text(
            text = "Date: $currentDate",
            style = TextStyle(fontSize = 16.sp, color = Color.Gray)
        )
        OutlinedTextField(value = postBody,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Add a diary entry...") },
            onValueChange = {
                postBody = it
            }
        )

        Button(onClick = {
            diaryViewModel.uploadDiaryPost(postTitle, postBody)
        }) {
            Text(text = "Upload")
        }
    }
}
