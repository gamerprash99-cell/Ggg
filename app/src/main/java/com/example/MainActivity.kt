package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.AppLockDialog
import com.example.ui.navigation.LifeOSBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.capture.CaptureSheet
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.insights.InsightsScreen
import com.example.ui.screens.insights.InsightsViewModel
import com.example.ui.screens.life.LifeHubScreen
import com.example.ui.screens.life.LifeViewModel
import com.example.ui.screens.notes.NoteEditorScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.notes.NotesViewModel
import com.example.ui.theme.LifeOSTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeOSTheme {
                LifeOSMainApp()
            }
        }
    }
}

@Composable
fun LifeOSMainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = androidx.compose.ui.platform.LocalContext.current
    var showCaptureSheet by remember { mutableStateOf(false) }
    var isAppUnlocked by remember { mutableStateOf(com.example.util.AppLockManager.isSessionUnlocked(context)) }
    var showPinFallback by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(isAppUnlocked) {
        if (!isAppUnlocked && context is androidx.fragment.app.FragmentActivity) {
            if (com.example.util.AppLockManager.canAuthenticate(context)) {
                com.example.util.AppLockManager.showBiometricPrompt(
                    activity = context,
                    onSuccess = { isAppUnlocked = true },
                    onError = { showPinFallback = true }
                )
            } else {
                showPinFallback = true
            }
        }
    }

    val homeViewModel: HomeViewModel = viewModel()
    val notesViewModel: NotesViewModel = viewModel()
    val insightsViewModel: InsightsViewModel = viewModel()
    val lifeViewModel: LifeViewModel = viewModel()

    val shouldShowBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Notes.route,
        Screen.Insights.route,
        Screen.Life.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                LifeOSBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onOpenCapture = { showCaptureSheet = true }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                    onNavigateToCapture = { showCaptureSheet = true },
                    onNavigateToLife = { navController.navigate(Screen.Life.route) }
                )
            }

            composable(Screen.Notes.route) {
                NotesScreen(
                    viewModel = notesViewModel,
                    onEditNote = { noteId ->
                        navController.navigate(Screen.NoteEditor.createRoute(noteId))
                    }
                )
            }

            composable(
                route = Screen.NoteEditor.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = 0L })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                NoteEditorScreen(
                    noteId = noteId,
                    viewModel = notesViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Insights.route) {
                InsightsScreen(viewModel = insightsViewModel)
            }

            composable(Screen.Life.route) {
                LifeHubScreen(viewModel = lifeViewModel)
            }
        }

        // Quick Capture Modal Sheet
        if (showCaptureSheet) {
            CaptureSheet(
                onDismiss = { showCaptureSheet = false },
                onSaved = {
                    showCaptureSheet = false
                }
            )
        }

        if (!isAppUnlocked) {
            // Block UI interaction while locked
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            ) {}
        }

        if (!isAppUnlocked && showPinFallback) {
            AppLockDialog(
                correctPin = com.example.util.AppLockManager.getPin(context),
                onUnlock = {
                    com.example.util.AppLockManager.unlockSession()
                    isAppUnlocked = true
                    showPinFallback = false
                },
                onDismiss = {
                    // Do nothing, force user to unlock
                }
            )
        }
    }
}
