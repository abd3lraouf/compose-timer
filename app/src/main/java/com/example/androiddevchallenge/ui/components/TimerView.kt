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
package com.example.androiddevchallenge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.application.Timer
import com.example.androiddevchallenge.application.TimerMode

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColumnScope.TimerView(
    timer: Timer
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        TimerPlayer(timer = timer)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerPlayer(timer: Timer) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val timerState by timer.state.collectAsState()
        Box(modifier = Modifier.padding(32.dp)) {
            val padding by animateDpAsState(targetValue = if (timerState.mode == TimerMode.Stopped) 0.dp else 32.dp)
            TimeCircle(timer = timer) {
                Box {
                    if (timerState.mode == TimerMode.Expired) {
                        TimeExpiredCircle()
                    }
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .clip(CircleShape)
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.1f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimerDisplay(
                            duration = timerState.startSeconds,
                            remaining = timerState.remainingSeconds,
                            expired = timerState.mode == TimerMode.Expired,
                            forceFullView = false,
                            animate = true
                        )
                        AnimatedVisibility(visible = timerState.mode != TimerMode.Stopped) {
                            TextButton(onClick = { timer.addSeconds(60) }) {
                                Text(text = "+1 Minute")
                            }
                        }
                    }
                }
            }
        }
        Row {
            AnimatedVisibility(visible = !timerState.timerActive) {
                Row {
                    OutlinedButton(
                        modifier = Modifier.size(56.dp, 56.dp),
                        onClick = { timer.reset() },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.RotateLeft, contentDescription = "")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
            FloatingActionButton(
                onClick = {
                    if (timerState.mode == TimerMode.Expired) timer.reset() else timer.toggle()
                }
            ) {
                val icon = when (timerState.mode) {
                    TimerMode.Running -> Icons.Filled.Pause
                    TimerMode.Expired -> Icons.Filled.Stop
                    else -> Icons.Filled.PlayArrow
                }
                Icon(icon, contentDescription = "")
            }
        }
    }
}
