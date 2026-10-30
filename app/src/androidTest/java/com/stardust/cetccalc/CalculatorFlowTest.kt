package com.stardust.cetccalc

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class CalculatorFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var shotIndex = 0

    private fun screenshot(name: String) {
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage()
        val pixels = IntArray(image.width * image.height)
        image.readPixels(pixels)
        val bitmap = Bitmap.createBitmap(pixels, image.width, image.height, Bitmap.Config.ARGB_8888)
        val dir = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)!!
        val file = File(dir, "${"%02d".format(shotIndex++)}_$name.png")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
    }

    @Test
    fun fullCalculatorFlowMatchesMockupMath() {
        // Wait for seed import + UI
        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching { composeRule.onNodeWithTag("testPicker").assertIsDisplayed(); true }.getOrDefault(false)
        }
        screenshot("home_empty")

        // Open picker and choose بروكتور رمل
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithText("بروكتور رمل").assertIsDisplayed(); true }.getOrDefault(false)
        }
        screenshot("picker_open")
        composeRule.onNodeWithText("بروكتور رمل").performClick()
        composeRule.waitForIdle()

        // Unit price ١٬٢٥٠ ج.م shown
        composeRule.onNodeWithText("١٬٢٥٠ ج.م").assertIsDisplayed()
        screenshot("selected_default_qty")

        // Quantity 3 -> cost ٣٬٧٥٠, tax ٥٢٥, total ٤٬٢٧٥
        composeRule.onNodeWithTag("qtyField").performTextReplacement("3")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("٣٬٧٥٠ ج.م").assertIsDisplayed()
        composeRule.onNodeWithText("٥٢٥ ج.م").assertIsDisplayed()
        composeRule.onNodeWithText("٤٬٢٧٥ ج.م").assertIsDisplayed()
        screenshot("qty3_with_tax")

        // Tax off -> total ٣٬٧٥٠
        composeRule.onNodeWithTag("taxSwitch").performClick()
        composeRule.waitForIdle()
        val totals = composeRule.onAllNodes(hasText("٣٬٧٥٠ ج.م"))
        assertTrue("cost and total should both show ٣٬٧٥٠", totals.fetchSemanticsNodes().size >= 2)
        screenshot("qty3_no_tax")

        // Below-minimum warning: مطرقة ارتداد min 30, qty 5
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("مطرقة ارتداد").performClick()
        composeRule.onNodeWithTag("qtyField").performTextReplacement("5")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("الحد الأدنى للكمية: ٣٠ قراءة").assertIsDisplayed()
        screenshot("min_warning")
    }

    @Test
    fun databaseAndAboutScreens() {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching { composeRule.onNodeWithTag("testPicker").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("menuButton").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("إدارة قاعدة البيانات").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("dbVersionLabel").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithText("إصدار قاعدة البيانات الحالي: 2026").assertIsDisplayed()
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
