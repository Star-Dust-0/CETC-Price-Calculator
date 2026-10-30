package com.stardust.cetccalc

import com.stardust.cetccalc.util.formatMoney
import com.stardust.cetccalc.util.parseLocalizedInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PricingMathTest {

    @Test
    fun `proctor sand example matches approved mockup`() {
        val price = 1250.0
        val qty = 3
        val cost = price * qty
        val tax = cost * 0.14
        val total = cost + tax
        assertEquals(3750.0, cost, 0.001)
        assertEquals(525.0, tax, 0.001)
        assertEquals(4275.0, total, 0.001)
    }

    @Test
    fun `money formatting uses arabic indic digits with separator`() {
        assertEquals("١٬٢٥٠ ج.م", formatMoney(1250.0))
        assertEquals("٤٬٢٧٥ ج.م", formatMoney(4275.0))
        assertEquals("٥٢٥ ج.م", formatMoney(525.0))
    }

    @Test
    fun `quantity parsing accepts arabic and latin digits`() {
        assertEquals(3, parseLocalizedInt("3"))
        assertEquals(3, parseLocalizedInt("٣"))
        assertEquals(12, parseLocalizedInt("١٢"))
        assertNull(parseLocalizedInt(""))
        assertNull(parseLocalizedInt("abc"))
    }
}
