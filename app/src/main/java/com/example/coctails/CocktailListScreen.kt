package com.example.coctails.viewmodel

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctails.components.ProportionalImage
import com.example.coctails.model.Cocktail
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.coctails.util.WindowInfo
import com.example.coctails.util.rememberWindowInfo
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.coctails.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.rememberPagerState
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalSnapperApi::class)
@Composable
fun CocktailListScreen(
    onCocktailClick: (String) -> Unit,
    themeViewModel: ThemeViewModel,
    cocktailListViewModel: CocktailListViewModel = viewModel() // Domyślnie tworzy nowy, ale można przekazać istniejący
) {
    val tabs = listOf("Main", "Alcoholic drinks", "Non-alcoholic drinks")
    val selectedTabIndex by cocktailListViewModel.selectedTabIndex.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = selectedTabIndex)
    val coroutineScope = rememberCoroutineScope()

    // Synchronizacja pagerState z viewModel
    LaunchedEffect(pagerState.currentPage) {
        cocktailListViewModel.setSelectedTabIndex(pagerState.currentPage)
    }

    // Synchronizacja viewModel z pagerState
    LaunchedEffect(selectedTabIndex) {
        if (pagerState.currentPage != selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }


    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            val contentModifier = if (this.maxWidth >= 600.dp) {
                Modifier.widthIn(max = 600.dp)
            } else {
                Modifier.fillMaxWidth()
            }

            Box(modifier = contentModifier) {
                HorizontalPager(
                    count = tabs.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    // lepszej kontroli przesuwania
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                    userScrollEnabled = true
                ) { page ->
                    when (page) {
                        0 -> MainTabContent(themeViewModel)
                        1 -> AlcoholicDrinksTabContent(onCocktailClick)
                        2 -> NonAlcoholicDrinksTabContent(onCocktailClick)
                    }
                }
            }
        }
    }
}


@Composable
fun MainTabContent(themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Dodaj możliwość przewijania
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isLandscape) Arrangement.SpaceEvenly else Arrangement.Center
    ) {
        // Zawartość główna
        Text(
            text = "Witaj w aplikacji Cocktails!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Tutaj znajdziesz przepisy na różnorodne koktajle. Przejdź do odpowiednich zakładek, aby odkryć drinki alkoholowe lub bezalkoholowe.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        // W trybie poziomym zmniejszamy lub usuwamy odstępy
        if (!isLandscape) {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // W trybie poziomym zmniejszamy rozmiar obrazu
        Image(
            painter = painterResource(id = R.drawable.cocktail_logo),
            contentDescription = "Cocktail Logo",
            modifier = Modifier
                .size(if (isLandscape) 120.dp else 200.dp)
                .padding(if (isLandscape) 8.dp else 16.dp),
            contentScale = ContentScale.Fit
        )

        // W trybie poziomym zmniejszamy lub usuwamy odstępy
        if (!isLandscape) {
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Przycisk zmiany motywu - zawsze widoczny
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = if (isDarkTheme) "Tryb ciemny" else "Tryb jasny")
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { themeViewModel.toggleTheme() }
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Przełącz motyw"
                )
            }
        }
    }
}
@Composable
fun AlcoholicDrinksTabContent(onCocktailClick: (String) -> Unit) {
    val viewModel: CocktailViewModel = viewModel()
    val cocktails by viewModel.cocktails.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Określenie, czy urządzenie jest tabletem
    val windowInfo = rememberWindowInfo()
    val isTablet = windowInfo.screenWidthInfo != WindowInfo.WindowType.Compact

    LaunchedEffect(Unit) {
        viewModel.refreshCocktails()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Na tabletach używamy LazyVerticalGrid zamiast LazyColumn
        if (isTablet) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cocktails.toList()) { cocktail: Cocktail ->
                    CocktailCard(cocktail, onCocktailClick)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cocktails.toList()) { cocktail: Cocktail ->
                    CocktailCard(cocktail, onCocktailClick)
                }
            }
        }
    }
}

@Composable
fun CocktailCard(cocktail: Cocktail, onCocktailClick: (String) -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = { onCocktailClick(cocktail.id) }),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isLandscape) {
            // Układ poziomy - obraz z lewej, tekst z prawej
            Row {
                ProportionalImage(
                    imageUrl = cocktail.imageUrl,
                    contentDescription = cocktail.name,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = cocktail.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            // Układ pionowy - obraz na górze, tekst na dole
            Column {
                ProportionalImage(
                    imageUrl = cocktail.imageUrl,
                    contentDescription = cocktail.name,
                    modifier = Modifier.height(180.dp)
                )
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = cocktail.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ProportionalImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit, // Zmiana na Fit
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f) // Zachowanie proporcji kwadratu
        )
    }
}

@Composable
fun NonAlcoholicDrinksTabContent(onCocktailClick: (String) -> Unit) {
    val viewModel: NonAlcoholicCocktailViewModel = viewModel()
    val cocktails by viewModel.cocktails.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Określenie, czy urządzenie jest tabletem
    val windowInfo = rememberWindowInfo()
    val isTablet = windowInfo.screenWidthInfo != WindowInfo.WindowType.Compact

    LaunchedEffect(Unit) {
        viewModel.refreshCocktails()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Na tabletach używamy LazyVerticalGrid zamiast LazyColumn
        if (isTablet) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cocktails.toList()) { cocktail: Cocktail ->
                    CocktailCard(cocktail, onCocktailClick)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cocktails.toList()) { cocktail: Cocktail ->
                    CocktailCard(cocktail, onCocktailClick)
                }
            }
        }
    }
}