package com.example.coctails

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coctails.viewmodel.CocktailListScreen
import com.example.coctails.viewmodel.ThemeViewModel
import com.example.coctails.viewmodel.TimerViewModel

sealed class Screen(val route: String) {
    object CocktailList : Screen("cocktailList")
    object CocktailDetail : Screen("cocktailDetail/{cocktailId}") {
        fun createRoute(cocktailId: String) = "cocktailDetail/$cocktailId"
    }
}

@Composable
fun CocktailNavHost(timerViewModel: TimerViewModel,themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CocktailList.route
    ) {
        composable(Screen.CocktailList.route) {
            CocktailListScreen(
                onCocktailClick = { cocktailId ->
                    // Usuń wszelkie parametry popUpTo, aby zachować stos nawigacji
                    navController.navigate(Screen.CocktailDetail.createRoute(cocktailId))
                },
                themeViewModel=themeViewModel
            )
        }

        composable(
            Screen.CocktailDetail.route,
            arguments = listOf(navArgument("cocktailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("cocktailId") ?: ""
            CocktailDetailScreen(
                cocktailId = cocktailId,
                // Użyj prostego navigateUp() zamiast popBackStack z parametrami
                onNavigateBack = { navController.navigateUp() },
                timerViewModel = timerViewModel
            )
        }
    }
}
