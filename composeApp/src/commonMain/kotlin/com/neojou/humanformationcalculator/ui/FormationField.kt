package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neojou.humanformationcalculator.core.FieldLayout
import com.neojou.humanformationcalculator.core.GateKind
import com.neojou.humanformationcalculator.core.MachineSnapshot

private val Paper = Color(0xFFF7F3EA)
private val Grid = Color(0x14000000)
private val CarryLine = Color(0xFF5D4037)

@Composable
fun FormationField(
    snapshot: MachineSnapshot,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = modifier
            .background(Paper, MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
    ) {
        val w = maxWidth
        val h = maxHeight
        Canvas(Modifier.fillMaxSize()) {
            val step = 28.dp.toPx()
            var gx = 0f
            while (gx < size.width) {
                drawLine(Grid, Offset(gx, 0f), Offset(gx, size.height), 1f)
                gx += step
            }
            var gy = 0f
            while (gy < size.height) {
                drawLine(Grid, Offset(0f, gy), Offset(size.width, gy), 1f)
                gy += step
            }
            for (i in 1..3) {
                val x = size.width * (i / 4f)
                drawLine(Color(0x22000000), Offset(x, size.height * 0.14f), Offset(x, size.height * 0.88f), 2f)
            }
            snapshot.groups.forEach { g ->
                val color = when (g.kind) {
                    GateKind.XOR -> Color(0xFF1565C0)
                    GateKind.AND -> Color(0xFF2E7D32)
                    GateKind.OR -> Color(0xFFEF6C00)
                    GateKind.NOT -> Color(0xFF6A1B9A)
                }
                val left = (g.cx - g.rx) * size.width
                val top = (g.cy - g.ry) * size.height
                val gw = g.rx * 2f * size.width
                val gh = g.ry * 2f * size.height
                drawOval(color.copy(alpha = 0.10f), Offset(left, top), Size(gw, gh))
                drawOval(color.copy(alpha = 0.45f), Offset(left, top), Size(gw, gh), style = Stroke(2.5f))
            }
            for (bit in 0..2) {
                val from = FieldLayout.cout(bit)
                val to = FieldLayout.cin(bit + 1)
                drawLine(
                    color = CarryLine,
                    start = Offset(from.x * size.width, from.y * size.height),
                    end = Offset(to.x * size.width, to.y * size.height),
                    strokeWidth = 3f,
                )
            }
        }

        Text(
            text = "資料區 A    A=${snapshot.aValue}",
            modifier = Modifier.offset(x = 10.dp, y = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "資料區 B    B=${snapshot.bValue}",
            modifier = Modifier.offset(
                x = with(density) { (w.toPx() * 0.58f).toDp() },
                y = 4.dp,
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "人列運算區（四個 1-bit full-adder）  黑旗=1  白旗=0  胸口 A=AND  O=OR  X=XOR",
            modifier = Modifier.offset(
                x = 10.dp,
                y = with(density) { (h.toPx() * 0.145f).toDp() },
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = "資料區 Sum    Sum=${snapshot.sumValue}" +
                if (snapshot.highCout.isOne) "  Cout=1" else "",
            modifier = Modifier.offset(
                x = 10.dp,
                y = with(density) { (h.toPx() * 0.875f).toDp() },
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )

        for (bit in 0..3) {
            val x = with(density) { (w.toPx() * (FieldLayout.colLeft(bit) + 0.01f)).toDp() }
            Text(
                text = "bit $bit",
                modifier = Modifier.offset(
                    x = x,
                    y = with(density) { (h.toPx() * 0.145f).toDp() + 16.dp },
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            )
        }

        snapshot.groups.forEach { g ->
            Text(
                text = g.label,
                modifier = Modifier.offset(
                    x = with(density) { (w.toPx() * (g.cx - g.rx) + 4).toDp() },
                    y = with(density) { (h.toPx() * (g.cy - g.ry) - 2).toDp() },
                ),
                style = MaterialTheme.typography.labelSmall,
                color = when (g.kind) {
                    GateKind.XOR -> Color(0xFF1565C0)
                    GateKind.AND -> Color(0xFF2E7D32)
                    GateKind.OR -> Color(0xFFEF6C00)
                    GateKind.NOT -> Color(0xFF6A1B9A)
                },
                fontWeight = FontWeight.Medium,
            )
        }

        snapshot.soldiers.forEach { s ->
            Box(
                modifier = Modifier.offset(
                    x = with(density) { (w.toPx() * s.x).toDp() - 22.dp },
                    y = with(density) { (h.toPx() * s.y).toDp() - 30.dp },
                ),
            ) {
                FormationSoldier(soldier = s, height = 54.dp)
            }
        }

        snapshot.cavalry.forEach { c ->
            Box(
                modifier = Modifier.offset(
                    x = with(density) { (w.toPx() * c.x).toDp() - 28.dp },
                    y = with(density) { (h.toPx() * c.y).toDp() - 36.dp },
                ),
            ) {
                FormationCavalry(cavalry = c, height = 62.dp)
            }
        }
    }
}
