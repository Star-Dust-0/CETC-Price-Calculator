package com.stardust.cetccalc.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stardust.cetccalc.data.TestItem
import com.stardust.cetccalc.ui.theme.OneUIBackground
import com.stardust.cetccalc.ui.theme.OneUIBlue
import com.stardust.cetccalc.ui.theme.OneUIChipBlue
import com.stardust.cetccalc.ui.theme.OneUISurface
import com.stardust.cetccalc.ui.theme.OneUITextPrimary
import com.stardust.cetccalc.ui.theme.OneUITextSecondary
import com.stardust.cetccalc.util.formatMoney

@Composable
fun DatabaseScreen(
    testItems: List<TestItem>,
    dbVersion: String,
    statusMessage: String?,
    onBack: () -> Unit,
    onImportText: (String, String?) -> Unit,
    onExportRequest: (writeTarget: (String) -> Unit) -> Unit,
    onSaveNew: (TestItem) -> Unit,
    onUpdate: (TestItem) -> Unit,
    onDelete: (Int) -> Unit,
    nextId: suspend () -> Int
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf<TestItem?>(null) }
    var adding by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val name = runCatching {
                    context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                        val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (c.moveToFirst() && idx >= 0) c.getString(idx) else null
                    }
                }.getOrNull()
                val text = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader(Charsets.UTF_8).readText()
                }
                if (text != null) onImportText(text, name)
            } catch (e: Exception) {
                onImportText("", "قراءة-الملف-فشلت")
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            onExportRequest { csv ->
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(csv.toByteArray(Charsets.UTF_8))
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OneUIBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("backButton")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = OneUITextPrimary
                )
            }
            Text(
                text = "إدارة قاعدة البيانات",
                style = MaterialTheme.typography.titleLarge,
                color = OneUITextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { adding = true },
                modifier = Modifier.testTag("addItemButton")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "إضافة بند", tint = OneUIBlue)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = OneUISurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "إصدار قاعدة البيانات الحالي: $dbVersion",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = OneUIBlue,
                    modifier = Modifier.testTag("dbVersionLabel")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "عدد البنود: ${testItems.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OneUITextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        colors = ButtonDefaults.buttonColors(containerColor = OneUIBlue),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("importButton")
                    ) { Text("استيراد CSV") }
                    OutlinedButton(
                        onClick = { exportLauncher.launch("prices_$dbVersion.csv") },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("exportButton")
                    ) { Text("تصدير CSV", color = OneUIBlue) }
                }
                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OneUITextPrimary,
                        modifier = Modifier.testTag("statusMessage")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val grouped = testItems.groupBy { it.category }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            grouped.forEach { (category, categoryItems) ->
                item(key = "header_$category") {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = OneUIBlue,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
                    )
                }
                items(categoryItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { editing = item },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OneUISurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OneUITextPrimary
                                )
                                Text(
                                    text = "لكل ${item.unit}" + (item.minNote.takeIf { it.isNotBlank() }?.let { " · الحد الأدنى: $it" } ?: ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OneUITextSecondary
                                )
                            }
                            Text(
                                text = formatMoney(item.unitPrice),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = OneUIBlue
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (adding) {
        EditItemDialog(
            initial = null,
            nextId = nextId,
            onDismiss = { adding = false },
            onSave = { onSaveNew(it); adding = false },
            onDelete = null
        )
    }
    editing?.let { item ->
        EditItemDialog(
            initial = item,
            nextId = nextId,
            onDismiss = { editing = null },
            onSave = { onUpdate(it); editing = null },
            onDelete = { onDelete(item.id); editing = null }
        )
    }
}

@Composable
private fun EditItemDialog(
    initial: TestItem?,
    nextId: suspend () -> Int,
    onDismiss: () -> Unit,
    onSave: (TestItem) -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var price by remember { mutableStateOf(initial?.let { if (it.unitPrice % 1.0 == 0.0) it.unitPrice.toLong().toString() else it.unitPrice.toString() } ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }
    var minQty by remember { mutableStateOf(initial?.minQty?.toString() ?: "") }
    var minNote by remember { mutableStateOf(initial?.minNote ?: "") }
    var details by remember { mutableStateOf(initial?.details ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = OneUISurface,
        title = {
            Text(
                text = if (initial == null) "إضافة بند جديد" else "تعديل البند",
                color = OneUITextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialogField("اسم الاختبار", name) { name = it }
                DialogField("الفئة", category) { category = it }
                DialogField("سعر الوحدة (ج.م)", price, KeyboardType.Decimal) { price = it }
                DialogField("الوحدة (عينة، مكعب، كور…)", unit) { unit = it }
                DialogField("الحد الأدنى للكمية (اختياري)", minQty, KeyboardType.Number) { minQty = it }
                DialogField("ملاحظة الحد الأدنى (اختياري)", minNote) { minNote = it }
                DialogField("تفاصيل الاختبار", details, singleLine = false) { details = it }
                if (error != null) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedPrice = price.replace(Regex("[٠-٩]")) { ('0' + (it.value[0] - '٠')).toString() }
                        .replace("٬", "").replace(",", "").trim().toDoubleOrNull()
                    when {
                        name.isBlank() -> error = "اسم الاختبار مطلوب"
                        parsedPrice == null || parsedPrice < 0 -> error = "السعر غير صالح"
                        else -> {
                            val id = initial?.id ?: -1
                            val build: (Int) -> TestItem = { realId ->
                                TestItem(
                                    id = realId,
                                    category = category.ifBlank { "أخرى" },
                                    name = name.trim(),
                                    details = details.trim(),
                                    unitPrice = parsedPrice,
                                    unit = unit.ifBlank { "وحدة" },
                                    minQty = minQty.trim().toIntOrNull(),
                                    minNote = minNote.trim()
                                )
                            }
                            if (initial != null) {
                                onSave(build(id))
                            } else {
                                // For a new item we need the next id; run inline.
                                kotlinx.coroutines.runBlocking { onSave(build(nextId())) }
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("dialogSave")
            ) { Text("حفظ", color = OneUIBlue, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete, modifier = Modifier.testTag("dialogDelete")) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                TextButton(onClick = onDismiss) { Text("إلغاء", color = OneUITextSecondary) }
            }
        }
    )
}

@Composable
private fun DialogField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
