package com.stardust.cetccalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stardust.cetccalc.ui.theme.OneUIBlue
import com.stardust.cetccalc.ui.theme.OneUIDivider
import com.stardust.cetccalc.ui.theme.OneUISurface
import com.stardust.cetccalc.ui.theme.OneUITextPrimary
import com.stardust.cetccalc.ui.theme.OneUITextSecondary

/** White rounded One UI card holding one labelled section. */
@Composable
fun OneUISectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = OneUISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = OneUIBlue,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(OneUIBlue, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

/** One label/value row of the pricing card, label at the start (right in RTL). */
@Composable
fun PriceRow(
    label: String,
    sublabel: String? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = OneUITextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    color = OneUITextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        trailing()
    }
}

@Composable
fun RowDivider() {
    HorizontalDivider(color = OneUIDivider, thickness = 1.dp)
}

@Composable
fun ValueText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = OneUITextPrimary,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/** The shared "brought to you by Instinct" credit line. */
@Composable
fun CreditFooter(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Text(
        text = "Brought to you by Instinct",
        color = OneUITextSecondary,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri("https://instinct.com") }
            .padding(vertical = 12.dp)
    )
}
