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
package com.example.androiddevchallenge

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.application.Timer
import com.example.androiddevchallenge.application.TimerMode
import com.example.androiddevchallenge.application.TimerService
import com.example.androiddevchallenge.ui.components.TimerEditor
import com.example.androiddevchallenge.ui.components.TimerView
import com.example.androiddevchallenge.ui.theme.TimerColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimerScreen(timerService: TimerService) {
    val timers by timerService.getTimer().collectAsState()
    var timerAdded by remember { mutableStateOf(false) }
    var selectedTimerIndex by remember { mutableStateOf(0) }
    if (timerAdded) {
        selectedTimerIndex = timers.lastIndex
        timerAdded = false
    }
    while (selectedTimerIndex > timers.lastIndex) selectedTimerIndex -= 1
    val selectedTimer = timers[selectedTimerIndex]

    Box {
        if (timers.size > 1) {
            TimerIndicator(timers = timers, selectedTimerIndex = selectedTimerIndex)
        }
        val modalSheet = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()
        val cornerSize = CornerSize(24.dp)
        ModalBottomSheetLayout(
            sheetState = modalSheet,
            sheetShape = RoundedCornerShape(cornerSize, cornerSize, CornerSize(0f), CornerSize(0f)),
            sheetBackgroundColor = MaterialTheme.colors.surface,
            scrimColor = Color.Transparent,
            sheetContent = {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Edit the Timer",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                key(selectedTimer) { TimerEditor(timer = selectedTimer) }
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        scope.launch {
                            modalSheet.hide()
                        }
                    }
                ) {
                    Text(text = "Submit")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        ) {
            Column(verticalArrangement = Arrangement.Bottom) {
                TimerView(
                    timer = selectedTimer
                )
                val timerState by selectedTimer.state.collectAsState()
                BottomRow(
                    timerState.timerActive,
                    timers.size > 1,
                    editClick = {
                        scope.launch { modalSheet.show() }
                    },
                    onPreviousClick = {
                        if (selectedTimerIndex > 0) selectedTimerIndex -= 1
                    },
                    onNextClick = {
                        if (selectedTimerIndex < timers.lastIndex) selectedTimerIndex += 1
                    },
                    onAddClick = {
                        timerService.addTimer()
                        timerAdded = true
                    },
                    onDeleteClick = {
                        timerService.deleteTimer(selectedTimer)
                    }
                )
            }
        }
    }
}

@Composable
fun BoxScope.TimerIndicator(timers: List<Timer>, selectedTimerIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .align(Alignment.TopCenter)
            .height(64.dp)
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        timers.indices.forEach { index ->
            val timerState by timers[index].state.collectAsState()
            val color = remember(timerState.mode) {
                if (timerState.mode == TimerMode.Expired) TimerColors.expiredColor else TimerColors.yellow
            }
            val alpha by animateFloatAsState(targetValue = if (index == selectedTimerIndex) 1f else 0.5f)
            val size by animateDpAsState(targetValue = if (index == selectedTimerIndex) 12.dp else 8.dp)
            Box(
                modifier = Modifier
                    .padding(4.dp, 0.dp)
                    .clip(CircleShape)
                    .size(size)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BottomRow(
    timerRunning: Boolean,
    deleteEnabled: Boolean,
    editClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onPreviousClick() }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "")
        }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(64.dp)
                .clip(CircleShape)
                .fillMaxWidth(0.7f)
                .background(MaterialTheme.colors.surface),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onDeleteClick() }, enabled = deleteEnabled) {
                val alpha = if (deleteEnabled) 1f else 0.3f
                Icon(Icons.Default.Delete, contentDescription = "", modifier = Modifier.alpha(alpha))
            }
            IconButton(onClick = { editClick() }, enabled = !timerRunning) {
                val alpha = if (timerRunning) 0.3f else 1f
                Icon(Icons.Default.Edit, contentDescription = "", modifier = Modifier.alpha(alpha))
            }
            IconButton(onClick = { onAddClick() }) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
        IconButton(onClick = { onNextClick() }) {
            Icon(Icons.Default.ChevronRight, contentDescription = "")
        }
    }
}
