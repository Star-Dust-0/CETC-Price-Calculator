package com.stardust.cetccalc

import com.stardust.cetccalc.data.CsvParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CsvParserTest {

    private fun seedText(): String = File("src/main/assets/prices_2026.csv").readText(Charsets.UTF_8)

    @Test
    fun `seed file parses with 56 items and version 2026`() {
        val parsed = CsvParser.parse(seedText())
        assertEquals("2026", parsed.version)
        assertEquals(56, parsed.items.size)
        assertEquals(parsed.items.map { it.id }, (1..56).toList())
    }

    @Test
    fun `seed file prices match key source values`() {
        val parsed = CsvParser.parse(seedText())
        val byId = parsed.items.associateBy { it.id }
        assertEquals(250.0, byId[4]!!.unitPrice, 0.001)   // Sand cone
        assertEquals(1250.0, byId[5]!!.unitPrice, 0.001)  // بروكتور رمل
        assertEquals(1750.0, byId[6]!!.unitPrice, 0.001)  // بروكتور تربة أساس
        assertEquals(100.0, byId[25]!!.unitPrice, 0.001)  // تكسير أسطوانات للأسطوانة
        assertEquals(200.0, byId[26]!!.unitPrice, 0.001)  // تسوية
        assertEquals(100.0, byId[27]!!.unitPrice, 0.001)  // تقرير
        assertEquals(10000.0, byId[29]!!.unitPrice, 0.001) // تصميم خلطة
        assertEquals(5000.0, byId[49]!!.unitPrice, 0.001)  // صلاحية أسمنت
        assertEquals(30, byId[18]!!.minQty)                // مطرقة ارتداد
    }

    @Test
    fun `multi price items exist as separate entries`() {
        val parsed = CsvParser.parse(seedText())
        val cylinders = parsed.items.filter { it.name.startsWith("تكسير أسطوانات خرسانية") }
        assertEquals(3, cylinders.size)
        val cubes = parsed.items.filter { it.name.startsWith("تكسير مكعبات خرسانية") }
        assertEquals(2, cubes.size)
    }

    @Test
    fun `export then parse round trips`() {
        val parsed = CsvParser.parse(seedText())
        val exported = CsvParser.export("2026", parsed.items)
        val reparsed = CsvParser.parse(exported)
        assertEquals("2026", reparsed.version)
        assertEquals(parsed.items, reparsed.items)
    }

    @Test
    fun `parser rejects bad price`() {
        val bad = "#version=2026\nid,category,name,details,unit_price,unit,min_qty,min_note\n1,\"فئة\",\"اسم\",\"تفاصيل\",abc,\"عينة\",1,\"\"\n"
        try {
            CsvParser.parse(bad)
            throw AssertionError("should have thrown")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("السعر"))
        }
    }
}
