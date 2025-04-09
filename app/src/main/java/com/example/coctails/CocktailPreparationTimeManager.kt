package com.example.coctails

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CocktailPreparationTimeManager(private val context: Context) {

    private val timeFile = File(context.filesDir, "cocktail_times.csv")

    // Kategorie czasów przygotowania
    enum class PreparationTimeCategory {
        SHORT, MEDIUM, LONG,UNKNOWN
    }

    // Zapisz nowy czas przygotowania
    suspend fun savePreparationTime(cocktailName: String, timeInSeconds: Int) = withContext(Dispatchers.IO) {
        try {
            if (!timeFile.exists()) {
                timeFile.createNewFile()
                timeFile.appendText("Cocktail,TimeInSeconds\n")
            }

            timeFile.appendText("$cocktailName,$timeInSeconds\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Pobierz wszystkie czasy dla danego drinka
    suspend fun getPreparationTimes(cocktailName: String): List<Int> = withContext(Dispatchers.IO) {
        if (!timeFile.exists()) return@withContext emptyList()

        val times = mutableListOf<Int>()
        timeFile.readLines().drop(1).forEach { line ->
            val parts = line.split(",")
            if (parts.size >= 2 && parts[0] == cocktailName) {
                parts[1].toIntOrNull()?.let { times.add(it) }
            }
        }

        return@withContext times
    }

    // Oblicz średni czas przygotowania
    suspend fun getAveragePreparationTime(cocktailName: String): Int = withContext(Dispatchers.IO) {
        val times = getPreparationTimes(cocktailName)
        return@withContext if (times.isEmpty()) -1 else times.average().toInt()
    }

    // Określ kategorię czasu przygotowania
    suspend fun getPreparationTimeCategory(cocktailName: String): PreparationTimeCategory = withContext(Dispatchers.IO) {
        val avgTime = getAveragePreparationTime(cocktailName)

        return@withContext when {
            avgTime < 0 -> PreparationTimeCategory.UNKNOWN // Domyślnie niewiadomo
            avgTime < 60 -> PreparationTimeCategory.SHORT // Mniej niż minuta
            avgTime < 180 -> PreparationTimeCategory.MEDIUM // Mniej niż 3 minuty
            else -> PreparationTimeCategory.LONG // 3 minuty lub więcej
        }
    }

    // Pobierz sformatowany opis czasu przygotowania
    suspend fun getPreparationTimeDescription(cocktailName: String): String = withContext(Dispatchers.IO) {
        return@withContext when (getPreparationTimeCategory(cocktailName)) {
            PreparationTimeCategory.SHORT -> "Preparation time: short"
            PreparationTimeCategory.MEDIUM -> "Preparation time: medium"
            PreparationTimeCategory.LONG -> "Preparation time: long"
            PreparationTimeCategory.UNKNOWN -> "Preparation time: too few examples to determine average time"
        }
    }
}
