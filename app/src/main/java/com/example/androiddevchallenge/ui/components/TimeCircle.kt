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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.application.Timer
import com.example.androiddevchallenge.ui.theme.TimerColors
import kotlin.random.Random

@Composable
fun TimeCircle(timer: Timer, content: @Composable BoxScope.() -> Unit) {
    val timerState by timer.state.collectAsState()
    val angle = remember(timerState.startSeconds, timerState.remainingSeconds) {
        (timerState.fraction * 360f).coerceAtLeast(0f)
    }
    val animatedAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = TweenSpec(durationMillis = 280)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .drawBehind {
                drawCircle(
                    Color.White.copy(alpha = 0.1f),
                    style = Stroke(width = 24f, cap = StrokeCap.Round)
                )
                drawArc(
                    TimerColors.yellow,
                    startAngle = -90f,
                    sweepAngle = animatedAngle,
                    useCenter = false,
                    style = Stroke(width = 24f, cap = StrokeCap.Round)
                )
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun TimeExpiredCircle() {
    val infinite = rememberInfiniteTransition()
    val random = Random(23213)
    val angles = (0 until 8).map {
        val start = random.nextInt(0, 360).toFloat()
        val sweep = random.nextInt(40, 80).toFloat()
        val cw = random.nextBoolean()
        val duration = random.nextInt(500, 3000)
        infinite.animateFloat(
            initialValue = start,
            targetValue = if (cw) start + 360 else start - 360,
            animationSpec = infiniteRepeatable(
                tween(duration, easing = LinearEasing),
                RepeatMode.Restart
            )
        ) to sweep
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .drawBehind {
                angles.forEach {
                    drawArc(
                        TimerColors.expiredColor,
                        alpha = 0.3f,
                        startAngle = it.first.value,
                        sweepAngle = it.second,
                        useCenter = false,
                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                    )
                }
            }
    )
}
