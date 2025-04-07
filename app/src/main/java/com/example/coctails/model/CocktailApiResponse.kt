//Pobieranie danych o koktajlach z API.
//Konwersję surowych danych JSON na czytelne obiekty Kotlin używane w interfejsie użytkownika.

package com.example.coctails.model


data class CocktailApiResponse(
    val drinks: List<CocktailDto>?
)

data class CocktailDto(
    val idDrink: String,
    val strDrink: String,
    val strDrinkThumb: String,
    val strInstructions: String?,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?
)

// Funkcja rozszerzająca do konwersji CocktailDto na Cocktail
fun CocktailDto.toCocktail(): Cocktail {
    val ingredients = listOfNotNull(
        combineIngredientAndMeasure(strIngredient1, strMeasure1),
        combineIngredientAndMeasure(strIngredient2, strMeasure2),
        combineIngredientAndMeasure(strIngredient3, strMeasure3),
        combineIngredientAndMeasure(strIngredient4, strMeasure4),
        combineIngredientAndMeasure(strIngredient5, strMeasure5)
    )

    return Cocktail(
        id = idDrink,
        name = strDrink,
        imageUrl = strDrinkThumb,
        ingredients = ingredients,
        instructions = strInstructions ?: ""
    )
}

private fun combineIngredientAndMeasure(ingredient: String?, measure: String?): String? {
    if (ingredient.isNullOrBlank()) return null
    return if (measure.isNullOrBlank()) ingredient else "$measure $ingredient"
}