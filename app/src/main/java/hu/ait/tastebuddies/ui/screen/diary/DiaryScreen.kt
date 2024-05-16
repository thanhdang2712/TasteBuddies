package hu.ait.tastebuddies.ui.screen.diary

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.items
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.PostType
import hu.ait.tastebuddies.data.food.FoodItem
import hu.ait.tastebuddies.data.food.FoodRecipes
import hu.ait.tastebuddies.ui.screen.profile.FoodCard
import hu.ait.tastebuddies.ui.screen.profile.ProfileViewModel
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.accompanist.permissions.shouldShowRationale
import hu.ait.tastebuddies.ui.navigation.MainNavigation

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
    var foodNames by rememberSaveable { mutableStateOf(emptyList<FoodItem>()) }
    var showDiaryEntryScreen by rememberSaveable{ mutableStateOf(false) }
    val allPostTypes by rememberSaveable{ mutableStateOf(listOf(PostType.ATE, PostType.MADE, PostType.CRAVE)) }
    var selected by rememberSaveable { mutableStateOf(PostType.ATE) }
    var diaryNote by rememberSaveable{ mutableStateOf(DiaryNote()) }
    val currentDate by rememberSaveable { mutableStateOf(LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("dd MMM yyyy"))) }

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
        // TODO: Display the post type icon too
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
                                    Icon(
                                        painter = painterResource(id = listEntry.icon),
                                        contentDescription = when (listEntry) {
                                            PostType.ATE -> "I ate out"
                                            PostType.MADE -> "I made this meal myself"
                                            PostType.CRAVE -> "I really want to eat this"
                                        }
                                    )
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
                            Box(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                                Icon(
                                    painter = painterResource(id = postType.icon),
                                    contentDescription = when (postType) {
                                        PostType.ATE -> "I ate out"
                                        PostType.MADE -> "I made this meal myself"
                                        PostType.CRAVE -> "I really want to eat this"
                                    },
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Text(
                                text = "Add a Diary Entry",
                            )
                            OutlinedTextField(
                                value = postTitle,
                                label = { 
                                    Text(text = when (postType) {
                                        PostType.ATE -> "Today I ate..."
                                        PostType.MADE -> "Today I made..."
                                        PostType.CRAVE -> "Today I crave..."
                                    })
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = "Search Icon"
                                    )
                                },
                                onValueChange = {
                                    postTitle = it
                                    diaryViewModel.getFoodRecipes(
                                        postTitle,
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
                                                postTitle = it.name
                                                diaryNote = DiaryNote(date = currentDate, noteType = postType, title = postTitle, body = "", rating = 0f, image = it.image)
                                                diaryViewModel.diaryNotes.add(diaryNote)
                                                showDialog = false
                                                showDiaryEntryScreen = true
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
        }
        if (showDiaryEntryScreen) {
            DiaryEntryScreen(postTitle, diaryViewModel, diaryNote)
        }
    }
}



@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun DiaryEntryScreen(
    postTitle: String,
    diaryViewModel: DiaryViewModel,
    diaryNote: DiaryNote
) {
    var noteBody by rememberSaveable { mutableStateOf("") }
    var noteRating by rememberSaveable { mutableStateOf(0.0f) }

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    if (cameraPermissionState.status.isGranted) {
                        //if we have the camera permission then show a "Take photo" button
                        Button(onClick = {
                            // this code launches the camera
                            val uri = ComposeFileProvider.getImageUri(context)
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }) {
                            Text(text = "Take photo")
                        }
                    } else { // if we do not have the Camera permission yet, we need to ask..
                        val permissionText = if (cameraPermissionState.status.shouldShowRationale) {
                            "Please reconsider giving the camera persmission " +
                                    "it is needed if you want to take photo for the message"
                        } else {
                            "Give permission for using photos with items"
                        }
                        Text(text = permissionText)
                        Button(onClick = {
                            // this code pops up a permission request dialog
                            cameraPermissionState.launchPermissionRequest()
                        }) {
                            Text(text = "Request permission")
                        }
                    }

                    Button(onClick = {
                        if (imageUri == null && diaryNote.body!= null) {
                            diaryViewModel.uploadDiaryPost(diaryNote.title!!, diaryNote.body!!, diaryNote.image)
                        } else {
                            diaryViewModel.uploadPostImage(
                                context.contentResolver,
                                imageUri!!, // this is the image file location locally on the phone
                                diaryNote.title!!,
                                diaryNote.body!!
                            )
                        }
                    }) {
                        Text(text = "Upload")
                    }
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 56.dp).padding(contentPadding).padding(25.dp),
        ) {
            Text(
                text = "Date: ${diaryNote.date}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                if (!diaryNote.title.isNullOrEmpty()) {
                    Text(
                        text = diaryNote.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Icon(
                    painter = painterResource(id = diaryNote.noteType!!.icon),
                    contentDescription = when ( diaryNote.noteType!!) {
                        PostType.ATE -> "I ate out"
                        PostType.MADE -> "I made this meal myself"
                        PostType.CRAVE -> "I really want to eat this"
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            StarRatingBar(
                maxStars = 5,
                rating = noteRating,
                onRatingChanged = {
                    diaryNote.rating = it
                    noteRating = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = noteBody,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Add a diary entry...") },
                onValueChange = {
                    diaryNote.body = it
                    noteBody = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (hasImage && imageUri != null) {
                AsyncImage(model = imageUri,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.height(8.dp))

            when (diaryViewModel.diaryUiState) {
                is DiaryUiState.Init -> {}
                is DiaryUiState.LoadingPostUpload -> CircularProgressIndicator()
                is DiaryUiState.PostUploadSuccess -> {
                    Text(text = "Diary Post uploaded")
                    diaryViewModel.addDiaryNote(diaryNote)
                    MyDiaryScreen(diaryViewModel)
                }
                is DiaryUiState.ErrorDuringPostUpload -> {
                    Text(
                        text = "Error: ${ (diaryViewModel.diaryUiState as
                                DiaryUiState.ErrorDuringPostUpload).error}"
                    )
                }
                is DiaryUiState.LoadingImageUpload -> CircularProgressIndicator()
                is DiaryUiState.ImageUploadSuccess -> {
                    Text(text = "Image uploaded, starting post upload.")
                }
                is DiaryUiState.ErrorDuringImageUpload -> Text(
                    text = "${(diaryViewModel.diaryUiState as
                            DiaryUiState.ErrorDuringImageUpload).error}")
            }
        }
    }

}

@Composable
fun MyDiaryScreen(
    diaryViewModel: DiaryViewModel
) {
    LazyColumn() {
        items(diaryViewModel.getAllDiaryNotes()) {
            MyDiaryEntryCard(it)
        }
    }

}

@Composable
fun MyDiaryEntryCard(
    diaryNote: DiaryNote
) {
    val (day, month, year) = diaryNote.date!!.split(" ")
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day ?: "",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = month ?: "", style = MaterialTheme.typography.bodyLarge)
        Text(text = year, style = MaterialTheme.typography.bodyLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (diaryNote.noteType != null) {
                Image(
                    painter = painterResource(diaryNote.noteType!!.icon),
                    contentDescription = "Post type icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (!diaryNote.title.isNullOrEmpty()) {
                Text(
                    text = diaryNote.title ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!diaryNote.body.isNullOrEmpty()) {
                Text(text = diaryNote.body ?: "", style = MaterialTheme.typography.bodyLarge)
            }
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

class ComposeFileProvider : FileProvider(
    hu.ait.tastebuddies.R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}