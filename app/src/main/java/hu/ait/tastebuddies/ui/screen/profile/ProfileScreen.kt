package hu.ait.tastebuddies.ui.screen.profile

import android.net.Uri
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.ui.screen.diary.DiaryViewModel
import hu.ait.tastebuddies.ui.screen.diary.FoodUiState
import sh.calvin.reorderable.ReorderableRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var showFavFoodDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
        Column(modifier = Modifier.padding(contentPadding)) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                ProfileImage()
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Quang Le", fontSize = 20.sp, modifier = Modifier.paddingFromBaseline(top = 50.dp))
                    Text(text = "20 years old", fontSize = 20.sp)
                }
            }
            FavoriteFoods(
                profileViewModel,
                onFoodCardClicked = {},
                showFavFoodDialog = { showFavFoodDialog = true })
            BioDescription()
            if (showFavFoodDialog) {
                FavFoodDialog(
                    onDismissRequest = { showFavFoodDialog = false },
                    profileViewModel = profileViewModel,
                    diaryViewModel = diaryViewModel)
            }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    // ProfileScreen()
}

@Composable
fun BioDescription() {
    var bioDescription by remember { mutableStateOf("I'm an AIT student, and I love to eat!!!") }
    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
        Text("Bio", fontSize = 20.sp)
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 250.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = bioDescription,
                fontSize = 15.sp)
        }
    }
}

@Composable
fun FavoriteFoods(
    profileViewModel: ProfileViewModel,
    onFoodCardClicked: () -> Unit,
    showFavFoodDialog: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Top 3 Dishes", fontSize = 20.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..2) {
                if (profileViewModel.favFoodList[i] == null) {
                    FavFoodPlaceholder(profileViewModel, id = i, showFavFoodDialog)
                } else {
                    FavFood(profileViewModel, id = i)
                }
            }
        }
    }
}

@Composable
fun FavFood(
    profileViewModel: ProfileViewModel,
    id: Int
) {
    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(20),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.cancel),
                contentDescription = "cancel button",
                modifier = Modifier
                    .weight(0.2f)
                    .align(Alignment.End)
                    .padding(5.dp)
                    .width(40.dp)
                    .clickable(onClick = {profileViewModel.removeFoodFromList(id)}))
            Image(
                painter = painterResource(id = R.drawable.food),
                contentDescription = "fav food",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.4f)
                    .padding(10.dp))
            Text(
                text = profileViewModel.favFoodList[id]!!,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.4f)
                    .padding(10.dp))
        }

    }
}

@Composable
fun FavFoodPlaceholder(profileViewModel: ProfileViewModel, id: Int, showDialogBox: () -> Unit) {
    OutlinedButton(
        onClick = {
            profileViewModel.foodCardNum = id
            showDialogBox()
        },
        modifier = Modifier
            .size(width = 100.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(20),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray
        )
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}


@Composable
fun ProfileImage() {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberAsyncImagePainter(
        if (imageUri.value.isEmpty()) {
            R.drawable.ic_user
        } else {
            imageUri.value
        }
    )

    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "profile image",
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {},
                contentScale = ContentScale.Crop)
        }
        Text(text = "Change profile picture", fontSize = 15.sp)
    }
}

@Composable
fun FavFoodDialog(
    profileViewModel: ProfileViewModel,
    onDismissRequest: () -> Unit,
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var favFood by rememberSaveable { mutableStateOf("") }
    var foodNames by rememberSaveable { mutableStateOf(emptyList<String>()) }

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

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cancel",
                        modifier = Modifier
                            .clickable(onClick = { onDismissRequest() })
                    )
                    Text(text = "Select a Dish", fontWeight = FontWeight.Bold)
                    Icon(
                        painter = painterResource(id = R.drawable.account_circle),
                        contentDescription = "profile",
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
                OutlinedTextField(
                    value = favFood,
                    label = { Text(text = "Name of dish (e.g. pasta)...") },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    onValueChange = {
                        favFood = it
                        diaryViewModel.getFoodRecipes(
                            favFood,
                            "9d3cc85171a74f679f647ab3dc919805",
                            "10"
                        )
                    }
                )
                // Show items in LazyColumn
                if (foodNames.isEmpty()) {
                    // TODO: Display recent searches or dish recommendations
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        items(foodNames) {
                            FoodCard(
                                profileViewModel,
                                foodName = it,
                                onDismissRequest)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodCard(profileViewModel: ProfileViewModel, foodName: String, onDismissRequest: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = {
                profileViewModel.addFoodToList(foodName)
                onDismissRequest()
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

