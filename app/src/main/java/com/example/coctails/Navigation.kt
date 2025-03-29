package com.example.coctails

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


sealed class Screen(val route: String) {
    object CocktailList : Screen("cocktailList")
    object CocktailDetail : Screen("cocktailDetail/{cocktailId}") {
        fun createRoute(cocktailId: String) = "cocktailDetail/$cocktailId"
    }
}

@Composable
fun CocktailNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CocktailList.route
    ) {
        composable(Screen.CocktailList.route) {
            CocktailListScreen(
                onCocktailClick = { cocktailId ->
                    navController.navigate(Screen.CocktailDetail.createRoute(cocktailId))
                }
            )
        }

        composable(
            Screen.CocktailDetail.route,
            arguments = listOf(navArgument("cocktailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("cocktailId") ?: ""
            CocktailDetailScreen(
                cocktailId = cocktailId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}