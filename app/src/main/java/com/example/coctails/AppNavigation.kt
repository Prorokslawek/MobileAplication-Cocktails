package com.example.coctails

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var selectedItem by remember { mutableStateOf("home") }

    // Współdzielony ViewModel dla listy koktajli
    val cocktailListViewModel: CocktailListViewModel = viewModel()

    val items = listOf(
        MenuItem(
            id = "home",
            title = "Strona główna",
            contentDescription = "Przejdź do strony głównej",
            icon = Icons.Default.Home
        ),
        MenuItem(
            id = "alcoholic",
            title = "Drinki Alkoholowe",
            contentDescription = "Przejdź do drinków alkoholowych",
            icon = Icons.Default.LocalBar
        ),
        MenuItem(
            id = "non_alcoholic",
            title = "Drinki Bezalkoholowe",
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
                        onClick = {
                            selectedItem = item.id
                            scope.launch {
                                drawerState.close()
                            }
                            when (item.id) {
                                "home" -> {
                                    cocktailListViewModel.setSelectedTabIndex(0)
                                    // Nawiguj tylko jeśli nie jesteś już na ekranie listy
                                    if (navController.currentDestination?.route != Screen.CocktailList.route) {
                                        navController.navigate(Screen.CocktailList.route)
                                    }
                                }
                                "alcoholic" -> {
                                    cocktailListViewModel.setSelectedTabIndex(1)
                                    if (navController.currentDestination?.route != Screen.CocktailList.route) {
                                        navController.navigate(Screen.CocktailList.route)
                                    }
                                }
                                "non_alcoholic" -> {
                                    cocktailListViewModel.setSelectedTabIndex(2)
                                    if (navController.currentDestination?.route != Screen.CocktailList.route) {
                                        navController.navigate(Screen.CocktailList.route)
                                    }
                                }
                                "settings" -> {
                                    // Przejdź do ustawień (jeśli masz taki ekran)
                                }
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.contentDescription) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Coctails") },
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
                        cocktailListViewModel = cocktailListViewModel // Przekaż ViewModel
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