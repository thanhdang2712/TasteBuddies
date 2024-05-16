package hu.ait.tastebuddies.ui.screen.cravelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.food.FoodItem
import hu.ait.tastebuddies.ui.screen.diary.DiaryEntryScreen
import hu.ait.tastebuddies.ui.screen.diary.DiaryViewModel
import hu.ait.tastebuddies.ui.screen.diary.FoodSearchCard
import hu.ait.tastebuddies.ui.screen.diary.FoodUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CravelistScreen(
    cravelistViewModel: CravelistViewModel = viewModel(),
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    var foodQuery by rememberSaveable { mutableStateOf("") }
    var foodNames by rememberSaveable { mutableStateOf(emptyList<FoodItem>()) }
    var showCravelistGridScreen by rememberSaveable{ mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My CraveList") },
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Add a dish you're craving for!",
                            )
                            OutlinedTextField(
                                value = foodQuery,
                                label = { Text(text = "Today I crave...") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = "Search Icon"
                                    )
                                },
                                onValueChange = {
                                    foodQuery = it
                                    diaryViewModel.getFoodRecipes(
                                        foodQuery,
                                        "9d3cc85171a74f679f647ab3dc919805",
                                        "10"
                                    )
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
                                                foodQuery = it.name
                                                showDialog = false
                                                showCravelistGridScreen = true

                                                cravelistViewModel.addFoodItem(
                                                    it
                                                )
                                            })
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
            if (showCravelistGridScreen) {
                CravelistGridScreen(cravelistViewModel, modifier)
            }
        }
    }
}
@Composable
fun CravelistGridScreen(
    cravelistViewModel: CravelistViewModel,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.testTag("plant_list")
            .imePadding(),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 24.dp
        )
    ) {
        items(
            cravelistViewModel.getAllFood()
        ) { craveItem ->
            CraveItemCard(craveItem = craveItem)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CraveItemCard(
    craveItem: FoodItem
) {
    Card(
        onClick = { },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 24.dp)
            .shadow(elevation = 26.dp, shape = RoundedCornerShape(4.dp))
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = craveItem.image,
                    contentDescription = "crave item image",
                    modifier = Modifier.fillMaxSize() ,
                    contentScale = ContentScale.Crop
                )
                BookMarkButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    backgroundColor = colorResource(id = R.color.black_alpha)
                )
            }
            Text(
                text = craveItem.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCraveItemCard() {
    val sampleFoodItem = FoodItem(2, "Banh Mi", "https://images.app.goo.gl/PjuMh4pYkk3ksSDo9")
    CraveItemCard(craveItem = sampleFoodItem)
}

@Composable
fun BookMarkButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    var selected by rememberSaveable { mutableStateOf(false)}
    val icon = if (selected) Icons.Filled.Star else Icons.Outlined.Star
    val iconTintColor = if (selected) Color(0xFFFFC700) else Color(0xFFCCCCCC)
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        modifier = modifier
            .requiredSize(36.dp, 36.dp)
            .clickable {
                selected = !selected
            }
    ) {
        Icon(
            imageVector = icon,
            tint = iconTintColor,
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
        )
    }
}

@Composable
@Preview
fun previewBookMarkButtonClicked() {
    BookMarkButton(backgroundColor = Color.Black)
}
