package com.example.coctails.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coctails.data.SampleData
import com.example.coctails.model.Cocktail
import kotlinx.coroutines.launch

class CocktailViewModel : ViewModel() {

    private val _cocktails = MutableLiveData<List<Cocktail>>()
    val cocktails: LiveData<List<Cocktail>> = _cocktails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCocktails()
    }

    private fun loadCocktails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                SampleData.refreshCocktails()
                _cocktails.value = SampleData.cocktails
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshCocktails() {
        loadCocktails()
    }
}
