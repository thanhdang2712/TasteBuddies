package hu.ait.tastebuddies.ui.screen.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import hu.ait.tastebuddies.R
import hu.ait.tastebuddies.data.DataManager
import hu.ait.tastebuddies.data.Post
import hu.ait.tastebuddies.ui.screen.diary.StarRatingBar
import hu.ait.tastebuddies.utils.parseMonthDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    discoveryViewModel: DiscoveryViewModel = viewModel()
) {
    val postListState = discoveryViewModel.postsList().collectAsState(
        initial = DiscoveryUIState.Init)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discovery") },
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
            when (postListState.value) {
                DiscoveryUIState.Init -> {
                    Text(text = "Initializing..")
                }
                DiscoveryUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is DiscoveryUIState.Success -> {
                    // show messages in a list...
                    LazyColumn() {
                        items((postListState.value as DiscoveryUIState.Success).postList){
                            PostCard(
                                post = it.post,
                                onUpdateLike = fun(likes: List<String>): Unit { discoveryViewModel.updateLikes(it.postId, likes)},
                                onRemoveItem = {
                                    discoveryViewModel.deletePost(it.postId)
                                },
                                currentUserId = FirebaseAuth.getInstance().uid!!
                            )
                        }
                    }

                }

                is DiscoveryUIState.Error -> {
                    // show error...
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post = Post(),
    onUpdateLike: (List<String>) -> Unit,
    onRemoveItem: () -> Unit = {},
    currentUserId: String = ""
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 40.dp
        ),
        modifier = Modifier.padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${post.authorName} ")
                            }
                            append(post.postType.type)
                        },
                        fontSize = 15.sp
                    )
                    Text(
                        text = post.title,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                    StarRatingBar(maxStars = 5, rating = 4f, onRatingChanged = {}, size = 7)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        AsyncImage(
                            model = post.imgUrl,
                            contentDescription = "food image",
                            modifier = Modifier.size(150.dp))
                        Text(post.body, fontSize = 15.sp)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(if (DataManager.email in post.likes) R.drawable.red_heart else R.drawable.black_heart),
                            contentDescription = "like",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = {
                                    onUpdateLike(post.likes)
                                })
                        )
                        Icon(
                            painter = painterResource(R.drawable.comment),
                            contentDescription = "comment",
                            modifier = Modifier.size(20.dp))
                    }
                    Text("${post.likes.size} likes", fontWeight = FontWeight.Bold)
                }
                Text(text = parseMonthDay(post.date), fontSize = 15.sp)
            }
        }
    }
}