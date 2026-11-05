package com.stardust.cetccalc.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDrawer(
    open: Boolean, selectedId: Int?, cubeId: Int?, groutId: Int?, dbVersion: String,
    onDismiss: () -> Unit, onSelect: (Int) -> Unit, onSearch: () -> Unit,
    onDatabase: () -> Unit, onSettings: () -> Unit
) {
    BackHandler(enabled = open, onBack = onDismiss)
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(open, enter = fadeIn(tween(240)), exit = fadeOut(tween(200))) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)).clickable(onClick = onDismiss).testTag("drawerScrim"))
        }
        AnimatedVisibility(
            visible = open, modifier = Modifier.align(Alignment.CenterStart),
            enter = slideInHorizontally(tween(280)) { it } + fadeIn(tween(220)),
            exit = slideOutHorizontally(tween(240)) { it } + fadeOut(tween(180))
        ) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(start = 11.dp, top = 14.dp, bottom = 24.dp)
                        .width((maxWidth * 0.78f).coerceAtMost(360.dp)).fillMaxHeight()
                        .frosted().verticalScroll(rememberScrollState()).padding(16.dp).testTag("navigationDrawer")
                ) {
                    Text("CETC", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp))
                    Text("حاسبة الأسعار", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 12.dp))
                    Text("اختصارات سريعة", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 28.dp, bottom = 12.dp))
                    DrawerItem("حاسبة سعر المكعبات الخرسانية", Icons.Outlined.ViewInAr, selectedId == cubeId && cubeId != null, cubeId != null, "cubeShortcut") { cubeId?.let(onSelect) }
                    DrawerItem("حاسبة سعر الجراوت", Icons.Outlined.Science, selectedId == groutId && groutId != null, groutId != null, "groutShortcut") { groutId?.let(onSelect) }
                    HorizontalDivider(Modifier.padding(vertical = 14.dp))
                    DrawerItem("كل الاختبارات", Icons.Outlined.Search, tag = "allTestsShortcut", onClick = onSearch)
                    DrawerItem("إدارة قاعدة البيانات", Icons.Outlined.Storage, tag = "databaseShortcut", onClick = onDatabase)
                    HorizontalDivider(Modifier.padding(vertical = 14.dp))
                    DrawerItem("الإعدادات", Icons.Outlined.Settings, tag = "drawerSettings", onClick = onSettings)
                    Spacer(Modifier.height(56.dp))
                    Text("قائمة الأسعار $dbVersion", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, selected: Boolean = false, enabled: Boolean = true, tag: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick).padding(14.dp).testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.4f), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
