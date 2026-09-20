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
 * Safely pops the backstack only when in the RESUMED state, preventing crashes on rapid back clicks.
 */
fun NavController.popBackStackSafe(): Boolean {
    val currentEntry = currentBackStackEntry
    return if (currentEntry != null && currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        try {
            popBackStack()
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    } else {
        false
    }
}
