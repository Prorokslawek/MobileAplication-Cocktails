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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coctails.components.ProportionalImage
import com.example.coctails.model.Cocktail
import com.example.coctails.utils.WindowWidthSizeClass
import com.example.coctails.utils.calculateWindowSizeClass
import com.example.coctails.components.TimerFragment
import com.example.coctails.viewmodel.TimerViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctails.viewmodel.CocktailDetailViewModel
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailDetailScreen(cocktailId: String, onNavigateBack: () -> Unit, timerViewModel: TimerViewModel) {
    val viewModel: CocktailDetailViewModel = viewModel()
    val cocktailState by viewModel.cocktail.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val windowSizeClass = calculateWindowSizeClass()
    val context = LocalContext.current

    LaunchedEffect(cocktailId) {
        viewModel.loadCocktailDetails(cocktailId)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        cocktailState?.let { cocktail ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(cocktail.name) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Przygotuj treść SMS-a
                            val smsBody = buildString {
                                append("Składniki do koktajlu ${cocktail.name}:\n\n")
                                cocktail.ingredients.forEach { ingredient ->
                                    append("• $ingredient\n")
                                }
                            }

                            // Utwórz intent do wysłania SMS-a
                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:")
                                putExtra("sms_body", smsBody)
                            }

                            // Uruchom aktywność wysyłania SMS-a
                            context.startActivity(smsIntent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Wyślij składniki SMS-em"
                        )
                    }
                }
            ) { paddingValues ->
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        CompactCocktailDetail(cocktail, Modifier.padding(paddingValues))
                    }
                    else -> {
                        MediumLargeCocktailDetail(cocktail, Modifier.padding(paddingValues))
                    }
                }
            }
        } ?: Text("Cannot find Cocktail")
    }
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
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
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
        // Obraz z lewej strony, ograniczony do 40% szerokoĹci
        Box(modifier = Modifier.weight(0.4f)) {
            ProportionalImage(
                imageUrl = cocktail.imageUrl,
                contentDescription = cocktail.name,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // SzczegĂłĹy z prawej strony
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

            // SkĹadniki
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
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            )

        }
    }
}