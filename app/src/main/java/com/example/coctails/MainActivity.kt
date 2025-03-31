package com.example.coctails

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coctails.ui.theme.CoctailsTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctails.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoctailsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Utworzenie instancji TimerViewModel na poziomie aktywności
                    val timerViewModel: TimerViewModel = viewModel()

                    // Przekazanie TimerViewModel do CocktailNavHost
                    CocktailNavHost(timerViewModel = timerViewModel)
                }
            }
        }
    }

    // Zapobiegaj zniszczeniu aktywności przy zmianie orientacji
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Nie rób nic - aktywność nie zostanie zniszczona
    }
}
