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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun ScrollInput(
    maxAmount: Int,
    modifier: Modifier = Modifier,
    scrollScale: Float = 2f,
    valueChanged: (Int) -> Unit,
    content: @Composable () -> Unit = { }
) {
    val scrollActive = remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (scrollActive.value) 4.dp else 0.dp)
    val padding by animateDpAsState(targetValue = if (scrollActive.value) 6.dp else 8.dp)
    val color by animateColorAsState(
        targetValue = if (scrollActive.value) Color.White.copy(alpha = 0.4f) else Color(
            0xFF2d1a66
        )
    )
    Column(
        modifier = modifier
            .padding(padding)
            .clip(
                RoundedCornerShape(
                    CornerSize(8.dp),
                    CornerSize(8.dp),
                    CornerSize(8.dp),
                    CornerSize(8.dp)
                )
            )
            .shadow(elevation)
            .background(color)
            .padding(0.dp, 8.dp)
            .scrollWheel(
                maxAmount,
                Orientation.Vertical,
                active = scrollActive,
                scrollScale = scrollScale
            ) {
                valueChanged(it)
            },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { valueChanged(1) }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "", modifier = Modifier.rotate(90f))
        }
        content()
        IconButton(onClick = { valueChanged(-1) }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "", modifier = Modifier.rotate(-90f))
        }
    }
}

fun Modifier.scrollWheel(
    maxAmount: Int,
    orientation: Orientation,
    scrollScale: Float = 2f,
    active: MutableState<Boolean>? = null,
    valueChange: (Int) -> Unit
): Modifier {
    return composed {
        var size by remember { mutableStateOf(0) }
        var temp by remember { mutableStateOf(0f) }
        onSizeChanged {
            size = when (orientation) {
                Orientation.Vertical -> it.height
                Orientation.Horizontal -> it.width
            }
        }.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    active?.value = true
                },
                onDragEnd = {
                    active?.value = false
                },
                onDragCancel = {
                    active?.value = false
                },
                onDrag = { change, dragAmount ->
                    val diff = when (orientation) {
                        Orientation.Vertical -> dragAmount.y
                        Orientation.Horizontal -> dragAmount.x
                    }
                    val percentage = diff / size
                    val amount = maxAmount * percentage * scrollScale
                    temp += amount
                    if (abs(temp) > 1f) {
                        valueChange(-(temp.toInt()))
                        temp = 0f
                    }
                    change.consumeAllChanges()
                }
            )
        }
    }
}
