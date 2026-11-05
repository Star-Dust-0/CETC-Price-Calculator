package com.stardust.cetccalc.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Themed color tokens in the Morphe manager design language (Material 3
 * expressive): quiet tinted neutrals, one blue accent, surface-container
 * tiers. Light and dark variants; names keep the historical OneUI* API so
 * call sites adapt to the theme automatically (redesign 2026-11-04).
 */
@Immutable
data class CETCColors(
    val accent: Color,
    val accentStrong: Color,
    val onAccent: Color,
    val background: Color,
    val surface: Color,
    val surfaceContainer: Color,
    val chip: Color,
    val onChip: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val outline: Color,
    val warning: Color,
    val menuBg: Color,
    val menuText: Color
)

val LightCETCColors = CETCColors(
    accent = Color(0xFF005FAC),          // Morphe light primary
    accentStrong = Color(0xFF00497F),
    onAccent = Color(0xFFFFFFFF),
    background = Color(0xFFF6F6F6),
    surface = Color(0xFFFFFFFF),         // Morphe light surface
    surfaceContainer = Color(0xFFEEEDF1),// Morphe light surfaceContainer
    chip = Color(0xFFD4E3FF),            // Morphe light primaryContainer
    onChip = Color(0xFF001C38),
    textPrimary = Color(0xFF1A1C1E),
    textSecondary = Color(0xFF43474E),
    divider = Color(0xFFE3E2E6),
    outline = Color(0xFFC3C6CF),
    warning = Color(0xFF9A4500),
    menuBg = Color(0xFF2B2B2B),
    menuText = Color(0xFFF5F5F5)
)

val DarkCETCColors = CETCColors(
    accent = Color(0xFFA4C9FF),          // Morphe dark primary
    accentStrong = Color(0xFF005FAC),
    onAccent = Color(0xFF001C38),
    background = Color(0xFF151515),      // slightly under Morphe dark background
    surface = Color(0xFF222A35),         // Frosted dark card
    surfaceContainer = Color(0xFF222A35),// Morphe dark surfaceContainer
    chip = Color(0xFF00497F),            // dark primaryContainer
    onChip = Color(0xFFD4E3FF),
    textPrimary = Color(0xFFE2E2E5),
    textSecondary = Color(0xFFC3C6CF),
    divider = Color(0xFF2F3133),
    outline = Color(0xFF43474E),
    warning = Color(0xFFFFB877),
    menuBg = Color(0xFF2B2B2B),
    menuText = Color(0xFFF5F5F5)
)

val LocalCETCColors = androidx.compose.runtime.compositionLocalOf { LightCETCColors }

// Legacy token names, now theme-aware.
val OneUIBlue: Color @Composable get() = LocalCETCColors.current.accent
val OneUIBlueDark: Color @Composable get() = LocalCETCColors.current.accentStrong
val OneUIBackground: Color @Composable get() = LocalCETCColors.current.background
val OneUISurface: Color @Composable get() = LocalCETCColors.current.surface
val OneUISurfaceContainer: Color @Composable get() = LocalCETCColors.current.surfaceContainer
val OneUISectionBlue: Color @Composable get() = LocalCETCColors.current.accent
val OneUIChipBlue: Color @Composable get() = LocalCETCColors.current.chip
val OneUITextPrimary: Color @Composable get() = LocalCETCColors.current.textPrimary
val OneUITextSecondary: Color @Composable get() = LocalCETCColors.current.textSecondary
val OneUIDivider: Color @Composable get() = LocalCETCColors.current.divider
val OneUIOutline: Color @Composable get() = LocalCETCColors.current.outline
val OneUIWarning: Color @Composable get() = LocalCETCColors.current.warning
val OneUIMenuBg: Color @Composable get() = LocalCETCColors.current.menuBg
val OneUIMenuText: Color @Composable get() = LocalCETCColors.current.menuText
