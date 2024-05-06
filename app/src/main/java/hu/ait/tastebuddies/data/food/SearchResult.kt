package hu.ait.tastebuddies.data.food


import hu.ait.tastebuddies.data.food.Result
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    @SerialName("name")
    var name: String? = null,
    @SerialName("results")
    var results: List<Result?>? = null,
    @SerialName("totalResults")
    var totalResults: Int? = null
)