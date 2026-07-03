package com.tawagcheck.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tawagcheck.app.AppContainer
import com.tawagcheck.app.service.RoleRequestHelper
import com.tawagcheck.app.ui.dashboard.DashboardScreen
import com.tawagcheck.app.ui.dashboard.DashboardViewModel
import com.tawagcheck.app.ui.history.HistoryScreen
import com.tawagcheck.app.ui.history.HistoryViewModel
import com.tawagcheck.app.ui.onboarding.OnboardingScreen
import com.tawagcheck.app.ui.scamlist.ScamListScreen
import com.tawagcheck.app.ui.scamlist.ScamListViewModel
import com.tawagcheck.app.ui.settings.SettingsScreen
import com.tawagcheck.app.ui.settings.SettingsViewModel
import com.tawagcheck.app.ui.strings.LocalStrings

private object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val HISTORY = "history"
    const val SCAM_LIST = "scam_list"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(appContainer: AppContainer, roleRequestHelper: RoleRequestHelper) {
    val navController = rememberNavController()
    var roleHeld by remember { mutableStateOf(roleRequestHelper.isRoleHeld()) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                roleHeld = roleRequestHelper.isRoleHeld()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val startDestination = if (roleHeld) Routes.DASHBOARD else Routes.ONBOARDING

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                roleRequestHelper = roleRequestHelper,
                onDone = {
                    roleHeld = true
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.DASHBOARD) { MainScaffold(appContainer, navController, Routes.DASHBOARD) }
        composable(Routes.HISTORY) { MainScaffold(appContainer, navController, Routes.HISTORY) }
        composable(Routes.SCAM_LIST) { MainScaffold(appContainer, navController, Routes.SCAM_LIST) }
        composable(Routes.SETTINGS) { MainScaffold(appContainer, navController, Routes.SETTINGS) }
    }
}

@Composable
private fun MainScaffold(appContainer: AppContainer, navController: NavHostController, current: String) {
    val strings = LocalStrings.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: current

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = { navController.navigateSingleTop(Routes.DASHBOARD) },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    label = { Text(strings.navDashboard) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.HISTORY,
                    onClick = { navController.navigateSingleTop(Routes.HISTORY) },
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    label = { Text(strings.navHistory) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.SCAM_LIST,
                    onClick = { navController.navigateSingleTop(Routes.SCAM_LIST) },
                    icon = { Icon(Icons.Filled.Block, contentDescription = null) },
                    label = { Text(strings.navScamList) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = { navController.navigateSingleTop(Routes.SETTINGS) },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(strings.navSettings) }
                )
            }
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (currentRoute) {
            Routes.DASHBOARD -> {
                val viewModel: DashboardViewModel = viewModel(factory = viewModelFactory {
                    initializer { DashboardViewModel(appContainer.settingsDataStore, appContainer.callHistoryRepository) }
                })
                DashboardScreen(viewModel, contentModifier)
            }
            Routes.HISTORY -> {
                val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory {
                    initializer { HistoryViewModel(appContainer.callHistoryRepository) }
                })
                HistoryScreen(viewModel, contentModifier)
            }
            Routes.SCAM_LIST -> {
                val viewModel: ScamListViewModel = viewModel(factory = viewModelFactory {
                    initializer { ScamListViewModel(appContainer.scamRepository) }
                })
                ScamListScreen(viewModel, contentModifier)
            }
            Routes.SETTINGS -> {
                val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory {
                    initializer {
                        SettingsViewModel(
                            appContainer.settingsDataStore,
                            appContainer.scamRepository,
                            appContainer.callHistoryRepository,
                            appContainer.scamListUpdateService
                        )
                    }
                })
                SettingsScreen(viewModel, contentModifier)
            }
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
