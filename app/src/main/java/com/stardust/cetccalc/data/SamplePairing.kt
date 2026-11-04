package com.stardust.cetccalc.data

/**
 * View-level pairing of the price list's separate "sample price" and "report price"
 * rows (e.g. cube crushing per cube + its results report) into one calculator entry
 * with two quantity inputs, per Mohamed's spec 2026-11-04.
 *
 * Pure presentation logic: the database rows and the versioned CSV format stay
 * exactly as the unit publishes them, so import/export and existing installs are
 * untouched; the pairing is derived from the item names at display time.
 */
sealed class PickerEntry {
    /** Selection id: the item's own id, or the sample item's id for a pair. */
    abstract val id: Int
    abstract val title: String
    abstract val category: String

    data class Single(val item: TestItem) : PickerEntry() {
        override val id: Int get() = item.id
        override val title: String get() = item.name
        override val category: String get() = item.category
    }

    data class Paired(val sample: TestItem, val report: TestItem, val sectionTitle: String) : PickerEntry() {
        override val id: Int get() = sample.id
        override val title: String get() = sectionTitle
        override val category: String get() = sample.category
    }
}

object SamplePairing {

    private val SAMPLE_SUFFIXES = listOf("(للمكعب)", "(للأسطوانة)")
    private const val REPORT_SUFFIX = "(للتقرير)"

    /** Mohamed's requested section titles (2026-11-04); fallback is the shared base name. */
    private val SECTION_TITLES = mapOf(
        "تكسير مكعبات خرسانية" to "مكعبات خرسانية",
        "تكسير أسطوانات خرسانية" to "أسطوانات خرسانية",
        "تكسير جروات" to "عينات جراوت"
    )

    /** Arabic plurals for the quantity field labels ("عدد المكعبات" etc.). */
    private val UNIT_PLURALS = mapOf(
        "مكعب" to "المكعبات",
        "أسطوانة" to "الأسطوانات",
        "تقرير" to "التقارير"
    )

    /** Definite singular for price row labels, e.g. "سعر المكعب" / "سعر التقرير". */
    private val UNIT_DEFINITE = mapOf(
        "مكعب" to "المكعب",
        "أسطوانة" to "الأسطوانة",
        "تقرير" to "التقرير"
    )

    /** Label for a unit-price row, e.g. "سعر المكعب". */
    fun priceLabel(unit: String): String = "سعر " + (UNIT_DEFINITE[unit] ?: unit)

    /** Label for a quantity input, e.g. "عدد المكعبات" / "عدد التقارير". */
    fun countLabel(unit: String): String = "عدد " + (UNIT_PLURALS[unit] ?: "الوحدات")

    /** Section cost: samples * sample price + reports * report price. */
    fun pairedCost(samplePrice: Double, sampleCount: Int, reportPrice: Double, reportCount: Int): Double =
        samplePrice * sampleCount + reportPrice * reportCount

    /**
     * Collapses "X (للمكعب)"/"X (للأسطوانة)" + "X (للتقرير)" pairs into one entry,
     * keeping the original list order and leaving every other item a [PickerEntry.Single].
     */
    fun buildPickerEntries(items: List<TestItem>): List<PickerEntry> {
        val byName = items.associateBy { it.name }
        val consumed = mutableSetOf<Int>()
        val entries = mutableListOf<PickerEntry>()
        for (item in items) {
            if (item.id in consumed) continue
            val suffix = SAMPLE_SUFFIXES.firstOrNull { item.name.endsWith(it) }
            if (suffix != null) {
                val base = item.name.removeSuffix(suffix).trim()
                val report = byName["$base $REPORT_SUFFIX"]
                if (report != null && report.id !in consumed) {
                    consumed += item.id
                    consumed += report.id
                    entries += PickerEntry.Paired(item, report, SECTION_TITLES[base] ?: base)
                    continue
                }
            }
            entries += PickerEntry.Single(item)
        }
        return entries
    }
}
