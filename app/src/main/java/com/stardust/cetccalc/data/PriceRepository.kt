package com.stardust.cetccalc.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PriceRepository(private val context: Context, private val db: AppDatabase) {

    private val dao = db.dao()

    val items: Flow<List<TestItem>> = dao.observeAll()
    val dbVersion: Flow<String> = dao.observeMeta(KEY_VERSION).map { it ?: "—" }

    suspend fun seedIfEmpty() {
        if (dao.count() > 0) return
        val text = context.assets.open(SEED_FILE).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val parsed = CsvParser.parse(text)
        dao.insertAll(parsed.items)
        dao.setMeta(DbMeta(KEY_VERSION, parsed.version ?: DEFAULT_VERSION))
    }

    /** Replaces the whole database with the parsed CSV. Returns the applied version. */
    suspend fun importCsv(text: String, sourceName: String?): String {
        val parsed = CsvParser.parse(text)
        val version = parsed.version
            ?: Regex("""(\d{4})""").find(sourceName ?: "")?.value
            ?: System.currentTimeMillis().let { "custom" }
        dao.deleteAll()
        dao.insertAll(parsed.items)
        dao.setMeta(DbMeta(KEY_VERSION, version))
        return version
    }

    suspend fun exportCsv(currentVersion: String, items: List<TestItem>): String {
        return CsvParser.export(currentVersion, items)
    }

    suspend fun nextId(): Int = (dao.maxId() ?: 0) + 1

    suspend fun save(item: TestItem) = dao.insert(item)

    suspend fun update(item: TestItem) = dao.update(item)

    suspend fun delete(id: Int) = dao.deleteById(id)

    companion object {
        const val KEY_VERSION = "db_version"
        const val SEED_FILE = "prices_2026.csv"
        const val DEFAULT_VERSION = "2026"
    }
}
