package com.example.tiptopf

import RecipeStorage
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiptopf.ui.theme.TiptopfTheme
import java.util.UUID


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val recipeStorage = RecipeStorage(this)
        setContent {
            TiptopfTheme {
                RecipeApp(recipeStorage)
                            /*
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.CreateView, builder = {
                    composable(Routes.CreateView) {
                        RecipeCreateView(navController)
                    }
                })

                 */
            }


                }
            }
        }


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeApp(recipeStorage: RecipeStorage) {
    var recipes by remember { mutableStateOf(recipeStorage.loadRecipes()) } // Load recipe list
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) } // Hold the selected recipe
    var showAddRecipe by remember { mutableStateOf(false) } // Control AddRecipeComposable visibility
    var showUpdateRecipe by remember { mutableStateOf(false) } // Control UpdateRecipeComposable visibility

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddRecipe = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        },
        content = {
            if (showAddRecipe) {
                AddRecipeComposable(onAddRecipe = { newRecipe ->
                    // Add the new recipe to the existing list
                    recipes = recipes + newRecipe
                    // Save the updated list
                    recipeStorage.saveRecipes(recipes)

                    showAddRecipe = false // Hide the AddRecipeComposable
                })
            } else if (showUpdateRecipe) {
                selectedRecipe?.let {
                    UpdateRecipeComposable(existingRecipe = it, onUpdateRecipe = { updatedRecipe ->
                        // Update recipies list
                        recipes = recipes.map { recipe ->
                            if (recipe.id == updatedRecipe.id) updatedRecipe else recipe
                        }
                        // Save the updated list
                        recipeStorage.saveRecipes(recipes)

                        showUpdateRecipe = false
                    })
                }
            } else {
                // ListView of recipes
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    recipes.forEach { recipe ->
                        item {
                            // Adding click interaction on Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        selectedRecipe = recipe
                                        showUpdateRecipe = true
                                    }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Recipe: ${recipe.title}")
                                    Text("Ingredients: ${recipe.ingredients}")
                                    Text("Preparation: ${recipe.preparation}")
                                    Button(onClick = {
                                        // for updting the list of recipes
                                        recipes = recipes.filter { it.id != recipe.id }
                                        // save the updated list
                                        recipeStorage.saveRecipes(recipes)
                                    }) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun UpdateRecipeComposable(existingRecipe: Recipe, onUpdateRecipe: (Recipe) -> Unit) {
    var title by remember { mutableStateOf(existingRecipe.title) }
    var ingredients by remember { mutableStateOf(existingRecipe.ingredients) }
    var preparation by remember { mutableStateOf(existingRecipe.preparation) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Update Recipe", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients") },
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = preparation,
            onValueChange = { preparation = it },
            label = { Text("Procedure") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                val updatedRecipe = existingRecipe.copy(
                    title = title,
                    ingredients = ingredients,
                    preparation = preparation
                )
                onUpdateRecipe(updatedRecipe)
            },
            modifier = Modifier.padding(top = 16.dp).align(Alignment.End)
        ) {
            Text("Update Recipe")
        }
    }
}


@Composable
fun AddRecipeComposable(onAddRecipe: (Recipe) -> Unit) {
    var title by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var preparation by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add Recipe", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients") },
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = preparation,
            onValueChange = { preparation = it },
            label = { Text("Preparation") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                val newRecipe = Recipe(
                    id = UUID.randomUUID(),
                    title = title,
                    ingredients = ingredients,
                    preparation = preparation
                )
                onAddRecipe(newRecipe)
            },
            modifier = Modifier.padding(top = 16.dp).align(Alignment.End)
        ) {
            Text("Add Recipe")
        }
    }
}


@Composable
fun CenteredCard(imagePainter: Painter, title: String) {
    Box(
        modifier = Modifier
            .size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Card() {
            Column {
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TiptopfTheme {
        Greeting("Android")
    }
}