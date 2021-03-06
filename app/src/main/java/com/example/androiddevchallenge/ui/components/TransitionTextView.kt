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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.async

@Composable
fun TransitionView(digit: Int, modifier: Modifier = Modifier, content: @Composable (digit: Int) -> Unit) {
    var mainViewIndex by remember { mutableStateOf(0) }
    LaunchedEffect(digit) {
        mainViewIndex = (mainViewIndex + 1).rem(2)
    }
    var currentDigit by remember { mutableStateOf(0) }
    val previousDigit = remember(digit) { currentDigit }
    currentDigit = digit

    if (currentDigit == previousDigit) {
        content(currentDigit)
        return
    }

    val endState = if (currentDigit < previousDigit) {
        TransitionViewPosition.Lower
    } else {
        TransitionViewPosition.Upper
    }
    val startState = if (currentDigit < previousDigit) {
        TransitionViewPosition.Upper
    } else {
        TransitionViewPosition.Lower
    }

    val states = listOf(
        startState,
        TransitionViewPosition.Default,
        TransitionViewPosition.Default,
        endState
    )

    val digits = listOf(currentDigit, previousDigit)

    Box(modifier = modifier) {
        AnimatedView(digit = digits[mainViewIndex], states[mainViewIndex * 2], states[mainViewIndex * 2 + 1]) {
            content(it)
        }

        AnimatedView(digit = digits[1 - mainViewIndex], states[2 - mainViewIndex * 2], states[3 - mainViewIndex * 2]) {
            content(it)
        }
    }
}

private enum class TransitionViewPosition {
    Upper, Default, Lower
}

@Composable
private fun AnimatedView(
    digit: Int,
    from: TransitionViewPosition,
    to: TransitionViewPosition,
    content: @Composable (digit: Int) -> Unit
) {
    val initialOffset = when (from) {
        TransitionViewPosition.Default -> IntOffset.Zero
        TransitionViewPosition.Lower -> IntOffset(0, -50)
        TransitionViewPosition.Upper -> IntOffset(0, 50)
    }
    val initialAlpha = if (from == TransitionViewPosition.Default) 1f else 0f
    val offset = remember(from) { Animatable(initialOffset, IntOffset.VectorConverter) }
    val alpha = remember(from) { Animatable(initialAlpha) }

    val newOffset = when (to) {
        TransitionViewPosition.Default -> IntOffset.Zero
        TransitionViewPosition.Lower -> IntOffset(0, -50)
        TransitionViewPosition.Upper -> IntOffset(0, 50)
    }
    val newAlpha = if (to == TransitionViewPosition.Default) 1f else 0f
    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .offset { offset.value }
    ) {
        content(digit)
    }
    LaunchedEffect(to) {
        offset.animateTo(initialOffset, animationSpec = SnapSpec())
        alpha.animateTo(initialAlpha, animationSpec = SnapSpec())
        val offsetAnim = async {
            offset.animateTo(newOffset)
        }
        val alphaAnim = async {
            alpha.animateTo(newAlpha)
        }
        offsetAnim.await()
        alphaAnim.await()
    }
}
