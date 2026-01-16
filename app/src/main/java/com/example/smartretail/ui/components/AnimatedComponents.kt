package com.example.smartretail.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Reusable animated card component
 * Memberikan smooth entrance animation untuk cards
 */
@Composable
fun AnimatedCard(
    visible: Boolean = true,
    delay: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(300, delay, FastOutSlowInEasing)
        ),
        exit = fadeOut(
            animationSpec = tween(200)
        ) + slideOutVertically(
            targetOffsetY = { -it / 4 },
            animationSpec = tween(200)
        )
    ) {
        content()
    }
}

/**
 * Animated list item dengan fade in effect
 */
@Composable
fun AnimatedListItem(
    visible: Boolean = true,
    index: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = index * 30,  // Staggered animation
                easing = LinearOutSlowInEasing
            )
        ) + expandVertically(
            animationSpec = tween(200, index * 30)
        ),
        exit = fadeOut() + shrinkVertically()
    ) {
        content()
    }
}
