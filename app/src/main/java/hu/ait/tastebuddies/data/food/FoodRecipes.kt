package hu.ait.tastebuddies.data.food


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodRecipes(
    @SerialName("limit")
    var limit: Int? = null,
    @SerialName("offset")
    var offset: Int? = null,
    @SerialName("query")
    var query: String? = null,
    @SerialName("searchResults")
    var searchResults: List<SearchResult?>? = null,
    @SerialName("totalResults")
    var totalResults: Int? = null
)