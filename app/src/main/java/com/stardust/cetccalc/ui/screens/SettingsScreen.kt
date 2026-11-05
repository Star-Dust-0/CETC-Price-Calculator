package com.stardust.cetccalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import com.stardust.cetccalc.ui.components.OneUISectionCard
import com.stardust.cetccalc.ui.components.RowDivider

@Composable
fun SettingsScreen(onBack: () -> Unit, onDatabase: () -> Unit, onAbout: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("backButton")) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "رجوع")
            }
            Text("الإعدادات", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
        }
        Spacer(Modifier.height(20.dp))
        OneUISectionCard("عام") {
            Text("إدارة قاعدة البيانات", modifier = Modifier.fillMaxWidth().clickable(onClick = onDatabase).padding(vertical = 18.dp))
            RowDivider()
            Text("حول التطبيق", modifier = Modifier.fillMaxWidth().clickable(onClick = onAbout).padding(vertical = 18.dp))
        }
    }
}
