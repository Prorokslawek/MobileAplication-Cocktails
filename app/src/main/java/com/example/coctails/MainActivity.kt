package com.example.coctails

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctails.ui.theme.CoctailsTheme
import com.example.coctails.viewmodel.ThemeViewModel
import com.example.coctails.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Dodajemy nowy ViewModel do kontrolowania stanu gotowości aplikacji
class SplashViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    init {
        // Symulujemy ładowanie danych
        viewModelScope.launch {
            delay(1000) // Dajemy aplikacji czas na załadowanie
            _isReady.value = true
        }
    }
}

class MainActivity : ComponentActivity() {
    private val themeViewModel by viewModels<ThemeViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalujemy splash screen przed super.onCreate()
        installSplashScreen().apply {
            // Utrzymujemy splash screen dopóki aplikacja nie będzie gotowa
            setKeepOnScreenCondition {
                !splashViewModel.isReady.value
            }

            // Konfigurujemy animację wyjścia
            setOnExitAnimationListener { screen ->
                // Animacja wlewania drinka do szklanki
                val iconView = screen.iconView

                // Przesunięcie w górę (przygotowanie do "wlania")
                val moveUp = ObjectAnimator.ofFloat(
                    iconView,
                    View.TRANSLATION_Y,
                    0f,
                    -100f
                )

                // Przesunięcie w dół (efekt "wlewania")
                val pourDown = ObjectAnimator.ofFloat(
                    iconView,
                    View.TRANSLATION_Y,
                    -100f,
                    300f
                )

                // Zmniejszanie szerokości (zwężanie strumienia)
                val narrowing = ObjectAnimator.ofFloat(
                    iconView,
                    View.SCALE_X,
                    1f,
                    0.3f
                )

                // Zmniejszanie wysokości pod koniec animacji
                val shortening = ObjectAnimator.ofFloat(
                    iconView,
                    View.SCALE_Y,
                    1f,
                    0.1f
                )

                // Obracanie (dodatkowy efekt)
                val rotate = ObjectAnimator.ofFloat(
                    iconView,
                    View.ROTATION,
                    0f,
                    15f
                )

                // Konfiguracja czasu trwania
                moveUp.duration = 600L
                pourDown.duration = 800L
                narrowing.duration = 600L
                shortening.duration = 400L
                rotate.duration = 500L

                // Konfiguracja opóźnień
                pourDown.startDelay = 100L
                narrowing.startDelay = 100L
                shortening.startDelay = 200L

                // Interpolatory dla płynniejszego ruchu
                val bounceInterpolator = BounceInterpolator()
                val accelerateInterpolator = AccelerateInterpolator(1.5f)
                val decelerateInterpolator = DecelerateInterpolator(1.5f)

                moveUp.interpolator = decelerateInterpolator
                pourDown.interpolator = accelerateInterpolator
                narrowing.interpolator = accelerateInterpolator
                shortening.interpolator = accelerateInterpolator

                // Usunięcie ekranu startowego po zakończeniu animacji
                pourDown.doOnEnd { screen.remove() }

                // Uruchomienie animacji
                moveUp.start()
                pourDown.start()
                narrowing.start()
                shortening.start()
                rotate.start()
            }
        }

        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val timerViewModel: TimerViewModel = viewModel()
            val scope = rememberCoroutineScope()

            CoctailsTheme(
                darkTheme = isDarkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        timerViewModel = timerViewModel,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }

    // Zapobiegaj zniszczeniu aktywności przy zmianie orientacji
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
