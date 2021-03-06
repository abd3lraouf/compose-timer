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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.application.Timer

@Composable
fun TimerEditor(timer: Timer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val timerState by timer.state.collectAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            ScrollInput(
                maxAmount = 59,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                scrollScale = 0.2f,
                valueChanged = {
                    timer.addInitialHours(it)
                }
            ) {
                Text(text = "H")
            }
            ScrollInput(
                maxAmount = 59,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                scrollScale = 0.5f,
                valueChanged = {
                    timer.addInitialMinutes(it)
                }
            ) {
                Text(text = "M")
            }
            ScrollInput(
                maxAmount = 59,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                valueChanged = {
                    timer.addInitialSeconds(it)
                }
            ) {
                Text(text = "S")
            }
        }
    }
}
