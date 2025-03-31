package com.example.coctails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coctails.components.ProportionalImage
import com.example.coctails.data.SampleData
import com.example.coctails.model.Cocktail
import com.example.coctails.utils.WindowWidthSizeClass
import com.example.coctails.utils.calculateWindowSizeClass
import com.example.coctails.components.TimerFragment
import com.example.coctails.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailDetailScreen(cocktailId: String, onNavigateBack: () -> Unit,timerViewModel: TimerViewModel) {
    val cocktail = SampleData.cocktails.find { it.id == cocktailId }
    val windowSizeClass = calculateWindowSizeClass()

    cocktail?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(it.name) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Różne layouty dla różnych rozmiarów ekranu
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    // Telefon - layout pionowy
                    CompactCocktailDetail(cocktail, Modifier.padding(paddingValues))
                }
                else -> {
                    // Tablet - layout poziomy z ograniczoną szerokością obrazu
                    MediumLargeCocktailDetail(cocktail, Modifier.padding(paddingValues))
                }
            }
        }
    } ?: Text("Nie znaleziono koktajlu")
}

@Composable
fun CompactCocktailDetail(cocktail: Cocktail, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ProportionalImage(
            imageUrl = cocktail.imageUrl,
            contentDescription = cocktail.name
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nazwa koktajlu
        Text(
            text = cocktail.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Składniki
        Text(
            text = "Ingredients:",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        cocktail.ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp).width(8.dp).height(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(ingredient)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instrukcje przygotowania
        Text(
            text = "Preparation method:",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cocktail.instructions,
            style = MaterialTheme.typography.bodyLarge
        )
        // Dodaj minutnik po instrukcjach
        Spacer(modifier = Modifier.height(24.dp))

        TimerFragment(
            modifier = Modifier.fillMaxWidth()
        )

    }
}


@Composable
fun MediumLargeCocktailDetail(cocktail: Cocktail, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Obraz z lewej strony, ograniczony do 40% szerokości
        Box(modifier = Modifier.weight(0.4f)) {
            ProportionalImage(
                imageUrl = cocktail.imageUrl,
                contentDescription = cocktail.name,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Szczegóły z prawej strony
        Column(
            modifier = Modifier
                .weight(0.6f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = cocktail.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Składniki
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            cocktail.ingredients.forEach { ingredient ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 8.dp).width(8.dp).height(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(ingredient)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instrukcje przygotowania
            Text(
                text = "Preparation method:",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cocktail.instructions,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            TimerFragment(
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}
