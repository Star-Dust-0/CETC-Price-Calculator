package com.stardust.cetccalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stardust.cetccalc.ui.components.CreditFooter
import com.stardust.cetccalc.ui.theme.OneUIBackground
import com.stardust.cetccalc.ui.theme.OneUIBlue
import com.stardust.cetccalc.ui.theme.OneUISurface
import com.stardust.cetccalc.ui.theme.OneUITextPrimary
import com.stardust.cetccalc.ui.theme.OneUITextSecondary

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OneUIBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
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
                text = "حول التطبيق",
                style = MaterialTheme.typography.titleLarge,
                color = OneUITextPrimary
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OneUISurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("حاسبة أسعار الاختبارات", style = MaterialTheme.typography.titleLarge, color = OneUITextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("الإصدار 1.0.0", style = MaterialTheme.typography.bodyMedium, color = OneUITextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "حاسبة أسعار اختبارات وحدة اختبارات واستشارات الهندسة المدنية، مع قاعدة بيانات أسعار قابلة للتحديث من ملفات CSV.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OneUITextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("المطور: محمد النجار", style = MaterialTheme.typography.bodyMedium, color = OneUITextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "github.com/Star-Dust-0/CETC-Price-Calculator",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OneUIBlue,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/Star-Dust-0/CETC-Price-Calculator")
                    }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OneUISurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "ملاحظات قائمة الأسعار",
                    style = MaterialTheme.typography.titleMedium,
                    color = OneUIBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                val notes = listOf(
                    "جميع الأسعار غير خاضعة لضريبة القيمة المضافة، وعند طلب فاتورة إلكترونية يضاف ١٤٪ على إجمالي قيمة الاختبارات.",
                    "يضاف ١٠٠٠ ج.م في حالة جدولة زيارة لإجراء اختبار في الموقع وحدوث تعطيل للعمل من جانب العميل.",
                    "يتم تحصيل مبالغ الاختبارات قبل البدء في تنفيذها، وفي حالة التأخير عن السداد لمدة ١٤ يومًا تضاف غرامة ٢٠٪.",
                    "يجب تسليم خطاب موجه إلى مدير وحدة اختبارات واستشارات الهندسة المدنية مع العينات، موضحًا به بيانات المشروع والمقاول والمالك والاستشاري والاختبارات المطلوبة بدقة، ومعتمدًا من طرف الاستشاري."
                )
                notes.forEach { note ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("• ", color = OneUIBlue, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OneUITextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        CreditFooter()
        Spacer(modifier = Modifier.height(8.dp))
    }
}
