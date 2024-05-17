# TasteBuddies

## Motivation
TasteBuddies aims to fill a gap in existing food applications by focusing on the dishes themselves rather than specific restaurants or locations. Our goal is to create a social platform for food enthusiasts to connect, share culinary experiences, maintain a food diary, and exchange stories with friends. TasteBuddies fosters a vibrant community where users can explore new flavors, discover favorite dishes, and engage with like-minded individuals.

## Tech Stack & Open-Source Libraries
- 100% [Kotlin](https://kotlinlang.org/) based + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Dagger Hilt](https://dagger.dev/hilt)
- UI completely written in [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/): including: Room, Lifecycle, Navigation, Datastore.
- **HTTP REST API ([Spoonacular API](https://spoonacular.com/)):** To fetch comprehensive food-related data.

## User Registration and Profile
- **Firebase Authentication:** Easy sign-up via email or social media.
- **Profile Creation:** Users can add a profile picture, bio, dietary restrictions, and favorite cuisines.
- **Top 3 Section:** Showcase all-time favorite dishes using a searchable database, allowing connections based on food preferences.

<p align="center">
<img src="https://user-images.githubusercontent.com/8813304/209520971-4a896a72-4963-4d21-b94e-775d79ab5a7b.jpg](https://github.com/QUANGLEA/TasteBuddies/blob/main/screenshots/user_register.gif" width="17%"/>
<img src="https://user-images.githubusercontent.com/8813304/209520972-4d44094f-11f8-4fa5-ab95-232beaa18fec.jpg](https://github.com/QUANGLEA/TasteBuddies/blob/main/screenshots/profile.gif" width="17%"/>
</p>

### Eating Diary
- **Scaffold Layout, Camera Support:** Maintain a comprehensive eating diary with posts including date, time, and meal rating or review.
- **Diary Categorization:** Entries categorized into eating out, home-cooked meals, or cravings, helping users track different dining experiences and moods.

### Discovery Feed
- **Firestore, LazyColumn:** Personalized feed highlighting friends' posts and diary updates. Users can like and comment on entries.
  
### Crave List
- **Firestore, LazyGrid Layout:** Curate a list of foods to try or recipes to experiment with, and star items once tried to track culinary exploration.
