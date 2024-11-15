import com.example.tiptopf.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeManager {
    private val gson = Gson()

    fun recipeListToJson(recipes: List<Recipe>): String {
        return gson.toJson(recipes)
    }

    fun jsonToRecipeList(json: String): List<Recipe> {
        val type = object : TypeToken<List<Recipe>>(){}.type
        return gson.fromJson(json, type)
    }
}