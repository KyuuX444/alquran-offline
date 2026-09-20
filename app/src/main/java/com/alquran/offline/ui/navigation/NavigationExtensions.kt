package com.alquran.offline.ui.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Safely navigates to the target route, preventing crashes from rapid multiple clicks,
 * duplicate destinations, and navigation while the screen is not in RESUMED state.
 */
fun NavController.navigateSafe(
    route: String,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    val currentEntry = currentBackStackEntry
    // Only proceed if current entry is RESUMED to avoid race conditions and backstack corruption
    if (currentEntry == null || currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        try {
            if (builder != null) {
                navigate(route, builder)
            } else {
                navigate(route) {
                    launchSingleTop = true
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

/**
 * Safely pops the backstack when in at least the STARTED state, preventing crashes on rapid back clicks,
 * and falls back to Home if the stack has no previous destinations.
 */
fun NavController.popBackStackSafe(): Boolean {
    val currentEntry = currentBackStackEntry
    return if (currentEntry == null || currentEntry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        try {
            val popped = popBackStack()
            if (!popped) {
                navigateToHome()
                true
            } else {
                true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            try {
                navigateToHome()
                true
            } catch (ignored: Throwable) {
                false
            }
        }
    } else {
        false
    }
}

/**
 * Navigates directly and reliably to the Home screen, popping all higher destinations.
 */
fun NavController.navigateToHome() {
    val popped = popBackStack(Screen.Home.route, inclusive = false)
    if (!popped) {
        navigateSafe(Screen.Home.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}
