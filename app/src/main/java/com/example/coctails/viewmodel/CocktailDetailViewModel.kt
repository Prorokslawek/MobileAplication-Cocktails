package com.example.coctails.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coctails.CocktailPreparationTimeManager
import com.example.coctails.model.Cocktail
import com.example.coctails.repository.CocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailDetailViewModel(application: Application) : AndroidViewModel(application) {
    // Istniejące pola
    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Nowe pole dla informacji o czasie przygotowania
    private val _preparationTimeInfo = MutableStateFlow("")
    val preparationTimeInfo: StateFlow<String> = _preparationTimeInfo

    private val preparationTimeManager = CocktailPreparationTimeManager(application)

    fun loadCocktailDetails(cocktailId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val repository = CocktailRepository()
                val cocktailDetails = repository.getCocktailDetails(cocktailId)
                _cocktail.value = cocktailDetails

                // Pobierz informację o czasie przygotowania
                if (cocktailDetails != null) {
                    _preparationTimeInfo.value = preparationTimeManager.getPreparationTimeDescription(cocktailDetails.name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
