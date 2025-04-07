//Pobieranie danych z Api/ zapasowe koktajle
package com.example.coctails.data

import com.example.coctails.model.Cocktail
import com.example.coctails.repository.CocktailRepository


object SampleData {
    // Domyślne dane, gdy API nie jest dostępne
    private val fallbackCocktails = listOf(
        Cocktail(
            id = "1",
            name = "Mojito",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg",
            ingredients = listOf(
                "40 ml białego rumu",
                "30 ml soku z limonki",
                "2 łyżeczki cukru",
                "Kilka listków mięty",
                "Woda gazowana"
            ),
            instructions = "Ugniatamy miętę z cukrem i sokiem z limonki. Dodajemy kruszony lód, rum i dopełniamy wodą gazowaną. Dekorujemy miętą."
        ),
        Cocktail(
            id = "2",
            name = "Margarita",
            imageUrl = "https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg",
            ingredients = listOf(
                "50 ml tequili",
                "20 ml Cointreau",
                "30 ml soku z limonki"
            ),
            instructions = "Wszystkie składniki wstrząsamy z lodem w shakerze. Podajemy w szklance z brzegiem obtoczonym w soli."
        )
    )

    // Koktajle z API (początkowo puste)
    private var _cocktails: List<Cocktail> = emptyList()

    // Funkcja do pobierania koktajli z API
    suspend fun refreshCocktails() {
        val repository = CocktailRepository()
        try {
            val cocktailsFromApi = repository.getPopularCocktails()
            if (cocktailsFromApi.isNotEmpty()) {
                _cocktails = cocktailsFromApi
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Getter, który zwraca dane z API lub dane zapasowe
    val cocktails: List<Cocktail>
        get() = if (_cocktails.isNotEmpty()) _cocktails else fallbackCocktails
}