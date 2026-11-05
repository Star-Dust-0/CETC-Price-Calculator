package com.stardust.cetccalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stardust.cetccalc.data.PickerEntry
import com.stardust.cetccalc.data.SamplePairing
import com.stardust.cetccalc.util.ArabicSearch
import com.stardust.cetccalc.ui.components.FloatingTool
import com.stardust.cetccalc.ui.components.CalculatorDrawer
import com.stardust.cetccalc.ui.components.LocalGlassState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import com.stardust.cetccalc.ui.components.OneUISectionCard
import com.stardust.cetccalc.ui.components.PriceRow
import com.stardust.cetccalc.ui.components.RowDivider
import com.stardust.cetccalc.ui.components.ValueText
import com.stardust.cetccalc.ui.theme.OneUIBackground
import com.stardust.cetccalc.ui.theme.OneUIBlueDark
import com.stardust.cetccalc.ui.theme.OneUIBlue
import com.stardust.cetccalc.ui.theme.OneUIChipBlue
import com.stardust.cetccalc.ui.theme.OneUIMenuBg
import com.stardust.cetccalc.ui.theme.OneUIMenuText
import com.stardust.cetccalc.ui.theme.OneUIOutline
import com.stardust.cetccalc.ui.theme.OneUISurface
import com.stardust.cetccalc.ui.theme.OneUISurfaceContainer
import com.stardust.cetccalc.ui.theme.OneUITextPrimary
import com.stardust.cetccalc.ui.theme.OneUITextSecondary
import com.stardust.cetccalc.ui.theme.OneUIWarning
import com.stardust.cetccalc.util.formatCount
import com.stardust.cetccalc.util.formatMoney
import com.stardust.cetccalc.util.parseLocalizedInt

@Composable
fun CalculatorScreen(
    entries: List<PickerEntry>,
    dbVersion: String,
    selected: PickerEntry?,
    qtyText: String,
    qty2Text: String,
    onSelect: (Int) -> Unit,
    onQtyChange: (String) -> Unit,
    onQty2Change: (String) -> Unit,
    onQtyInc: () -> Unit,
    onQtyDec: () -> Unit,
    onQty2Inc: () -> Unit,
    onQty2Dec: () -> Unit,
    onOpenDatabase: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var searchOpen by remember { mutableStateOf(false) }
    var detailsExpanded by remember(selected?.id) { mutableStateOf(false) }
    var drawerOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val hazeState = remember { HazeState() }
    val cubeEntry = entries.filterIsInstance<PickerEntry.Paired>().firstOrNull { it.sectionTitle == "مكعبات خرسانية" }
    val groutEntry = entries.filterIsInstance<PickerEntry.Paired>().firstOrNull { it.sectionTitle == "عينات جراوت" }
    LaunchedEffect(selected?.id) { scrollState.scrollTo(0) }

    val qty = parseLocalizedInt(qtyText) ?: 0
    val qty2 = parseLocalizedInt(qty2Text) ?: 0
    val single = selected as? PickerEntry.Single
    val paired = selected as? PickerEntry.Paired
    val unitPrice = single?.item?.unitPrice ?: paired?.sample?.unitPrice ?: 0.0
    // Paired sample+report entries: samples * sample price + reports * report price (Mohamed's spec 2026-11-04)
    val cost = if (paired != null) {
        SamplePairing.pairedCost(paired.sample.unitPrice, qty, paired.report.unitPrice, qty2)
    } else {
        unitPrice * qty
    }
    // VAT 14% always applies (Mohamed's decision, 2026-10-30)
    val tax = cost * 0.14
    val total = cost + tax
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(LocalGlassState provides hazeState) {
    Box(Modifier.fillMaxSize().background(OneUIBackground).statusBarsPadding().navigationBarsPadding().imePadding()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 64.dp).hazeSource(hazeState)
            .verticalScroll(scrollState).testTag("calcScroll")
    ) {
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().padding(horizontal = 23.dp, vertical = 18.dp)) {
            Text("حاسبة الأسعار", style = MaterialTheme.typography.labelLarge, color = OneUITextSecondary)
            Text(
                when (selected?.id) {
                    cubeEntry?.id -> if (selected != null) "المكعبات الخرسانية" else "حاسبة أسعار الاختبارات"
                    groutEntry?.id -> "سعر الجراوت"
                    else -> "حاسبة أسعار الاختبارات"
                },
                style = MaterialTheme.typography.headlineMedium, color = OneUITextPrimary
            )
            Text("وحدة اختبارات واستشارات الهندسة المدنية · إصدار الأسعار: $dbVersion",
                style = MaterialTheme.typography.bodySmall, color = OneUITextSecondary)
        }

        // Test type picker: search-first (Mohamed 2026-11-04: "scrolling the list
        // every time is illogical" - the search field is the primary way to pick)
        OneUISectionCard(title = "نوع الاختبار") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("testPicker")
                    .clip(RoundedCornerShape(28.dp))
                    .background(OneUISurfaceContainer)
                    .border(1.5.dp, OneUIBlue, RoundedCornerShape(28.dp))
                    .clickable { searchOpen = true }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = OneUITextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selected?.title ?: "ابحث عن الاختبار أو الخدمة",
                        style = if (selected != null) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                        color = if (selected != null) OneUITextPrimary else OneUITextSecondary
                    )
                    if (selected != null) {
                        Text(
                            text = selected.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = OneUITextSecondary
                        )
                    }
                }
            }
        }

        if (searchOpen) {
            SearchPickerDialog(
                entries = entries,
                selectedId = selected?.id,
                onSelect = { id ->
                    onSelect(id)
                    searchOpen = false
                },
                onDismiss = { searchOpen = false }
            )
        }

        // Test details
        val detailItem = when (selected) {
            is PickerEntry.Single -> selected.item
            is PickerEntry.Paired -> selected.sample
            null -> null
        }
        if (detailItem != null && paired == null) {
            Spacer(modifier = Modifier.height(14.dp))
            OneUISectionCard(title = "تفاصيل الاختبار") {
                if (detailItem.details.isNotBlank()) {
                    Text(
                        text = detailItem.details,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OneUITextPrimary,
                        lineHeight = 24.sp
                    )
                }
                if (paired != null && paired.report.details.isNotBlank()) {
                    if (detailItem.details.isNotBlank()) Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = paired.report.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OneUITextSecondary,
                        lineHeight = 22.sp
                    )
                }
                if (detailItem.minNote.isNotBlank() || detailItem.minQty != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OneUIChipBlue)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "الحد الأدنى: " + (detailItem.minNote.ifBlank { formatCount(detailItem.minQty!!) + " " + detailItem.unit }),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OneUIBlue
                        )
                    }
                }
            }
        }

        // Pricing
        if (selected != null) {
            Spacer(modifier = Modifier.height(14.dp))
            OneUISectionCard(title = paired?.sectionTitle ?: "حساب التسعير") {
                if (paired != null) {
                    PriceRow(label = SamplePairing.priceLabel(paired.sample.unit)) {
                        ValueText(formatMoney(paired.sample.unitPrice), modifier = Modifier.testTag("unitPriceValue"))
                    }
                    RowDivider()
                    PriceRow(label = SamplePairing.priceLabel(paired.report.unit)) {
                        ValueText(formatMoney(paired.report.unitPrice), modifier = Modifier.testTag("reportPriceValue"))
                    }
                    RowDivider()
                    PriceRow(label = SamplePairing.countLabel(paired.sample.unit)) {
                        QtyStepper(value = qtyText, onValueChange = onQtyChange, onIncrement = onQtyInc, onDecrement = onQtyDec, tag = "qtyField") { focusManager.clearFocus() }
                    }
                    val sampleMin = paired.sample.minQty
                    if (sampleMin != null && qty in 1 until sampleMin) {
                        MinWarning("الحد الأدنى للكمية: ${formatCount(sampleMin)} ${paired.sample.unit}")
                    }
                    RowDivider()
                    PriceRow(label = SamplePairing.countLabel(paired.report.unit)) {
                        QtyStepper(value = qty2Text, onValueChange = onQty2Change, onIncrement = onQty2Inc, onDecrement = onQty2Dec, tag = "qty2Field") { focusManager.clearFocus() }
                    }
                    val reportMin = paired.report.minQty
                    if (reportMin != null && qty2 in 1 until reportMin) {
                        MinWarning("الحد الأدنى للكمية: ${formatCount(reportMin)} ${paired.report.unit}")
                    }
                    TextButton(onClick = { detailsExpanded = !detailsExpanded }) {
                        Text(if (detailsExpanded) "إخفاء التفاصيل" else "تفاصيل الاختبار والحد الأدنى")
                    }
                    AnimatedVisibility(detailsExpanded) {
                        Column {
                            Text(paired.sample.details, color = OneUITextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text(paired.report.details, color = OneUITextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text("الحد الأدنى: " + paired.sample.minNote.ifBlank { formatCount(paired.sample.minQty ?: 1) + " " + paired.sample.unit }, color = OneUIBlue, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    PriceRow(label = "سعر الوحدة") {
                        ValueText(formatMoney(unitPrice), modifier = Modifier.testTag("unitPriceValue"))
                    }
                    RowDivider()
                    PriceRow(label = "الكمية") {
                        QtyStepper(value = qtyText, onValueChange = onQtyChange, onIncrement = onQtyInc, onDecrement = onQtyDec, tag = "qtyField") { focusManager.clearFocus() }
                    }
                    val minQty = single?.item?.minQty
                    if (minQty != null && qty in 1 until minQty) {
                        MinWarning("الحد الأدنى للكمية: ${formatCount(minQty)} ${single!!.item.unit}")
                    }
                    RowDivider()
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cost and VAT cards: equal prominence to the total card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BigAmountCard(
                    label = "التكلفة",
                    value = formatMoney(cost),
                    tag = "costValue",
                    modifier = Modifier.weight(1f)
                )
                BigAmountCard(
                    label = "ضريبة القيمة المضافة ١٤٪",
                    sublabel = "عند طلب فاتورة إلكترونية",
                    value = formatMoney(tax),
                    tag = "taxValue",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = OneUIBlueDark)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الإجمالي",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "التكلفة + الضريبة",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = formatMoney(total),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        modifier = Modifier.testTag("totalValue")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Spacer(Modifier.height(24.dp))
    }
    Row(Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        FloatingTool(Icons.Outlined.Menu, "القائمة", "menuButton") { drawerOpen = true }
        Spacer(Modifier.weight(1f))
        Text("CETC", style = MaterialTheme.typography.labelLarge, color = OneUITextSecondary)
        Spacer(Modifier.weight(1f))
        FloatingTool(Icons.Outlined.Settings, "الإعدادات", "settingsButton", onOpenSettings)
    }
    CalculatorDrawer(
        open = drawerOpen, selectedId = selected?.id, cubeId = cubeEntry?.id, groutId = groutEntry?.id, dbVersion = dbVersion,
        onDismiss = { drawerOpen = false },
        onSelect = { id -> onSelect(id); drawerOpen = false },
        onSearch = { drawerOpen = false; searchOpen = true },
        onDatabase = { drawerOpen = false; onOpenDatabase() },
        onSettings = { drawerOpen = false; onOpenSettings() }
    )
    }
    }
}

@Composable
private fun QtyStepper(
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    tag: String,
    onDone: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.5.dp, OneUIOutline, CircleShape)
                .clickable(onClick = onDecrement)
                .testTag(tag + "Minus"),
            contentAlignment = Alignment.Center
        ) { Text("\u2212", color = OneUITextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.width(8.dp))
        QtyField(value = value, onValueChange = onValueChange, tag = tag, onDone = onDone)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(OneUIBlue)
                .clickable(onClick = onIncrement)
                .testTag(tag + "Plus"),
            contentAlignment = Alignment.Center
        ) { Text("+", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun BigAmountCard(
    label: String,
    value: String,
    tag: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = OneUISurfaceContainer)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = OneUITextSecondary)
            if (sublabel != null) {
                Text(sublabel, style = MaterialTheme.typography.bodySmall, color = OneUITextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = OneUITextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.testTag(tag)
            )
        }
    }
}

@Composable
private fun QtyField(
    value: String,
    onValueChange: (String) -> Unit,
    tag: String,
    onDone: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(46.dp)
            .border(1.5.dp, OneUIBlue, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = OneUITextPrimary,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(OneUIBlue),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag(tag)
        )
    }
}

@Composable
private fun MinWarning(text: String) {
    Text(
        text = text,
        color = OneUIWarning,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun OverflowMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = OneUIMenuText,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 13.dp)
    )
}

@Composable
private fun SearchPickerDialog(
    entries: List<PickerEntry>,
    selectedId: Int?,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val filtered = ArabicSearch.filterEntries(entries, query)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            color = OneUIBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = OneUITextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(OneUISurfaceContainer)
                            .border(1.5.dp, OneUIBlue, RoundedCornerShape(28.dp))
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium.copy(color = OneUITextPrimary),
                            cursorBrush = SolidColor(OneUIBlue),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .testTag("pickerSearchField"),
                            decorationBox = { inner ->
                                if (query.isEmpty()) {
                                    Text(
                                        text = "ابحث عن الاختبار أو الخدمة",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OneUITextSecondary
                                    )
                                }
                                inner()
                            }
                        )
                    }
                }
                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد نتائج مطابقة",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OneUITextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("pickerPopup")
                    ) {
                        items(filtered, key = { it.id }) { entry ->
                            val isSelected = entry.id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) OneUIChipBlue else Color.Transparent)
                                    .clickable { onSelect(entry.id) }
                                    .padding(horizontal = 24.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = OneUITextPrimary
                                    )
                                    Text(
                                        text = entry.category,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OneUITextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = when (entry) {
                                        is PickerEntry.Single -> formatMoney(entry.item.unitPrice)
                                        is PickerEntry.Paired ->
                                            formatMoney(entry.sample.unitPrice).removeSuffix(" ج.م") +
                                                " + " + formatMoney(entry.report.unitPrice)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OneUIBlue
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = OneUIBlue
                                    )
                                }
                            }
                            RowDivider()
                        }
                    }
                }
            }
        }
    }
}
