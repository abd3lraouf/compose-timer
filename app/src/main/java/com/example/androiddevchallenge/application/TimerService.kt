/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerService {

    private val timers = MutableStateFlow(listOf(Timer()))

    fun getTimer(): StateFlow<List<Timer>> = timers.asStateFlow()

    fun deleteTimer(timer: Timer) {
        timer.stop()
        timers.tryEmit(timers.value - timer)
    }

    fun addTimer() {
        timers.tryEmit(timers.value + Timer())
    }
}

class Timer {
    private val _state = MutableStateFlow(TimerState(0, 0, TimerMode.Stopped))
    val state get() = _state.asStateFlow()

    private var initialSeconds: Int = 0
    private var initialMinutes: Int = 0
    private var initialHours: Int = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var runningJob: Job? = null

    fun start() {
        if (runningJob?.isActive == true) return
        runningJob = coroutineScope.launch {
            alterState(mode = TimerMode.Running)
            while (isActive) {
                delay(1000)
                ensureActive()
                alterState(remainingSeconds = _state.value.remainingSeconds - 1)
                checkExpiration()
            }
        }
    }

    fun stop() {
        runningJob?.cancel()
        alterState(mode = TimerMode.Paused)
    }

    fun toggle() {
        if (runningJob?.isActive == true) {
            stop()
        } else {
            start()
        }
    }

    fun reset() {
        stop()
        alterState(remainingSeconds = initialSeconds, startSeconds = initialSeconds, mode = TimerMode.Stopped)
    }

    fun addInitialSeconds(seconds: Int) {
        initialSeconds = (initialSeconds + seconds).coerceIn(0, 59)
        updateInitialTime()
    }

    fun addInitialMinutes(minutes: Int) {
        initialMinutes = (initialMinutes + minutes).coerceIn(0, 59)
        updateInitialTime()
    }

    fun addInitialHours(hours: Int) {
        initialHours = (initialHours + hours).coerceIn(0, 59)
        updateInitialTime()
    }

    fun addSeconds(seconds: Int) {
        alterState(
            startSeconds = _state.value.startSeconds + seconds,
            remainingSeconds = _state.value.remainingSeconds + seconds
        )
        checkExpiration()
    }

    private fun updateInitialTime() {
        val time = sumUpInitialTime()
        alterState(startSeconds = time, remainingSeconds = time)
    }

    private fun sumUpInitialTime(): Int {
        return initialSeconds + initialMinutes * 60 + initialHours * 60 * 60
    }

    private fun checkExpiration() {
        if (_state.value.remainingSeconds <= 0) alterState(mode = TimerMode.Expired)
        else alterState(mode = TimerMode.Running)
    }

    private fun alterState(
        startSeconds: Int = _state.value.startSeconds,
        remainingSeconds: Int = _state.value.remainingSeconds,
        mode: TimerMode = _state.value.mode
    ) {
        _state.tryEmit(TimerState(startSeconds, remainingSeconds, mode))
    }
}

class TimerState(
    val startSeconds: Int,
    val remainingSeconds: Int,
    val mode: TimerMode
) {
    val fraction get() = if (startSeconds == 0) 1f else remainingSeconds.toFloat() / startSeconds.toFloat()

    val timerActive: Boolean get() = mode == TimerMode.Running || mode == TimerMode.Expired
}

enum class TimerMode {
    Running, Paused, Stopped, Expired
}
