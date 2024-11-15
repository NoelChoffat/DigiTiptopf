import android.content.Context
import com.example.tiptopf.Recipe
import java.io.OutputStreamWriter

class RecipeStorage(private val context: Context) {
    private val fileName = "recipes.json"
    private val recipeManager = RecipeManager()

    fun saveRecipes(recipes: List<Recipe>) {
        val data = recipeManager.recipeListToJson(recipes)
        val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.use { it.write(data) }
    }

    fun loadRecipes(): List<Recipe> {
        return try {
            context.openFileInput(fileName).use { input ->
                val data = input.bufferedReader().readText()
                recipeManager.jsonToRecipeList(data)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}