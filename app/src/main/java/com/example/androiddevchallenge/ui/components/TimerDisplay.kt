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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.TimerColors
import kotlin.math.abs

@Composable
fun TimerDisplay(
    duration: Int,
    remaining: Int,
    expired: Boolean,
    forceFullView: Boolean,
    animate: Boolean
) {
    val seconds = remember(remaining) { remaining.rem(60 * 60).rem(60) }
    val minutes = remember(remaining) { remaining.rem(60 * 60) - seconds }
    val hours = remember(remaining) { remaining - minutes - seconds }

    val formattedSeconds = remember(seconds) { seconds }
    val formattedMinutes = remember(minutes) { minutes / 60 }
    val formattedHours = remember(hours) { hours / 60 / 60 }

    val color by animateColorAsState(targetValue = if (expired) TimerColors.expiredColor else TimerColors.yellow)

    val digitTextStyle =
        TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = color)
    val unitTextStyle = TextStyle(fontSize = 24.sp, color = color)

    Row(verticalAlignment = Alignment.Bottom) {
        if (remaining < 0) {
            Text(text = "-", style = digitTextStyle)
        }
        if (duration > 3600 || forceFullView) {
            DigitText(formattedHours, digitTextStyle, animate)
            Text(text = "h", style = unitTextStyle)
            Spacer(modifier = Modifier.width(8.dp))
        }
        DigitText(formattedMinutes, digitTextStyle, animate)
        Text(text = "m", style = unitTextStyle)
        Spacer(modifier = Modifier.width(8.dp))
        DigitText(formattedSeconds, digitTextStyle, animate)
        Text(text = "s", style = unitTextStyle)
    }
}

@Composable
private fun DigitText(digit: Int, textStyle: TextStyle, animate: Boolean) {
    if (animate) {
        TransitionView(digit = digit) {
            Text(text = abs(it).toString().padStart(2, '0'), style = textStyle)
        }
    } else {
        Text(text = abs(digit).toString().padStart(2, '0'), style = textStyle)
    }
}
