package com.routinealarm.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor() : ViewModel() {

    private val _countdownSeconds = MutableStateFlow<Int?>(null)
    val countdownSeconds: StateFlow<Int?> = _countdownSeconds

    private val _isSnoozing = MutableStateFlow(false)
    val isSnoozing: StateFlow<Boolean> = _isSnoozing

    private val _timerRemainingSeconds = MutableStateFlow<Int?>(null)
    val timerRemainingSeconds: StateFlow<Int?> = _timerRemainingSeconds

    private val _isTimerPaused = MutableStateFlow(false)
    val isTimerPaused: StateFlow<Boolean> = _isTimerPaused

    /** アラームが再発火すべき時に呼ぶコールバック */
    var onSnoozeExpired: (() -> Unit)? = null
    var onTimerFinished: (() -> Unit)? = null

    private var countdownJob: Job? = null

    fun startSnooze(seconds: Int) {
        countdownJob?.cancel()
        _isSnoozing.value = true
        _countdownSeconds.value = seconds
        countdownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000L)
                remaining--
                _countdownSeconds.value = remaining
            }
            _isSnoozing.value = false
            _countdownSeconds.value = null
            onSnoozeExpired?.invoke()
        }
    }

    fun cancelSnooze() {
        countdownJob?.cancel()
        _isSnoozing.value = false
        _countdownSeconds.value = null
    }

    fun startTimer(seconds: Int) {
        countdownJob?.cancel()
        _isSnoozing.value = false
        _countdownSeconds.value = null
        _isTimerPaused.value = false
        _timerRemainingSeconds.value = seconds
        countdownJob = viewModelScope.launch {
            while ((_timerRemainingSeconds.value ?: 0) > 0) {
                delay(1000L)
                if (!_isTimerPaused.value) {
                    val remaining = (_timerRemainingSeconds.value ?: 0) - 1
                    _timerRemainingSeconds.value = remaining.coerceAtLeast(0)
                }
            }
            if (!_isTimerPaused.value) {
                onTimerFinished?.invoke()
            }
        }
    }

    fun addTimerMinutes(minutes: Int) {
        val current = _timerRemainingSeconds.value ?: 0
        _timerRemainingSeconds.value = current + minutes * 60
    }

    fun pauseTimer() {
        if ((_timerRemainingSeconds.value ?: 0) > 0) {
            _isTimerPaused.value = true
        }
    }

    fun resumeTimer() {
        if ((_timerRemainingSeconds.value ?: 0) > 0) {
            _isTimerPaused.value = false
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}
