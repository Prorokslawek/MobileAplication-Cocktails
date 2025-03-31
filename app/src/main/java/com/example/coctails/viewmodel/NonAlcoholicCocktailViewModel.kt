
package com.example.coctails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coctails.model.Cocktail
import com.example.coctails.repository.CocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NonAlcoholicCocktailViewModel : ViewModel() {
    private val repository = CocktailRepository()

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun refreshCocktails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Pobierz listę drinków bezalkoholowych
                val nonAlcoholicCocktails = repository.getNonAlcoholicCocktails()
                _cocktails.value = nonAlcoholicCocktails
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
