package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControlPanel(
    aText: String,
    bText: String,
    onAChange: (String) -> Unit,
    onBChange: (String) -> Unit,
    error: String?,
    status: String,
    canStep: Boolean,
    canPlay: Boolean,
    playing: Boolean,
    speedMs: Int,
    onStart: () -> Unit,
    onStep: () -> Unit,
    onTogglePlay: () -> Unit,
    onSpeed: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = aText,
                    onValueChange = onAChange,
                    label = { Text("A") },
                    singleLine = true,
                    modifier = Modifier.width(120.dp),
                )
                OutlinedTextField(
                    value = bText,
                    onValueChange = onBChange,
                    label = { Text("B") },
                    singleLine = true,
                    modifier = Modifier.width(120.dp),
                )
                Button(onClick = onStart) { Text("開始") }
                OutlinedButton(onClick = onStep, enabled = canStep && !playing) {
                    Text("Step")
                }
                Button(onClick = onTogglePlay, enabled = canPlay || playing) {
                    Text(if (playing) "Pause" else "Play")
                }
                Text("速度", style = MaterialTheme.typography.labelMedium)
                SpeedChip("慢", selected = speedMs == 900, onClick = { onSpeed(900) })
                SpeedChip("中", selected = speedMs == 450, onClick = { onSpeed(450) })
                SpeedChip("快", selected = speedMs == 180, onClick = { onSpeed(180) })
            }
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = status.ifBlank { "輸入 0–15 或四位二進位（如 0111、0b0101），按開始載入 A / B。" },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SpeedChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
    )
}
