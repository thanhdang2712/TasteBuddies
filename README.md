# TasteBuddies

## Motivation
TasteBuddies aims to fill a gap in existing food applications by focusing on the dishes themselves rather than specific restaurants or locations. Our goal is to create a social platform for food enthusiasts to connect, share culinary experiences, maintain a food diary, and exchange stories with friends. TasteBuddies fosters a vibrant community where users can explore new flavors, discover favorite dishes, and engage with like-minded individuals.

## Tech Stack & Open-Source Libraries
- **Jetpack Compose:** Modern toolkit for building native Android UIs.
- **Kotlin + Coroutines + Flow:** For implementing the MVI (Model-View-Intent) architecture.
- **Dagger Hilt:** Dependency injection to manage app components.
- **Architecture Components:** Navigation and Datastore for robust app structure and state management.
- **Firebase:** Backend-as-a-Service (BaaS) providing authentication and real-time data synchronization.
- **HTTP REST API (Spoonacular API):** To fetch comprehensive food-related data.

## Implemented Features

### User Registration and Profile
- **Firebase Authentication:** Easy sign-up via email or social media.
- **Profile Creation:** Users can add a profile picture, bio, dietary restrictions, and favorite cuisines.
- **Top 3 Section:** Showcase all-time favorite dishes using a searchable database, allowing connections based on food preferences.

### Eating Diary
- **Scaffold Layout, Camera Support:** Maintain a comprehensive eating diary with posts including date, time, and meal rating or review.
- **Diary Categorization:** Entries categorized into eating out, home-cooked meals, or cravings, helping users track different dining experiences and moods.

### Discovery Feed
- **Firestore, LazyColumn:** Personalized feed highlighting friends' posts and diary updates. Users can like and comment on entries.
  
### Crave List
- **Firestore, LazyGrid Layout:** Curate a list of foods to try or recipes to experiment with, and star items once tried to track culinary exploration.
