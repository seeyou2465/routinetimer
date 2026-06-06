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

    /** アラームが再発火すべき時に呼ぶコールバック */
    var onSnoozeExpired: (() -> Unit)? = null

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

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}
