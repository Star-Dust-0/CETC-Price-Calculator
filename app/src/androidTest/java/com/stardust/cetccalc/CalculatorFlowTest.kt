package com.stardust.cetccalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CalculatorFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var shotIndex = 0

    private fun screenshot(name: String) {
        composeRule.waitForIdle()
        Thread.sleep(500) // let popup/system windows finish drawing before UiDevice capture
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val dir = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)!!
        dir.mkdirs()
        val file = File(dir, "${"%02d".format(shotIndex++)}_$name.png")
        val ok = device.takeScreenshot(file)
        android.util.Log.i("CETCShot", "takeScreenshot=$ok path=${file.absolutePath} exists=${file.exists()} len=${if (file.exists()) file.length() else -1}")
        check(ok && file.exists() && file.length() > 0) { "screenshot failed: ${file.absolutePath}" }
    }

    private fun toggleTaxAndAwaitTotal(expected: String) {
        android.util.Log.i("CETCTax", "before click: " + composeRule.onNodeWithTag("taxSwitch").fetchSemanticsNode().config.toString())
        composeRule.onNodeWithTag("taxSwitch").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("totalValue").assertTextEquals(expected); true }.getOrDefault(false)
        }
        android.util.Log.i("CETCTax", "after click: " + composeRule.onNodeWithTag("taxSwitch").fetchSemanticsNode().config.toString())
        android.util.Log.i("CETCTax", "total now: " + composeRule.onNodeWithTag("totalValue").fetchSemanticsNode().config.toString())
        composeRule.onNodeWithTag("totalValue").assertTextEquals(expected)
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
        composeRule.onNodeWithText("بروكتور رمل").performClick()
        composeRule.waitForIdle()

        // Unit price shown in the pricing card
        composeRule.onNodeWithTag("unitPriceValue").assertTextEquals("١٬٢٥٠ ج.م")
        screenshot("selected_default_qty")

        // Quantity 3 -> cost ٣٬٧٥٠, tax ٥٢٥, total ٤٬٢٧٥
        composeRule.onNodeWithTag("qtyField").performTextReplacement("3")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("costValue").assertTextEquals("٣٬٧٥٠ ج.م")
        composeRule.onNodeWithTag("taxValue").assertTextEquals("٥٢٥ ج.م")
        composeRule.onNodeWithTag("totalValue").assertTextEquals("٤٬٢٧٥ ج.م")
        screenshot("qty3_with_tax")

        // Tax off -> total equals cost
        toggleTaxAndAwaitTotal("٣٬٧٥٠ ج.م")
        screenshot("qty3_no_tax")
        toggleTaxAndAwaitTotal("٤٬٢٧٥ ج.م")

        // Below-minimum warning: مطرقة ارتداد min ٣٠ قراءة, qty 5
        composeRule.onNodeWithTag("testPicker").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching { composeRule.onNodeWithTag("pickerPopup").assertIsDisplayed(); true }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("pickerPopup").performScrollToNode(hasText("مطرقة ارتداد"))
        composeRule.onNodeWithText("مطرقة ارتداد").performClick()
        composeRule.onNodeWithTag("qtyField").performTextReplacement("5")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("calcScroll").performScrollToNode(hasText("الحد الأدنى للكمية", substring = true))
        composeRule.onNodeWithText("الحد الأدنى للكمية: ٣٠ قراءة").assertIsDisplayed()
        screenshot("min_warning")
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
