package com.example.coctails

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coctails.model.MenuItem
import com.example.coctails.viewmodel.ThemeViewModel
import com.example.coctails.viewmodel.TimerViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.coctails.viewmodel.CocktailListScreen
import com.example.coctails.viewmodel.CocktailListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(timerViewModel: TimerViewModel, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // Współdzielony ViewModel dla listy koktajli
    val cocktailListViewModel: CocktailListViewModel = viewModel()

    // Obserwuj zmiany wybranej zakładki
    val selectedTabIndex by cocktailListViewModel.selectedTabIndex.collectAsStateWithLifecycle()

    // Mapuj indeks zakładki na id elementu menu
    val selectedItemId = when(selectedTabIndex) {
        0 -> "home"
        1 -> "alcoholic"
        2 -> "non_alcoholic"
        else -> "home"
    }

    // Używaj selectedItemId zamiast lokalnego stanu
    var selectedItem by remember { mutableStateOf(selectedItemId) }

    // Aktualizuj selectedItem gdy zmienia się zakładka
    LaunchedEffect(selectedTabIndex) {
        selectedItem = selectedItemId
    }

    val items = listOf(
        MenuItem(
            id = "home",
            title = "Home",
            contentDescription = "Przejdź do strony głównej",
            icon = Icons.Default.Home
        ),
        MenuItem(
            id = "alcoholic",
            title = "Alcoholic drinks",
            contentDescription = "Przejdź do drinków alkoholowych",
            icon = Icons.Default.LocalBar
        ),
        MenuItem(
            id = "non_alcoholic",
            title = "Non-alcoholic drinks",
            contentDescription = "Przejdź do drinków bezalkoholowych",
            icon = Icons.Default.NoDrinks
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider()

                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = selectedItem == item.id,
                        // W pliku AppNavigation.kt, w bloku onClick dla NavigationDrawerItem
                        onClick = {
                            selectedItem = item.id
                            scope.launch {
                                drawerState.close()
                            }

                            val targetTabIndex = when (item.id) {
                                "home" -> 0
                                "alcoholic" -> 1
                                "non_alcoholic" -> 2
                                else -> 0
                            }

                            // Najpierw ustaw indeks zakładki
                            cocktailListViewModel.setSelectedTabIndex(targetTabIndex)

                            // Sprawdź, czy jesteśmy na ekranie szczegółów koktajlu
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute?.startsWith(Screen.CocktailDetail.route.substringBefore("{")) == true) {
                                // Całkowicie wyczyść stos nawigacji i utwórz nowy ekran listy koktajli
                                navController.navigate(Screen.CocktailList.route) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        ,
                                icon = { Icon(imageVector = item.icon, contentDescription = item.contentDescription) }
                    )
                }
            }
        }
    ) {
        // Reszta kodu pozostaje bez zmian
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cocktails") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.CocktailList.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.CocktailList.route) {
                    CocktailListScreen(
                        onCocktailClick = { cocktailId ->
                            navController.navigate(Screen.CocktailDetail.createRoute(cocktailId))
                        },
                        themeViewModel = themeViewModel,
                        cocktailListViewModel = cocktailListViewModel
                    )
                }

                composable(
                    Screen.CocktailDetail.route,
                    arguments = listOf(navArgument("cocktailId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val cocktailId = backStackEntry.arguments?.getString("cocktailId") ?: ""
                    CocktailDetailScreen(
                        cocktailId = cocktailId,
                        onNavigateBack = { navController.navigateUp() },
                        timerViewModel = timerViewModel
                    )
                }
            }
        }
    }
}
