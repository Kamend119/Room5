package com.example.room5

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.room4.AppDataBase
import com.example.room4.Cocktails
import com.example.room4.Ingredients
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Room.databaseBuilder(
                LocalContext.current,
                AppDataBase::class.java, "AppDataBase"
            ).allowMainThreadQueries().build()

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "Main") {
                composable("Main") { MainScreen( navController, db) }
                composable("Add") { AddScreen( navController, db) }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreen(navController: NavHostController, db: AppDataBase) {
    val cocktails = db.cocktailsDao().getCocktails()

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Коктейли", fontSize = 40.sp)
        }

        LazyColumn(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            items(cocktails.size)
            {  index ->
                var open by remember{ mutableStateOf(false) }

                Column(
                    Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .border(3.dp, Color.Black)
                        .clickable { open = !open }
                        .padding(8.dp)
                ) {
                    if (open){
                        Text(
                            cocktails[index].name,
                            Modifier
                                .fillMaxSize()
                                .padding(bottom = 10.dp),
                            fontSize = 35.sp
                        )

                        Text(
                            text = "${cocktails[index].glass} - ${cocktails[index].build}",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column {
                            val ingredients = db.cocktailsDao().getIngredientsById(cocktails[index].id)

                            for (i in 0..<ingredients.size)
                                Text(text = "${ingredients[i].name} - ${ingredients[i].weight}")
                        }
                    }
                    else
                        Text(
                            cocktails[index].name,
                            Modifier.fillMaxSize(),
                            fontSize = 35.sp
                        )
                }
            }
        }

        Column(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("Add") } ) {
                Text("Добавить", fontSize = 30.sp)
            }
        }

    }
}

//@Preview(showBackground = true, showSystemUi = true)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddScreen(navController: NavHostController, db: AppDataBase) {
    val ctx = LocalContext.current
    var name by remember { mutableStateOf("") }
    var glass by remember { mutableStateOf("") }
    var build by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(2) }
    val ingredients by remember { mutableStateOf(mutableListOf<String>()) }
    val weights by remember { mutableStateOf(mutableListOf<Int>()) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = { count++ }, shape = CircleShape) {
                Icon(Icons.Default.AddCircle, "", Modifier.size(50.dp))
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ArrowBack, "",
                        Modifier
                            .clickable { navController.popBackStack() }
                            .size(35.dp))

                    Text("Добавить коктейль", fontSize = 40.sp)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") }
                )

                OutlinedTextField(
                    value = glass,
                    onValueChange = { glass = it },
                    label = { Text("Вид бокала") }
                )

                OutlinedTextField(
                    value = build,
                    onValueChange = { build = it },
                    label = { Text("Вид приготовления") }
                )
            }

            LazyColumn(
                Modifier
                    .weight(6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(count) {
                    Row(
                        Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var ingred by remember { mutableStateOf("") }
                        var mg by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = ingred,
                            onValueChange = { ingred = it },
                            label = { Text(text = "Ингридиент ${it + 1}") }
                        )

                        OutlinedTextField(
                            value = mg,
                            onValueChange = { mg = it },
                            label = { Text(text = "мг") }
                        )

                        ingredients.add(ingred)
                        weights.add(mg.toIntOrNull() ?: 0)

                    }
                }
            }

            Column(
                Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    if (name == "") Toast.makeText( ctx ,"Название не должно быть пустым", Toast.LENGTH_SHORT).show()
                    else if (glass == "") Toast.makeText( ctx ,"Тип стекла не должен быть пустым", Toast.LENGTH_SHORT).show()
                    else if (build == "") Toast.makeText( ctx ,"Тип приготовления не должен быть пустым", Toast.LENGTH_SHORT).show()
                    else {
                        db.cocktailsDao().insertCocktails(Cocktails(name, glass, build))

                        for (i in 0..<count)
                            if (ingredients[i] != "")
                                db.cocktailsDao().insertIngredients(Ingredients(ingredients[i], weights[i], db.cocktailsDao().getCocktailsIdByName(name)))

                        navController.popBackStack()
                    }
                }) {
                    Text("Добавить", fontSize = 30.sp)
                }
            }
        }
    }
}