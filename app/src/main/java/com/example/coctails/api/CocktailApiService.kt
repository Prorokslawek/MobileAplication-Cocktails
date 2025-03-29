package com.example.coctails.api


import com.example.coctails.model.CocktailApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {
    @GET("api/json/v1/1/search.php")
    suspend fun searchCocktails(@Query("s") searchQuery: String): CocktailApiResponse

    @GET("api/json/v1/1/random.php")
    suspend fun getRandomCocktail(): CocktailApiResponse

    @GET("api/json/v1/1/filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): CocktailApiResponse
}

object CocktailApi {
    private const val BASE_URL = "https://www.thecocktaildb.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: CocktailApiService = retrofit.create(CocktailApiService::class.java)
}