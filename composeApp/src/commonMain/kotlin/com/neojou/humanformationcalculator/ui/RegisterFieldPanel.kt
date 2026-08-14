package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neojou.humanformationcalculator.core.Bit

@Composable
fun RegisterFieldPanel(
    a: List<Bit>,
    b: List<Bit>,
    sum: List<Bit>,
    aValue: Int,
    bValue: Int,
    sumValue: Int,
    cin: Bit,
    cout: Bit,
    temp1: Bit,
    carry1: Bit,
    carry2: Bit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "資料區（士兵）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "黑旗 = 1，白旗 = 0。左側為高位。",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            )
            NibbleRow("A", a, aValue)
            NibbleRow("B", b, bValue)
            NibbleRow("Sum", sum, sumValue)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "進位與暫存",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SoldierFlag(cin, "Cin")
                SoldierFlag(cout, "Cout")
                SoldierFlag(temp1, "Temp1")
                SoldierFlag(carry1, "Carry1")
                SoldierFlag(carry2, "Carry2")
            }
        }
    }
}

@Composable
private fun NibbleRow(name: String, bits: List<Bit>, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in bits.lastIndex downTo 0) {
                SoldierFlag(bits.getOrNull(i), "[$i]")
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
