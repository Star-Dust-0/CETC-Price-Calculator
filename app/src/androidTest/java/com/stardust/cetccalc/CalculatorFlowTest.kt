package com.stardust.cetccalc

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.graphics.asAndroidBitmap

import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.content.ContentValues
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class CalculatorFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var shotIndex = 0

    private fun screenshot(name: String) {
        composeRule.waitForIdle()
        Thread.sleep(500) // let popup/system windows finish drawing before UiDevice capture
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = context.getExternalFilesDir(null)!!
        dir.mkdirs()
        val file = File(dir, "${"%02d".format(shotIndex++)}_$name.png")
        val ok = device.takeScreenshot(file)
        android.util.Log.i("CETCShot", "takeScreenshot=$ok path=${file.absolutePath} exists=${file.exists()} len=${if (file.exists()) file.length() else -1}")
        check(ok && file.exists() && file.length() > 0) { "screenshot failed: ${file.absolutePath}" }
        // Mirror into the public Pictures collection so CI can pull it: the app-private
        // external dir is invisible to adb, and the app is uninstalled when tests finish.
        mirrorToPictures(context, file)
    }

    private fun captureNodePng(tag: String, name: String) {
        composeRule.waitForIdle()
        val bitmap = composeRule.onNodeWithTag(tag).captureToImage().asAndroidBitmap()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = context.getExternalFilesDir(null)!!.apply { mkdirs() }
        val file = File(dir, "${"%02d".format(shotIndex++)}_$name.png")
        FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        android.util.Log.i("CETCShot", "node capture path=${file.absolutePath} len=${file.length()}")
        mirrorToPictures(context, file)
    }

    private fun mirrorToPictures(context: android.content.Context, file: File) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CETC-CI")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out -> file.inputStream().use { it.copyTo(out) } }
        }
    }

    private fun waitForPicker() {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching { composeRule.onNodeWithTag("testPicker").assertIsDisplayed(); true }.getOrDefault(false)
        }
    }

    @Test
    fun fullCalculatorFlowMatchesMockupMath() {
        waitForPicker()
        screenshot("home_empty")

        // Open picker and choose بروكتور رمل
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithText("بروكتور رمل").assertIsDisplayed(); true }.getOrDefault(false)
        }
        screenshot("picker_open")
        captureNodePng("pickerPopup", "01b_picker_dropdown")
        composeRule.onNodeWithText("بروكتور رمل").performClick()
        composeRule.waitForIdle()

        // Unit price shown in the pricing card
        composeRule.onNodeWithTag("unitPriceValue").assertTextEquals("١٬٢٥٠ ج.م")
        screenshot("selected_default_qty")

        // Quantity 3 -> cost ٣٬٧٥٠, tax ٥٢٥, total ٤٬٢٧٥
        composeRule.onNodeWithTag("qtyField").performTextReplacement("3")
        composeRule.onNodeWithTag("qtyField").performImeAction()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("costValue").assertTextEquals("٣٬٧٥٠ ج.م")
        composeRule.onNodeWithTag("taxValue").assertTextEquals("٥٢٥ ج.م")
        composeRule.onNodeWithTag("totalValue").assertTextEquals("٤٬٢٧٥ ج.م")
        screenshot("qty3_with_tax")

        // VAT is always on: no switch exists, total always includes the 14%
        composeRule.onAllNodesWithTag("taxSwitch").assertCountEquals(0)

        // Below-minimum warning: مطرقة ارتداد min ٣٠ قراءة, qty 5
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("pickerPopup").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("pickerPopup").performScrollToNode(hasText("مطرقة ارتداد"))
        composeRule.onNodeWithText("مطرقة ارتداد").performClick()
        composeRule.onNodeWithTag("qtyField").performTextReplacement("5")
        composeRule.onNodeWithTag("qtyField").performImeAction()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("calcScroll").performScrollToNode(hasText("الحد الأدنى للكمية", substring = true))
        composeRule.onNodeWithText("الحد الأدنى للكمية: ٣٠ قراءة").assertIsDisplayed()
        screenshot("min_warning")
    }

    @Test
    fun darkThemeScreenshots() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        try {
            automation.executeShellCommand("cmd uimode night yes").close()
            waitForPicker()
            composeRule.waitForIdle()
            Thread.sleep(1200) // let the recreated activity settle
            screenshot("home_dark")

            composeRule.onNodeWithTag("testPicker").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                runCatching { composeRule.onNodeWithTag("pickerSearchField").assertIsDisplayed(); true }.getOrDefault(false)
            }
            composeRule.onNodeWithTag("pickerSearchField").performTextReplacement("مكعبات")
            composeRule.waitForIdle()
            screenshot("search_dark")
            composeRule.onNodeWithText("مكعبات خرسانية").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("qtyField").performTextReplacement("3")
            composeRule.onNodeWithTag("qty2Field").performTextReplacement("2")
            composeRule.onNodeWithTag("qty2Field").performImeAction()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("totalValue").assertTextEquals("٣١٩٫٢ ج.م")
            screenshot("paired_dark")
        } finally {
            automation.executeShellCommand("cmd uimode night no").close()
        }
    }

    @Test
    fun searchFiltersThePickerInstantly() {
        waitForPicker()
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("pickerSearchField").assertIsDisplayed(); true }.getOrDefault(false)
        }
        // Full list first (empty query), then instant Arabic-normalized filtering
        composeRule.onNodeWithTag("pickerPopup").performScrollToNode(hasText("بروكتور رمل"))
        composeRule.onNodeWithText("بروكتور رمل").assertIsDisplayed()
        composeRule.onNodeWithTag("pickerSearchField").performTextReplacement("اسطوانات")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("أسطوانات خرسانية").assertIsDisplayed()
        composeRule.onNodeWithText("بروكتور رمل").assertDoesNotExist()
        screenshot("search_filter")
        // Multi-word, any order
        composeRule.onNodeWithTag("pickerSearchField").performTextReplacement("خرسانيه اسطوانات")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("أسطوانات خرسانية").assertIsDisplayed()
        composeRule.onNodeWithText("أسطوانات خرسانية").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("calcScroll").performScrollToNode(hasTestTag("qty2Field"))
        composeRule.onNodeWithTag("qty2Field").assertIsDisplayed()
    }

    @Test
    fun pairedSampleAndReportPricing() {
        waitForPicker()
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("pickerPopup").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("pickerPopup").performScrollToNode(hasText("مكعبات خرسانية"))
        composeRule.onNodeWithText("مكعبات خرسانية").performClick()
        composeRule.waitForIdle()

        // Two unit prices, two quantity inputs: 3 cubes * 40 + 2 reports * 80 = 280
        composeRule.onNodeWithTag("unitPriceValue").assertTextEquals("٤٠ ج.م")
        composeRule.onNodeWithTag("reportPriceValue").assertTextEquals("٨٠ ج.م")
        composeRule.onNodeWithTag("qtyField").performTextReplacement("3")
        composeRule.onNodeWithTag("qtyField").performImeAction()
        composeRule.onNodeWithTag("qty2Field").performTextReplacement("2")
        composeRule.onNodeWithTag("qty2Field").performImeAction()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("costValue").assertTextEquals("٢٨٠ ج.م")
        composeRule.onNodeWithTag("taxValue").assertTextEquals("٣٩٫٢ ج.م")
        composeRule.onNodeWithTag("totalValue").assertTextEquals("٣١٩٫٢ ج.م")
        screenshot("paired_cubes_reports")
    }

    @Test
    fun databaseAndAboutScreens() {
        waitForPicker()
        composeRule.onNodeWithTag("menuButton").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("إدارة قاعدة البيانات").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("dbVersionLabel").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("dbVersionLabel").assertTextEquals("إصدار قاعدة البيانات الحالي: 2026")
        screenshot("database_screen")
        composeRule.onNodeWithTag("backButton").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("menuButton").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("حول التطبيق").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Brought to you by Instinct").assertIsDisplayed()
        screenshot("about_screen")
    }
}
