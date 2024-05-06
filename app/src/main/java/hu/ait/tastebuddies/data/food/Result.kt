package hu.ait.tastebuddies.data.food


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("content")
    var content: String? = null,
    @SerialName("id")
    var id: Int? = null,
    @SerialName("image")
    var image: String? = null,
    @SerialName("link")
    var link: String? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("relevance")
    var relevance: Double? = null,
    @SerialName("type")
    var type: String? = null
)