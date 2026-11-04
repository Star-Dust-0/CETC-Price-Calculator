package com.stardust.cetccalc.util

import com.stardust.cetccalc.data.PickerEntry
import com.stardust.cetccalc.data.TestItem

/**
 * Arabic-aware search for the test picker (Mohamed's request 2026-11-04).
 * Normalizes alef variants, yaa/alef maqsura, taa marbuta, hamza carriers and
 * diacritics/tatweel, then matches every query word as a substring in any order.
 */
object ArabicSearch {

    // Tashkeel (fathatan..sukun), dagger alef and tatweel/kashida.
    private val DIACRITICS = Regex("[\\u064B-\\u0652\\u0670\\u0640]")
    private val ALEF_VARIANTS = Regex("[أإآٱ]")
    private val WHITESPACE = Regex("\\s+")

    // Light suffix stemming (applied post-normalization) so خرسانة matches خرسانية
    // and اسطوانة matches أسطوانات: trailing يه / ات / ه / ي are ignored at match time.
    private val SUFFIX = Regex("(يه|ات|ه|ي)$")

    private fun stem(token: String): String = SUFFIX.replaceFirst(token, "")

    fun normalize(text: String): String {
        var s = text.lowercase()
        s = DIACRITICS.replace(s, "")
        s = ALEF_VARIANTS.replace(s, "ا")
        s = s.replace('ى', 'ي')
        s = s.replace('ئ', 'ي')
        s = s.replace('ؤ', 'و')
        s = s.replace('ة', 'ه')
        s = WHITESPACE.replace(s, " ").trim()
        return s
    }

    /**
     * True when every whitespace-separated query word appears in the haystack (any
     * order), either as a plain substring or via stemmed token matching.
     */
    fun matches(query: String, haystack: String): Boolean {
        val q = normalize(query)
        if (q.isEmpty()) return true
        val h = normalize(haystack)
        val hStems = h.split(' ').map { stem(it) }
        return q.split(' ').all { token ->
            token in h || stem(token).let { s -> s.isNotEmpty() && hStems.any { s in it } }
        }
    }

    fun itemHaystack(item: TestItem): String =
        "${item.name} ${item.details} ${item.category} ${item.unit}"

    fun entryHaystack(entry: PickerEntry): String = when (entry) {
        is PickerEntry.Single -> itemHaystack(entry.item)
        is PickerEntry.Paired ->
            entry.sectionTitle + " " + itemHaystack(entry.sample) + " " + itemHaystack(entry.report)
    }

    fun matchesEntry(entry: PickerEntry, query: String): Boolean = matches(query, entryHaystack(entry))

    fun filterEntries(entries: List<PickerEntry>, query: String): List<PickerEntry> =
        if (normalize(query).isEmpty()) entries else entries.filter { matchesEntry(it, query) }
}
