package hu.ait.tastebuddies.network

import hu.ait.tastebuddies.data.food.FoodRecipes
import retrofit2.http.GET
import retrofit2.http.Query

/*
HOST: https://api.spoonacular.com/
PATH: food/search
QUERY PARAMS:
?
apiKey=eeddaeccb08a4d10a7566bc34480a8e6
&
query=apple
&
number=2
 */
interface FoodAPI {
    @GET("food/search")
    suspend fun getFoodRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: String
    ): FoodRecipes
}