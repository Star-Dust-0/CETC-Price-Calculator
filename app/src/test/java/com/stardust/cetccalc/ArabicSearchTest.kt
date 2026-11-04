package com.stardust.cetccalc

import com.stardust.cetccalc.data.PickerEntry
import com.stardust.cetccalc.data.SamplePairing
import com.stardust.cetccalc.data.TestItem
import com.stardust.cetccalc.util.ArabicSearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArabicSearchTest {

    private fun item(name: String, details: String = "", category: String = "") =
        TestItem(1, category, name, details, 100.0, "عينة", null, "")

    @Test
    fun `normalization folds alef yaa taa marbuta and hamza carriers`() {
        assertEquals("اسطوانات", ArabicSearch.normalize("أسطوانات"))
        assertEquals("اسطوانات", ArabicSearch.normalize("أسطوانات".replace('أ', 'إ')))
        assertEquals("اسطوانات", ArabicSearch.normalize("أسطوانات".replace('أ', 'آ')))
        assertEquals("خرسانه", ArabicSearch.normalize("خرسانة"))
        assertEquals("معالجه", ArabicSearch.normalize("معالجة"))
        assertEquals("موسي", ArabicSearch.normalize("موسى"))
        assertEquals("سوال", ArabicSearch.normalize("سؤال"))
    }

    @Test
    fun `diacritics and tatweel are stripped`() {
        assertEquals("خرسانه", ArabicSearch.normalize("خَرَسَانَة"))
        assertEquals("خرسانه", ArabicSearch.normalize("خرســانة"))
    }

    @Test
    fun `partial and multi word match in any order`() {
        val h = "تكسير مكعبات خرسانية تحديد اجهاد الضغط"
        assertTrue(ArabicSearch.matches("مكعبات", h))
        assertTrue(ArabicSearch.matches("تكسير مكعبات", h))
        assertTrue(ArabicSearch.matches("مكعبات تكسير", h))
        assertTrue(ArabicSearch.matches("ضغط", h))
        assertFalse(ArabicSearch.matches("تربه", h))
    }

    @Test
    fun `query with different spelling still matches`() {
        val h = "تكسير أسطوانات خرسانية"
        assertTrue(ArabicSearch.matches("اسطوانات", h))
        assertTrue(ArabicSearch.matches("تكسير اسطوانات خرسانه", h))
        // singular query matches plural/suffixed haystack and vice versa
        assertTrue(ArabicSearch.matches("اسطوانه", h))
        assertTrue(ArabicSearch.matches("خرسانة", h))
        assertTrue(ArabicSearch.matches("اختبار", "اختبارات الخرسانة والمتصلدة"))
    }

    @Test
    fun `empty or blank query matches everything`() {
        assertTrue(ArabicSearch.matches("", "أي نص"))
        assertTrue(ArabicSearch.matches("   ", "أي نص"))
    }

    @Test
    fun `entry filtering covers paired titles and details`() {
        val sample = item("تكسير مكعبات خرسانية (للمكعب)", "تحديد اجهاد الضغط", "اختبارات الخرسانة والمتصلدة")
        val report = item("تكسير مكعبات خرسانية (للتقرير)", "تقرير نتائج اختبارات المكعبات", "اختبارات الخرسانة والمتصلدة")
        val other = item("بروكتور رمل")
        val entries = listOf(
            PickerEntry.Paired(sample, report, "مكعبات خرسانية"),
            PickerEntry.Single(other)
        )
        // by section title
        assertEquals(1, ArabicSearch.filterEntries(entries, "مكعبات خرسانيه").size)
        // by report-only wording
        assertEquals(1, ArabicSearch.filterEntries(entries, "نتائج").size)
        // by sample row name
        assertEquals(1, ArabicSearch.filterEntries(entries, "للمكعب").size)
        // no match
        assertEquals(0, ArabicSearch.filterEntries(entries, "تربه").size)
        // empty query returns all
        assertEquals(2, ArabicSearch.filterEntries(entries, "").size)
    }

    @Test
    fun `buildPickerEntries output stays searchable`() {
        val items = listOf(
            TestItem(23, "c", "تكسير مكعبات خرسانية (للمكعب)", "", 40.0, "مكعب", 3, ""),
            TestItem(24, "c", "تكسير مكعبات خرسانية (للتقرير)", "", 80.0, "تقرير", 1, "")
        )
        val entries = SamplePairing.buildPickerEntries(items)
        assertEquals(1, entries.size)
        assertTrue(ArabicSearch.matchesEntry(entries[0], "مكعبات"))
        assertTrue(ArabicSearch.matchesEntry(entries[0], "تقرير"))
    }
}
