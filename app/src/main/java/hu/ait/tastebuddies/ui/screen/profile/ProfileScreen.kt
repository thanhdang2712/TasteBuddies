package hu.ait.tastebuddies.ui.screen.profile

import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import hu.ait.tastebuddies.R
import sh.calvin.reorderable.ReorderableRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
) {

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
            FavoriteFoods() {}
            BioDescription()
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    ProfileScreen()
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
    onFoodImageClick: () -> Unit
) {
    var favoriteFoodList = remember {
        mutableStateListOf<String>()
    }
    favoriteFoodList.add(0, "Pho")
    favoriteFoodList.add(1, "Banh Mi")
    favoriteFoodList.add(2, "")
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Top 3 Foods", fontSize = 20.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..2) {
                if (favoriteFoodList[i].isEmpty()) {
                    FavFoodPlaceholder()
                } else {
                    FavFood()
                }
            }
        }
    }
}

@Composable
fun FavFood() {
    OutlinedButton(
        onClick = {},
        modifier = Modifier
            .size(width = 100.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(20),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray
        )
    ) {
        Image(painter = painterResource(id = R.drawable.food), contentDescription = "fav food")
    }
}

@Composable
fun FavFoodPlaceholder() {
    OutlinedButton(
        onClick = {},
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