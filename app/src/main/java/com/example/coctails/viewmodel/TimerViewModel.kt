package com.example.coctails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> = _isFullScreen.asStateFlow()

    private var timerJob: Job? = null

    fun toggleFullScreen() {
        _isFullScreen.value = !_isFullScreen.value
    }

    fun setMinutes(minutes: Int) {
        if (_timerState.value.isRunning) return

        _timerState.value = _timerState.value.copy(
            totalSeconds = minutes * 60,
            currentSeconds = minutes * 60
        )
    }

    fun setSeconds(seconds: Int) {
        if (_timerState.value.isRunning) return

        _timerState.value = _timerState.value.copy(
            totalSeconds = _timerState.value.totalSeconds - _timerState.value.totalSeconds % 60 + seconds,
            currentSeconds = _timerState.value.currentSeconds - _timerState.value.currentSeconds % 60 + seconds
        )
    }

    fun startTimer() {
        if (_timerState.value.isRunning || _timerState.value.currentSeconds <= 0) return

        _timerState.value = _timerState.value.copy(isRunning = true)

        timerJob = viewModelScope.launch {
            while (_timerState.value.currentSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                _timerState.value = _timerState.value.copy(
                    currentSeconds = _timerState.value.currentSeconds - 1
                )
            }

            if (_timerState.value.currentSeconds <= 0) {
                _timerState.value = _timerState.value.copy(isRunning = false)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            currentSeconds = _timerState.value.totalSeconds
        )
    }

    fun clearTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    data class TimerState(
        val totalSeconds: Int = 0,
        val currentSeconds: Int = 0,
        val isRunning: Boolean = false
    ) {
        val minutes: Int
            get() = currentSeconds / 60

        val seconds: Int
            get() = currentSeconds % 60

        val formattedTime: String
            get() = String.format("%02d:%02d", minutes, seconds)
    }
}