package com.example.pointofsale.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

object NavAnimations {
    private const val DURATION = 400
    private val easing = FastOutSlowInEasing

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            initialOffsetX = { it / 10 },
            animationSpec = tween(DURATION, easing = easing)
        ) + fadeIn(animationSpec = tween(DURATION, easing = easing)) +
        scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(DURATION, easing = easing)
        )
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            targetOffsetX = { -it / 10 },
            animationSpec = tween(DURATION, easing = easing)
        ) + fadeOut(animationSpec = tween(DURATION, easing = easing)) +
        scaleOut(
            targetScale = 0.92f,
            animationSpec = tween(DURATION, easing = easing)
        )
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            initialOffsetX = { -it / 10 },
            animationSpec = tween(DURATION, easing = easing)
        ) + fadeIn(animationSpec = tween(DURATION, easing = easing)) +
        scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(DURATION, easing = easing)
        )
    }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            targetOffsetX = { it / 10 },
            animationSpec = tween(DURATION, easing = easing)
        ) + fadeOut(animationSpec = tween(DURATION, easing = easing)) +
        scaleOut(
            targetScale = 0.92f,
            animationSpec = tween(DURATION, easing = easing)
        )
    }
}
