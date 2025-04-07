package com.example.coctails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctails.viewmodel.TimerViewModel

@Composable
fun TimerFragment(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel = viewModel()
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val isFullScreen by timerViewModel.isFullScreen.collectAsState()
    var minutesInput by remember { mutableIntStateOf(0) }
    var secondsInput by remember { mutableIntStateOf(0) }

    // Wyświetl dialog pełnoekranowy, jeśli isFullScreen jest true
    if (isFullScreen) {
        Dialog(
            onDismissRequest = { timerViewModel.toggleFullScreen() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            FullScreenTimer(
                timerState = timerState,
                onStartClick = { timerViewModel.startTimer() },
                onStopClick = { timerViewModel.stopTimer() },
                onResetClick = {
                    if (timerState.currentSeconds > 0) {
                        timerViewModel.clearTimer()
                    } else {
                        timerViewModel.resetTimer()
                    }
                },
                onExitFullScreenClick = { timerViewModel.toggleFullScreen() },
                isRunning = timerState.isRunning
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Przycisk pełnego ekranu
                IconButton(
                    onClick = { timerViewModel.toggleFullScreen() }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Pełny ekran",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wyświetlanie czasu
            Text(
                text = timerState.formattedTime,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!timerState.isRunning && timerState.currentSeconds == 0) {
                // Ustawienia czasu
                Column {
                    Text(
                        text = "Minutes: $minutesInput",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Slider(
                        value = minutesInput.toFloat(),
                        onValueChange = { minutesInput = it.toInt() },
                        valueRange = 0f..30f,
                        steps = 30,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Seconds: $secondsInput",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Slider(
                        value = secondsInput.toFloat(),
                        onValueChange = { secondsInput = it.toInt() },
                        valueRange = 0f..59f,
                        steps = 59,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            timerViewModel.setMinutes(minutesInput)
                            timerViewModel.setSeconds(secondsInput)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Set time")
                    }
                }
            } else {
                // Przyciski kontrolne z ikonami
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { timerViewModel.startTimer() },
                        enabled = !timerState.isRunning && timerState.currentSeconds > 0,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = if (!timerState.isRunning && timerState.currentSeconds > 0)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = { timerViewModel.stopTimer() },
                        enabled = timerState.isRunning,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = if (timerState.isRunning)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Stop",
                            tint = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (timerState.currentSeconds > 0) {
                                timerViewModel.clearTimer()
                            } else {
                                timerViewModel.resetTimer()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Zeruj",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenTimer(
    timerState: TimerViewModel.TimerState,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    onExitFullScreenClick: () -> Unit,
    isRunning: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Przycisk wyjścia z pełnego ekranu (w prawym górnym rogu)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onExitFullScreenClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Wyjdź z pełnego ekranu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Duży wyświetlacz czasu
            Text(
                text = timerState.formattedTime,
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Przyciski kontrolne
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onStartClick,
                    enabled = !isRunning && timerState.currentSeconds > 0,
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = if (!isRunning && timerState.currentSeconds > 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = onStopClick,
                    enabled = isRunning,
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = if (isRunning)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Zeruj",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
