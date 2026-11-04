package com.stardust.cetccalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stardust.cetccalc.data.SamplePairing
import com.stardust.cetccalc.ui.MainViewModel
import com.stardust.cetccalc.ui.screens.AboutScreen
import com.stardust.cetccalc.ui.screens.CalculatorScreen
import com.stardust.cetccalc.ui.screens.DatabaseScreen
import com.stardust.cetccalc.ui.theme.CETCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CETCTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val vm: MainViewModel = viewModel(factory = MainViewModel.Factory)

    val items by vm.items.collectAsState()
    val dbVersion by vm.dbVersion.collectAsState()
    val selectedId by vm.selectedId.collectAsState()
    val qtyText by vm.qtyText.collectAsState()
    val qty2Text by vm.qty2Text.collectAsState()
    val statusMessage by vm.statusMessage.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "calculator",
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)) }
    ) {
        composable("calculator") {
            val entries = SamplePairing.buildPickerEntries(items)
            CalculatorScreen(
                entries = entries,
                dbVersion = dbVersion,
                selected = entries.firstOrNull { it.id == selectedId },
                qtyText = qtyText,
                qty2Text = qty2Text,
                onSelect = vm::select,
                onQtyChange = vm::setQty,
                onQty2Change = vm::setQty2,
                onOpenDatabase = { navController.navigate("database") },
                onOpenAbout = { navController.navigate("about") }
            )
        }
        composable("database") {
            DatabaseScreen(
                testItems = items,
                dbVersion = dbVersion,
                statusMessage = statusMessage,
                onBack = { navController.popBackStack() },
                onImportText = { text, name -> vm.importCsv(text, name) {} },
                onExportRequest = { writeTarget -> vm.exportCsv { csv -> writeTarget(csv) } },
                onSaveNew = vm::saveNew,
                onUpdate = vm::updateItem,
                onDelete = vm::deleteItem,
                nextId = { vm.nextId() }
            )
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
