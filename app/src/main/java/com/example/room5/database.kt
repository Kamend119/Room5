package com.example.room4

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "cocktails")
class Cocktails(
    var name: String,
    var glass: String,
    var build: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Entity(tableName = "ingredients",
    foreignKeys = [
        ForeignKey(entity = Cocktails::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("cocktailsId"),
            onDelete = ForeignKey.CASCADE )
    ])
class Ingredients(
    var name: String,
    var weight: Int,
    var cocktailsId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface CocktailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCocktails(cocktails: Cocktails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredients(ingredients: Ingredients)

    @Query(" SELECT * FROM cocktails")
    fun getCocktails() : MutableList<Cocktails>

    @Query(" SELECT id FROM cocktails " +
            "WHERE name = :nam")
    fun getCocktailsIdByName(nam : String) : Int

    @Query(" SELECT * FROM ingredients " +
            "WHERE cocktailsId = :numb")
    fun getIngredientsById(numb : Int) : MutableList<Ingredients>
}

@Database(
    entities = [Cocktails::class, Ingredients::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun cocktailsDao(): CocktailsDao
}
