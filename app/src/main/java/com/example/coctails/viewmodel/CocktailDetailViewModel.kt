package com.example.coctails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coctails.model.Cocktail
import com.example.coctails.repository.CocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailDetailViewModel : ViewModel() {
    private val repository = CocktailRepository()

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCocktailDetails(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cocktailDetails = repository.getCocktailDetails(id)
                _cocktail.value = cocktailDetails
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
