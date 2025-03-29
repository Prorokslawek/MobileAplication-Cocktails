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

    // Pobierz koktajl po ID
    suspend fun getCocktailById(id: String): Cocktail? = withContext(Dispatchers.IO) {
        try {
            val response = CocktailApi.service.searchCocktails(id)
            return@withContext response.drinks?.firstOrNull()?.toCocktail()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    // Pobierz losowy koktajl
    suspend fun getRandomCocktail(): Cocktail? = withContext(Dispatchers.IO) {
        try {
            val response = CocktailApi.service.getRandomCocktail()
            return@withContext response.drinks?.firstOrNull()?.toCocktail()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}