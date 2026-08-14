package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.neojou.humanformationcalculator.core.GateKind
import com.neojou.humanformationcalculator.core.GateSnapshot

@Composable
fun GateYardPanel(
    xor: GateSnapshot,
    and: GateSnapshot,
    or: GateSnapshot,
    not: GateSnapshot,
    activeGate: GateKind?,
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
                text = "運算區（門士兵）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "騎兵把 bit 送到入口，門士兵舉起結果旗。",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            )
            GateRow(xor, active = activeGate == GateKind.XOR)
            GateRow(and, active = activeGate == GateKind.AND)
            GateRow(or, active = activeGate == GateKind.OR)
            GateRow(not, active = activeGate == GateKind.NOT, unary = true)
        }
    }
}

@Composable
private fun GateRow(
    gate: GateSnapshot,
    active: Boolean,
    unary: Boolean = false,
) {
    Surface(
        tonalElevation = if (active) 4.dp else 0.dp,
        color = if (active) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = gate.kind.name,
                modifier = Modifier.width(52.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            SoldierFlag(gate.in1, "入1", highlighted = active)
            if (!unary) {
                Spacer(Modifier.width(8.dp))
                SoldierFlag(gate.in2, "入2", highlighted = active)
            }
            Text(
                text = "→",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            SoldierFlag(gate.out, "出", highlighted = active)
        }
    }
}
