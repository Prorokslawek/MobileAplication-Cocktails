package com.example.coctails.model

data class Cocktail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: String
)