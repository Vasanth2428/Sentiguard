package com.sentiguard.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentiguard.app.ui.screens.*

object SentiguardDestinations {
    const val DASHBOARD = "home"
    const val MONITOR = "monitor"
    const val SCAN = "scan"
    const val LOGS = "logs"
    const val ALERTS = "alerts"
    const val SUPPORT = "support"
    const val STATISTICS = "statistics"
    const val HEALTH = "health"
}

@Composable
fun SentiguardNavGraph() {
    val navController = rememberNavController()
    // Current route for bottom bar selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            SafeGuardBottomBar(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SentiguardDestinations.DASHBOARD,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { androidx.compose.animation.fadeIn() },
            exitTransition = { androidx.compose.animation.fadeOut() }
        ) {
            composable(SentiguardDestinations.DASHBOARD) {
                // Home
                val viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                DashboardScreen(
                    state = state, 
                    onEvent = viewModel::onEvent,
                    onNavigateToHealth = { navController.navigate(SentiguardDestinations.HEALTH) }
                )
            }
            
            composable(SentiguardDestinations.HEALTH) {
                val viewModel: HealthCheckViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                HealthCheckScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            
            composable(SentiguardDestinations.MONITOR) {
                CoughMonitorScreen()
            }

            composable(SentiguardDestinations.SCAN) {
                // Nail Check
                val viewModel: NailCheckViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                NailCheckScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }

            composable(SentiguardDestinations.LOGS) {
                // Logs
                val viewModel: EvidenceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                EvidenceLogsScreen(
                    state = state, 
                    onViewStats = { navController.navigate(SentiguardDestinations.STATISTICS) }
                )
            }

            composable(SentiguardDestinations.ALERTS) {
                val viewModel: AlertsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                AlertsScreen(state = state, onEvent = viewModel::onEvent)
            }
            
            composable(SentiguardDestinations.SUPPORT) {
                // Settings/Support
                val viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                SettingsScreen(state = state, onEvent = viewModel::onEvent)
            }
            
            // Details
             composable("evidence_detail/{logId}") { 
                // ... detail logic
             }

            composable(SentiguardDestinations.STATISTICS) {
                val viewModel: StatisticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                
                StatisticsScreen(state = state, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun SafeGuardBottomBar(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(SentiguardDestinations.DASHBOARD, "Home", Icons.Default.Home),
            Triple(SentiguardDestinations.MONITOR, "Monitor", Icons.Default.Star), // Mic icon ideal
            Triple(SentiguardDestinations.SCAN, "Scan", Icons.Default.ThumbUp), // Camera/Scan icon
            Triple(SentiguardDestinations.LOGS, "Logs", Icons.Default.LocationOn),
            Triple(SentiguardDestinations.ALERTS, "Alerts", Icons.Default.Notifications),
            Triple(SentiguardDestinations.SUPPORT, "Support", Icons.Default.Settings)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                selected = currentDestination?.route == route,
                onClick = { onNavigate(route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
