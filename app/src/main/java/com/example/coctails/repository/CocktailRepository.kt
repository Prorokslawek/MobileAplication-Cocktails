package com.example.coctails.repository


import com.example.coctails.api.CocktailApi
import com.example.coctails.model.Cocktail
import com.example.coctails.model.toCocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CocktailRepository {

    // Pobierz popularne koktajle - tu użyjemy wyszukiwania kilku popularnych nazw
    suspend fun getPopularCocktails(): List<Cocktail> = withContext(Dispatchers.IO) {
        val popularQueries = listOf("Margarita", "Mojito", "Martini")
        val cocktails = mutableListOf<Cocktail>()

        popularQueries.forEach { query ->
            try {
                val response = CocktailApi.service.searchCocktails(query)
                response.drinks?.map { it.toCocktail() }?.let { cocktails.addAll(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return@withContext cocktails
    }


    //Pobierz koktalje bezalkoholowe
    suspend fun getNonAlcoholicCocktails(): List<Cocktail> {
        val response = CocktailApi.service.filterByAlcoholic("Non_Alcoholic")
        // Zwróć podstawowe informacje o drinkach
        return response.drinks?.map {
            Cocktail(
                id = it.idDrink ?: "",
                name = it.strDrink ?: "",
                imageUrl = it.strDrinkThumb ?: "",
                ingredients = emptyList(),
                instructions = ""
            )
        } ?: emptyList()
    }

    // Dodaj metodę do pobierania szczegółów drinka po ID
    suspend fun getCocktailDetails(id: String): Cocktail? {
        try {
            val response = CocktailApi.service.lookupCocktail(id)
            return response.drinks?.firstOrNull()?.toCocktail()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}