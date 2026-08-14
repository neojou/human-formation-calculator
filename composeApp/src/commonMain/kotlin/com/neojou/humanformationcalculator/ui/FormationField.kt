package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neojou.humanformationcalculator.core.CavalryKind
import com.neojou.humanformationcalculator.core.CavalryView
import com.neojou.humanformationcalculator.core.FieldLayout
import com.neojou.humanformationcalculator.core.GateKind
import com.neojou.humanformationcalculator.core.MachineSnapshot
import com.neojou.humanformationcalculator.core.SoldierView

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
            text = "資料區 A / B    A=${snapshot.aValue}  B=${snapshot.bValue}",
            modifier = Modifier.offset(x = 10.dp, y = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "人列運算區（四個 1-bit full-adder）  黑旗=1  白旗=0",
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
                    x = with(density) { (w.toPx() * s.x).toDp() - 20.dp },
                    y = with(density) { (h.toPx() * s.y).toDp() - 16.dp },
                ),
            ) {
                SoldierFlag(
                    bit = s.bit,
                    caption = s.label,
                    highlighted = s.changed,
                    diameter = 22.dp,
                )
            }
        }

        snapshot.cavalry.forEach { c ->
            Box(
                modifier = Modifier.offset(
                    x = with(density) { (w.toPx() * c.x).toDp() - 16.dp },
                    y = with(density) { (h.toPx() * c.y).toDp() - 16.dp },
                ),
            ) {
                CavalryMarker(c)
            }
        }
    }
}

@Composable
private fun CavalryMarker(cavalry: CavalryView) {
    val body = when (cavalry.kind) {
        CavalryKind.FETCH_A -> Color(0xFF6D4C41)
        CavalryKind.FETCH_B -> Color(0xFF5D4037)
        CavalryKind.WRITE_SUM -> Color(0xFF37474F)
    }
    val flag = if (cavalry.flag.isOne) Color(0xFF1B1B1B) else Color(0xFFF4F1E8)
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(body, RoundedCornerShape(8.dp))
            .border(
                width = if (cavalry.riding) 2.dp else 1.dp,
                color = if (cavalry.riding) Color(0xFFFFC107) else Color(0xFF212121),
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = cavalry.label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(10.dp)
                .border(1.dp, Color.White, CircleShape)
                .background(flag, CircleShape),
        )
    }
}
