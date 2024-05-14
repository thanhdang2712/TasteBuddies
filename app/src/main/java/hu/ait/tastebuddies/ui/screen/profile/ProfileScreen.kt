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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.food.FoodItem
import hu.ait.tastebuddies.ui.screen.diary.DiaryViewModel
import hu.ait.tastebuddies.ui.screen.diary.FoodUiState
import hu.ait.tastebuddies.ui.screen.register.RegisterUiState
import org.jetbrains.annotations.Async
import sh.calvin.reorderable.ReorderableRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var showFavFoodDialog by rememberSaveable { mutableStateOf(false) }
    profileViewModel.initializeProfile()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            when (profileViewModel.profileUiState) {
                is ProfileUiState.Init -> {}
                is ProfileUiState.Loading -> CircularProgressIndicator()
                is ProfileUiState.Success -> {
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
                            Text(text = profileViewModel.currentUser!!.name, fontSize = 20.sp, modifier = Modifier.paddingFromBaseline(top = 50.dp))
                            Text(text = "${profileViewModel.currentUser!!.age} years old", fontSize = 20.sp)
                        }
                    }
                    FavoriteFoods(
                        profileViewModel,
                        onRemoveFoodItem = fun(id: Int): Unit { profileViewModel.removeFoodFromList(id)},
                        showFavFoodDialog = {
                            showFavFoodDialog = true
                        })
                    profileViewModel.currentUser?.bio?.let { BioDescription(it) }
                    if (showFavFoodDialog) {
                        FavFoodDialog(
                            onDismissRequest = { showFavFoodDialog = false },
                            profileViewModel = profileViewModel,
                            diaryViewModel = diaryViewModel)
                    }
                }
                is ProfileUiState.Error -> Text(
                    text = "Error: ${(profileViewModel.profileUiState as ProfileUiState.Error).errorMsg}"
                )
            }

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
fun BioDescription(bioText: String) {
    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
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
                text = bioText,
                fontSize = 15.sp)
        }
    }
}

@Composable
fun FavoriteFoods(
    profileViewModel: ProfileViewModel,
    onRemoveFoodItem: (Int) -> Unit,
    showFavFoodDialog: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Top 3 Dishes", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (i in 0..2) {
                if (profileViewModel.favFoodList[i] == null) {
                    FavFoodPlaceholder(profileViewModel, id = i, showFavFoodDialog)
                } else {
                    FavFood(
                        profileViewModel,
                        onRemoveFoodItem = { onRemoveFoodItem(i) },
                        id = i)
                }
            }
        }
    }
}

@Composable
fun FavFood(
    profileViewModel: ProfileViewModel,
    onRemoveFoodItem: () -> Unit,
    id: Int
) {
    var favFoodCardState by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 200.dp),
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
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
                    .clickable(onClick = {
                        onRemoveFoodItem()
                        favFoodCardState = !favFoodCardState
                    }))
            AsyncImage(
                model = profileViewModel.favFoodList[id]!!.image,
                contentDescription = "fav food",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.4f)
                    .padding(10.dp))
            Text(
                text = profileViewModel.favFoodList[id]!!.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.4f)
                    .padding(10.dp))
        }

    }
}

@Composable
fun FavFoodPlaceholder(profileViewModel: ProfileViewModel, id: Int, showDialogBox: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 200.dp)
            .clickable(
                onClick = {
                    profileViewModel.foodCardNum = id
                    showDialogBox()
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(20),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun FavFoodDialog(
    profileViewModel: ProfileViewModel,
    onDismissRequest: () -> Unit,
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    var favFood by rememberSaveable { mutableStateOf("") }
    var foodNames by rememberSaveable { mutableStateOf(emptyList<FoodItem>()) }


    when (diaryViewModel.foodUiState) {
        is FoodUiState.Init -> {}
        is FoodUiState.Loading -> CircularProgressIndicator()
        is FoodUiState.Success -> {
            foodNames =
                diaryViewModel.getFoodNames((diaryViewModel.foodUiState as FoodUiState.Success).foodRecipes)
        }
        is FoodUiState.Error -> Text(
            text = "Error: " +
                    (diaryViewModel.foodUiState as FoodUiState.Error).errorMsg
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
                            "f8aaf2cb54144b4f85fd846c61a23cd9",
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
                                foodItem = it,
                                onDismissRequest)
                        }
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

@Composable
fun FoodCard(profileViewModel: ProfileViewModel, foodItem: FoodItem, onDismissRequest: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = {
                profileViewModel.addFoodToList(foodItem)
                onDismissRequest()
            })
    ) {
        Text(
            text = foodItem.name,
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
        )
    }
}

