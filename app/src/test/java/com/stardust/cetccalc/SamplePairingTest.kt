package com.stardust.cetccalc

import com.stardust.cetccalc.data.PickerEntry
import com.stardust.cetccalc.data.SamplePairing
import com.stardust.cetccalc.data.TestItem
import com.stardust.cetccalc.util.formatMoney
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SamplePairingTest {

    // Mirrors the real prices_2026.csv rows for crushing tests (April 2026 list).
    private fun row(id: Int, name: String, price: Double, unit: String, min: Int?) =
        TestItem(id, "اختبارات الخرسانة والمتصلدة", name, "", price, unit, min, "")

    private val items = listOf(
        row(1, "بروكتور رمل", 1250.0, "عينة", 3),
        row(21, "تكسير جروات (للمكعب)", 50.0, "مكعب", 3),
        row(22, "تكسير جروات (للتقرير)", 150.0, "تقرير", 1),
        row(23, "تكسير مكعبات خرسانية (للمكعب)", 40.0, "مكعب", 3),
        row(24, "تكسير مكعبات خرسانية (للتقرير)", 80.0, "تقرير", 1),
        row(25, "تكسير أسطوانات خرسانية (للأسطوانة)", 100.0, "أسطوانة", 3),
        row(26, "تكسير أسطوانات خرسانية (تسوية أسطوانة)", 200.0, "أسطوانة", null),
        row(27, "تكسير أسطوانات خرسانية (للتقرير)", 100.0, "تقرير", 1)
    )

    @Test
    fun `cube cylinder and grout rows collapse into paired entries`() {
        val entries = SamplePairing.buildPickerEntries(items)
        val paired = entries.filterIsInstance<PickerEntry.Paired>()
        val singles = entries.filterIsInstance<PickerEntry.Single>()

        assertEquals(3, paired.size)
        assertEquals("مكعبات خرسانية", paired.first { it.sample.id == 23 }.sectionTitle)
        assertEquals("أسطوانات خرسانية", paired.first { it.sample.id == 25 }.sectionTitle)
        assertEquals("عينات جراوت", paired.first { it.sample.id == 21 }.sectionTitle)

        // Report rows no longer appear on their own; leveling and others stay single.
        assertEquals(listOf(1, 26), singles.map { it.item.id })
        assertEquals(5, entries.size)
        // Pair takes the sample row's list position.
        assertEquals(23, entries[2].id)
    }

    @Test
    fun `paired cost is samples times price plus reports times price`() {
        // Cubes: 3 * 40 + 2 * 80 = 280, VAT 39.2, total 319.2
        val cost = SamplePairing.pairedCost(40.0, 3, 80.0, 2)
        assertEquals(280.0, cost, 0.001)
        assertEquals(39.2, cost * 0.14, 0.001)
        assertEquals(319.2, cost * 1.14, 0.001)

        // Cylinders: 3 * 100 + 1 * 100 = 400 -> 456 with VAT
        assertEquals(400.0, SamplePairing.pairedCost(100.0, 3, 100.0, 1), 0.001)

        // Grout: 3 * 50 + 1 * 150 = 300 -> 342 with VAT
        assertEquals(300.0, SamplePairing.pairedCost(50.0, 3, 150.0, 1), 0.001)

        // Zero reports is allowed
        assertEquals(120.0, SamplePairing.pairedCost(40.0, 3, 80.0, 0), 0.001)
    }

    @Test
    fun `paired cost formats like the approved mockup`() {
        assertEquals("٢٨٠ ج.م", formatMoney(SamplePairing.pairedCost(40.0, 3, 80.0, 2)))
        assertEquals("٣١٩٫٢ ج.م", formatMoney(SamplePairing.pairedCost(40.0, 3, 80.0, 2) * 1.14))
    }

    @Test
    fun `labels match the requested wording`() {
        assertEquals("عدد المكعبات", SamplePairing.countLabel("مكعب"))
        assertEquals("عدد الأسطوانات", SamplePairing.countLabel("أسطوانة"))
        assertEquals("عدد التقارير", SamplePairing.countLabel("تقرير"))
        assertEquals("سعر المكعب", SamplePairing.priceLabel("مكعب"))
        assertEquals("سعر التقرير", SamplePairing.priceLabel("تقرير"))
        assertTrue(SamplePairing.countLabel("قراءة").isNotBlank())
    }
}
