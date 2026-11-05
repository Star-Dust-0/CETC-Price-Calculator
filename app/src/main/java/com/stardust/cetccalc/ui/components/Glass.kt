package com.stardust.cetccalc.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

val LocalGlassState = compositionLocalOf<HazeState?> { null }

/** Blur the content behind the control, never the icon or text itself. */
@Composable
fun Modifier.frosted(shape: Shape = RoundedCornerShape(26.dp)): Modifier {
    val state = LocalGlassState.current
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    return if (Build.VERSION.SDK_INT >= 31 && state != null) {
        clip(shape).hazeEffect(state, HazeStyle(base, HazeTint(base.copy(alpha = 0.85f)), 28.dp, 0.04f))
    } else {
        clip(shape).background(base.copy(alpha = 0.97f))
    }
}

@Composable
fun FloatingTool(icon: ImageVector, label: String, tag: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp).frosted().testTag(tag)) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurface)
    }
}
