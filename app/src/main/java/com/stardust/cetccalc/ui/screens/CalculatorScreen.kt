package com.stardust.cetccalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.stardust.cetccalc.data.TestItem
import com.stardust.cetccalc.ui.components.CreditFooter
import com.stardust.cetccalc.ui.components.OneUISectionCard
import com.stardust.cetccalc.ui.components.PriceRow
import com.stardust.cetccalc.ui.components.RowDivider
import com.stardust.cetccalc.ui.components.ValueText
import com.stardust.cetccalc.ui.theme.OneUIBackground
import com.stardust.cetccalc.ui.theme.OneUIBlue
import com.stardust.cetccalc.ui.theme.OneUIChipBlue
import com.stardust.cetccalc.ui.theme.OneUIMenuBg
import com.stardust.cetccalc.ui.theme.OneUIMenuText
import com.stardust.cetccalc.ui.theme.OneUIOutline
import com.stardust.cetccalc.ui.theme.OneUISurface
import com.stardust.cetccalc.ui.theme.OneUITextPrimary
import com.stardust.cetccalc.ui.theme.OneUITextSecondary
import com.stardust.cetccalc.ui.theme.OneUIWarning
import com.stardust.cetccalc.util.formatCount
import com.stardust.cetccalc.util.formatMoney

@Composable
fun CalculatorScreen(
    items: List<TestItem>,
    dbVersion: String,
    selected: TestItem?,
    qtyText: String,
    taxEnabled: Boolean,
    onSelect: (Int) -> Unit,
    onQtyChange: (String) -> Unit,
    onTaxChange: (Boolean) -> Unit,
    onOpenDatabase: () -> Unit,
    onOpenAbout: () -> Unit
) {
    var pickerExpanded by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val qty = qtyText.toIntOrNull() ?: com.stardust.cetccalc.util.parseLocalizedInt(qtyText) ?: 0
    val unitPrice = selected?.unitPrice ?: 0.0
    val cost = unitPrice * qty
    val tax = if (taxEnabled) cost * 0.14 else 0.0
    val total = cost + tax
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OneUIBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .testTag("calcScroll")
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 56.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "حاسبة أسعار الاختبارات",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OneUITextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "وحدة اختبارات واستشارات الهندسة المدنية · إصدار الأسعار: $dbVersion",
                    style = MaterialTheme.typography.bodySmall,
                    color = OneUITextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("menuButton")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "القائمة",
                        tint = OneUITextPrimary
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(OneUIMenuBg, RoundedCornerShape(16.dp))
                ) {
                    OverflowMenuItem("إدارة قاعدة البيانات") {
                        menuExpanded = false
                        onOpenDatabase()
                    }
                    OverflowMenuItem("حول التطبيق") {
                        menuExpanded = false
                        onOpenAbout()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Test type picker
        OneUISectionCard(title = "نوع الاختبار") {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testPicker")
                        .border(1.5.dp, OneUIOutline, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { pickerExpanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected?.name ?: "اختر الاختبار",
                            style = MaterialTheme.typography.titleMedium,
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
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OneUITextSecondary
                    )
                }
                DropdownMenu(
                    expanded = pickerExpanded,
                    onDismissRequest = { pickerExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .heightIn(max = 480.dp)
                        .background(OneUISurface, RoundedCornerShape(20.dp))
                        .testTag("pickerPopup")
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = item.id == selected?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) OneUIChipBlue else Color.Transparent)
                                .clickable {
                                    onSelect(item.id)
                                    pickerExpanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = OneUITextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatMoney(item.unitPrice),
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
                        if (index < items.lastIndex) {
                            RowDivider()
                        }
                    }
                }
            }
        }

        // Test details
        if (selected != null) {
            Spacer(modifier = Modifier.height(14.dp))
            OneUISectionCard(title = "تفاصيل الاختبار") {
                if (selected.details.isNotBlank()) {
                    Text(
                        text = selected.details,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OneUITextPrimary,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                if (selected.minNote.isNotBlank() || selected.minQty != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OneUIChipBlue)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "الحد الأدنى: " + (selected.minNote.ifBlank { formatCount(selected.minQty!!) + " " + selected.unit }),
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
            OneUISectionCard(title = "حساب التسعير") {
                PriceRow(label = "سعر الوحدة") {
                    ValueText(formatMoney(unitPrice), modifier = Modifier.testTag("unitPriceValue"))
                }
                RowDivider()
                PriceRow(label = "الكمية") {
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(46.dp)
                            .border(1.5.dp, OneUIOutline, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = qtyText,
                            onValueChange = onQtyChange,
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
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .testTag("qtyField")
                        )
                    }
                }
                val minQty = selected.minQty
                if (minQty != null && qty in 1 until minQty) {
                    Text(
                        text = "الحد الأدنى للكمية: ${formatCount(minQty)} ${selected.unit}",
                        color = OneUIWarning,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                RowDivider()
                PriceRow(label = "التكلفة") {
                    ValueText(formatMoney(cost), modifier = Modifier.testTag("costValue"))
                }
                RowDivider()
                PriceRow(
                    label = "ضريبة القيمة المضافة (١٤٪)",
                    sublabel = "عند طلب فاتورة إلكترونية"
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (taxEnabled) {
                            ValueText(formatMoney(tax), modifier = Modifier.testTag("taxValue"))
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Switch(
                            checked = taxEnabled,
                            onCheckedChange = onTaxChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = OneUIBlue,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = OneUIOutline
                            ),
                            modifier = Modifier.testTag("taxSwitch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = OneUIBlue)
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
                            text = if (taxEnabled) "التكلفة + الضريبة" else "التكلفة بدون ضريبة",
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
        CreditFooter()
    }
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
