package hu.ait.tastebuddies.ui.screen.diary

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.PostType
import hu.ait.tastebuddies.data.food.FoodItem
import hu.ait.tastebuddies.data.food.FoodRecipes
import hu.ait.tastebuddies.ui.screen.profile.FoodCard
import hu.ait.tastebuddies.ui.screen.profile.ProfileViewModel
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
    var postType by rememberSaveable { mutableStateOf(PostType.ATE) }
//    var foodNames by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var foodNames by rememberSaveable { mutableStateOf(listOf(FoodItem(1, "Pasta", ""), FoodItem(2, "Pizza", ""), FoodItem(3, "Banh mi", ""))) }
    var showDiaryEntryScreen by rememberSaveable{ mutableStateOf(false) }
    val allPostTypes by rememberSaveable{ mutableStateOf(listOf(PostType.ATE, PostType.MADE, PostType.CRAVE)) }
    var selected by rememberSaveable { mutableStateOf(PostType.ATE) }
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
                            showDropdown = true
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { contentPadding ->

        // TODO: Display the post type icon to
        Column(modifier = Modifier.padding(contentPadding)) {
            if (showDropdown) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        modifier = Modifier.wrapContentSize(Alignment.BottomEnd)
                    ) {
                        allPostTypes.forEach { listEntry ->
                            DropdownMenuItem(
                                onClick = {
                                    selected = listEntry
                                    postType = selected
                                    showDialog = true
                                    showDropdown = false
                                },
                                text = {
                                    Text(
                                        text = listEntry.type,
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                            .padding(10.dp)
                                    )
                                },
                                leadingIcon = {
                                    when (listEntry) {
                                        PostType.ATE -> {
                                            Icon(
                                                painter = painterResource(R.drawable.ate),
                                                contentDescription = "I ate out"
                                            )
                                        }
                                        PostType.MADE -> {
                                            Icon(
                                                painter = painterResource(R.drawable.made),
                                                contentDescription = "I made this meal myself"
                                            )
                                        }
                                        PostType.CRAVE -> {
                                            Icon(
                                                painter = painterResource(R.drawable.crave),
                                                contentDescription = "I really want to eat this"
                                            )
                                        }
                                    }
                                }

                            )
                        }
                    }
                }


            }
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Add a Diary Entry",
                            )
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
//                                    diaryViewModel.getFoodRecipes(
//                                        postTitle,
//                                        "9d3cc85171a74f679f647ab3dc919805",
//                                        "10"
//                                    )
                                }
                            )
                            // Show items in LazyColumn
                            if (foodNames.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    items(foodNames) {
                                        FoodSearchCard(
                                            diaryViewModel,
                                            foodName = it.name,
                                            onFoodSelectedListener = {
                                                postTitle = it.name
                                                showDialog = false
                                                showDiaryEntryScreen = true
                                            })
                                    }
                                }
                            }
//                                DropdownMenu(
//                                    expanded = showDropdown,
//                                    onDismissRequest = {
//                                        showDropdown = false
//                                    }
//                                ) {
//                                    foodNames.forEach { name ->
//                                        DropdownMenuItem(
//                                            text = {
//                                                Text(
//                                                    text = name, modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .align(Alignment.Start)
//                                                )
//                                            },
//                                            onClick = {
//                                                postTitle = name
//                                                showDropdown = false
//                                                showDialog = false
//                                                showDiaryEntryScreen = true
//                                            }
//                                        )
//                                    }
//                                }

//                            Spacer(modifier = Modifier.height(16.dp))
//                            if (!showDropdown) {
//                                Button(onClick = { }) {
//                                    Text(text = "Cancel")
//                                }
//                                Button(onClick = {
//                                    // Call your search function here
//                                    diaryViewModel.getFoodRecipes(
//                                        postTitle,
//                                        "9d3cc85171a74f679f647ab3dc919805",
//                                        "10"
//                                    )
//                                    showDropdown = true
//                                }) {
//                                    Text(text = "Search")
//                                }
//                            }

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
    var rating by rememberSaveable { mutableStateOf(0f) }
    Column(
        modifier = Modifier.padding(20.dp).padding(top = 56.dp)
    ) {
        Text(text = postTitle, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Date: $currentDate",
            style = TextStyle(fontSize = 16.sp, color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(5.dp))
        StarRatingBar(
            maxStars = 5,
            rating = rating,
            onRatingChanged = {
                rating = it
            }
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


@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    size: Int = 12
) {
    val density = LocalDensity.current.density
    val starSize = (size * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0xFFCCCCCC)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize).height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
fun FoodSearchCard(diaryViewModel: DiaryViewModel, foodName: String, onFoodSelectedListener: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = {
                onFoodSelectedListener()
            })
    ) {
        Text(
            text = foodName,
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
        )
    }
}
@Composable
fun SpinnerSample(
    list: List<PostType>,
    preselected: PostType,
    onSelectionChanged: (myData: PostType) -> Unit,
    modifier: Modifier
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(true) } // initial value

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = modifier.fillMaxWidth()
    ) {
        list.forEach { listEntry ->
            DropdownMenuItem(
                onClick = {
                    selected = listEntry
                    expanded = false
                    onSelectionChanged(selected)
                },
                text = {
                    Text(
                        text = listEntry.type,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(10.dp)
                    )
                },
            )
        }
    }
}