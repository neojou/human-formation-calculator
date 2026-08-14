package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neojou.humanformationcalculator.core.Bit

private val FlagBlack = Color(0xFF1B1B1B)
private val FlagWhite = Color(0xFFF4F1E8)
private val FlagUnset = Color(0xFFB0B0B0)
private val FlagBorder = Color(0xFF424242)
private val FlagActive = Color(0xFF1565C0)

/**
 * One soldier's flag. Black = 1, white = 0, gray = not yet delivered.
 */
@Composable
fun SoldierFlag(
    bit: Bit?,
    caption: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val fill = when (bit) {
        Bit.ONE -> FlagBlack
        Bit.ZERO -> FlagWhite
        null -> FlagUnset
    }
    val digit = when (bit) {
        Bit.ONE -> "1"
        Bit.ZERO -> "0"
        null -> "–"
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(
                    width = if (highlighted) 3.dp else 1.dp,
                    color = if (highlighted) FlagActive else FlagBorder,
                    shape = CircleShape,
                )
                .background(fill, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = digit,
                color = when (bit) {
                    Bit.ONE -> Color.White
                    else -> Color(0xFF212121)
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        )
    }
}
