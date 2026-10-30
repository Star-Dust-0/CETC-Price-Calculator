package com.stardust.cetccalc.data

class CsvException(message: String) : Exception(message)

data class ParsedCsv(
    val version: String?,
    val items: List<TestItem>
)

object CsvParser {

    const val HEADER = "id,category,name,details,unit_price,unit,min_qty,min_note"

    /**
     * Parses a price-list CSV. Lines starting with '#' are comments;
     * "#version=XXXX" sets the database version label.
     */
    fun parse(text: String): ParsedCsv {
        var version: String? = null
        val items = mutableListOf<TestItem>()
        var sawHeader = false

        text.lines().forEachIndexed { index, rawLine ->
            val lineNumber = index + 1
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachIndexed
            if (line.startsWith("#")) {
                val body = line.removePrefix("#").trim()
                if (body.startsWith("version=")) {
                    version = body.removePrefix("version=").trim()
                }
                return@forEachIndexed
            }
            val fields = splitCsvLine(line, lineNumber)
            if (!sawHeader && fields.firstOrNull()?.trim()?.equals("id", ignoreCase = true) == true) {
                sawHeader = true
                return@forEachIndexed
            }
            if (fields.size < 8) {
                throw CsvException("سطر $lineNumber: متوقع 8 خانات ووجد ${fields.size}")
            }
            val id = fields[0].trim().toIntOrNull()
                ?: throw CsvException("سطر $lineNumber: رقم التعريف غير صالح")
            val name = fields[2].trim()
            if (name.isEmpty()) throw CsvException("سطر $lineNumber: اسم الاختبار فارغ")
            val price = fields[4].trim().replace("٬", "").replace(",", "").toDoubleOrNull()
                ?: throw CsvException("سطر $lineNumber: السعر غير صالح")
            if (price < 0) throw CsvException("سطر $lineNumber: السعر سالب")
            val minQty = fields[6].trim().ifEmpty { null }?.let {
                it.toIntOrNull() ?: throw CsvException("سطر $lineNumber: الحد الأدنى للكمية غير صالح")
            }
            items.add(
                TestItem(
                    id = id,
                    category = fields[1].trim().ifEmpty { "أخرى" },
                    name = name,
                    details = fields[3].trim(),
                    unitPrice = price,
                    unit = fields[5].trim().ifEmpty { "وحدة" },
                    minQty = minQty,
                    minNote = fields[7].trim()
                )
            )
        }
        if (items.isEmpty()) throw CsvException("الملف لا يحتوي على أي اختبارات")
        val ids = items.map { it.id }
        if (ids.toSet().size != ids.size) throw CsvException("يوجد أرقام تعريف مكررة في الملف")
        return ParsedCsv(version = version, items = items.sortedBy { it.id })
    }

    fun export(version: String, items: List<TestItem>): String {
        val sb = StringBuilder()
        sb.append("#version=").append(version).append('\n')
        sb.append(HEADER).append('\n')
        items.sortedBy { it.id }.forEach { item ->
            sb.append(item.id).append(',')
            sb.append(q(item.category)).append(',')
            sb.append(q(item.name)).append(',')
            sb.append(q(item.details)).append(',')
            sb.append(if (item.unitPrice % 1.0 == 0.0) item.unitPrice.toLong().toString() else item.unitPrice.toString()).append(',')
            sb.append(q(item.unit)).append(',')
            sb.append(item.minQty?.toString() ?: "").append(',')
            sb.append(q(item.minNote)).append('\n')
        }
        return sb.toString()
    }

    private fun q(value: String): String {
        return "\"" + value.replace("\"", "\"\"") + "\""
    }

    /** RFC-4180-ish line splitter supporting double-quoted fields. */
    private fun splitCsvLine(line: String, lineNumber: Int): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                inQuotes && c == '"' -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        cur.append('"'); i++
                    } else {
                        inQuotes = false
                    }
                }
                c == '"' && cur.isEmpty() -> inQuotes = true
                c == ',' && !inQuotes -> {
                    out.add(cur.toString()); cur.clear()
                }
                else -> cur.append(c)
            }
            i++
        }
        if (inQuotes) throw CsvException("سطر $lineNumber: علامة اقتباس غير مغلقة")
        out.add(cur.toString())
        return out
    }
}
