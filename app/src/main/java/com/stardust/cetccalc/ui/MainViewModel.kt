package com.stardust.cetccalc.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stardust.cetccalc.data.AppDatabase
import com.stardust.cetccalc.data.CsvException
import com.stardust.cetccalc.data.PriceRepository
import com.stardust.cetccalc.data.TestItem
import com.stardust.cetccalc.util.parseLocalizedInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : ViewModel() {

    private val repo = PriceRepository(app, AppDatabase.get(app))

    val items: StateFlow<List<TestItem>> = repo.items
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dbVersion: StateFlow<String> = repo.dbVersion
        .stateIn(viewModelScope, SharingStarted.Eagerly, "2026")

    private val _selectedId = MutableStateFlow<Int?>(null)
    val selectedId: StateFlow<Int?> = _selectedId

    private val _qtyText = MutableStateFlow("1")
    val qtyText: StateFlow<String> = _qtyText

    // Second quantity for paired sample+report entries (e.g. number of reports).
    private val _qty2Text = MutableStateFlow("1")
    val qty2Text: StateFlow<String> = _qty2Text

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    init {
        viewModelScope.launch { repo.seedIfEmpty() }
    }

    fun select(id: Int) { _selectedId.value = id }

    fun setQty(text: String) {
        // Keep only digit characters (Latin or Arabic-Indic), max 6 digits.
        val filtered = text.filter { it.isDigit() || it in '٠'..'٩' || it in '۰'..'۹' }.take(6)
        _qtyText.value = filtered
    }

    fun setQty2(text: String) {
        val filtered = text.filter { it.isDigit() || it in '٠'..'٩' || it in '۰'..'۹' }.take(6)
        _qty2Text.value = filtered
    }

    fun clearStatus() { _statusMessage.value = null }

    fun quantity(): Int = parseLocalizedInt(_qtyText.value) ?: 0

    fun quantity2(): Int = parseLocalizedInt(_qty2Text.value) ?: 0

    fun selectedItem(): TestItem? = items.value.firstOrNull { it.id == _selectedId.value }

    fun importCsv(text: String, sourceName: String?, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val version = repo.importCsv(text, sourceName)
                _selectedId.value = null
                _statusMessage.value = "تم استيراد قاعدة البيانات بنجاح — الإصدار $version"
                onDone(true)
            } catch (e: CsvException) {
                _statusMessage.value = "فشل الاستيراد: ${e.message}"
                onDone(false)
            } catch (e: Exception) {
                _statusMessage.value = "فشل الاستيراد: الملف غير صالح"
                onDone(false)
            }
        }
    }

    fun exportCsv(onReady: (String) -> Unit) {
        _statusMessage.value = null
        onReady(com.stardust.cetccalc.data.CsvParser.export(dbVersion.value, items.value))
    }

    fun saveNew(item: TestItem) {
        viewModelScope.launch {
            repo.save(item)
            _statusMessage.value = "تمت إضافة «${item.name}»"
        }
    }

    fun updateItem(item: TestItem) {
        viewModelScope.launch {
            repo.update(item)
            _statusMessage.value = "تم حفظ التعديلات"
        }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            repo.delete(id)
            if (_selectedId.value == id) _selectedId.value = null
            _statusMessage.value = "تم حذف البند"
        }
    }

    suspend fun nextId(): Int = repo.nextId()

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY]!!
                MainViewModel(app)
            }
        }
    }
}
