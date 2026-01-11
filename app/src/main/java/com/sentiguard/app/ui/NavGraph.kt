package com.sentiguard.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sentiguard.app.ui.screens.DashboardEvent
import com.sentiguard.app.ui.screens.DashboardScreen
import com.sentiguard.app.ui.screens.DashboardState
import com.sentiguard.app.ui.screens.EvidenceLogsScreen
import com.sentiguard.app.ui.screens.EvidenceLogsState
import com.sentiguard.app.ui.screens.NailCheckEvent
import com.sentiguard.app.ui.screens.NailCheckScreen
import com.sentiguard.app.ui.screens.NailCheckState
import com.sentiguard.app.ui.screens.SettingsEvent
import com.sentiguard.app.ui.screens.SettingsScreen
import com.sentiguard.app.ui.screens.SettingsState

object SentiguardDestinations {
    const val DASHBOARD = "dashboard"
    const val NAIL_CHECK = "nail_check"
    const val LOGS = "logs"
    const val SETTINGS = "settings"
}

@Composable
fun SentiguardNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SentiguardDestinations.DASHBOARD,
        enterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { 1000 }) + androidx.compose.animation.fadeIn() },
        exitTransition = { androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -1000 }) + androidx.compose.animation.fadeOut() },
        popEnterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { -1000 }) + androidx.compose.animation.fadeIn() },
        popExitTransition = { androidx.compose.animation.slideOutHorizontally(targetOffsetX = { 1000 }) + androidx.compose.animation.fadeOut() }
    ) {
        composable(SentiguardDestinations.DASHBOARD) {
            // In a real app, state would come from ViewModel
            val dummyState = DashboardState() 
            DashboardScreen(
                state = dummyState,
                onEvent = { event ->
                    when (event) {
                        is DashboardEvent.NavigateToNailCheck -> navController.navigate(SentiguardDestinations.NAIL_CHECK)
                        is DashboardEvent.NavigateToLogs -> navController.navigate(SentiguardDestinations.LOGS)
                        is DashboardEvent.NavigateToSettings -> navController.navigate(SentiguardDestinations.SETTINGS)
                        is DashboardEvent.ToggleMonitoring -> { /* Handle monitoring toggle logic */ }
                    }
                }
            )
        }

        composable(SentiguardDestinations.NAIL_CHECK) {
            val dummyState = NailCheckState()
            NailCheckScreen(
                state = dummyState,
                onEvent = { event ->
                    when (event) {
                        is NailCheckEvent.CaptureImage -> { /* Trigger Camera/Mock Result */ }
                        is NailCheckEvent.Reset -> { /* Reset State */ }
                    }
                }
            )
        }

        composable(SentiguardDestinations.LOGS) {
            val dummyState = EvidenceLogsState()
            EvidenceLogsScreen(state = dummyState)
        }

        composable(SentiguardDestinations.SETTINGS) {
            val dummyState = SettingsState()
            SettingsScreen(
                state = dummyState,
                onEvent = { event ->
                    when (event) {
                        is SettingsEvent.ToggleVibration -> { /* Handle toggle */ }
                        is SettingsEvent.ToggleAudioSupport -> { /* Handle toggle */ }
                    }
                }
            )
        }
    }
}
