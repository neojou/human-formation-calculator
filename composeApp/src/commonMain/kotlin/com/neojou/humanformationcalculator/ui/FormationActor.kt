package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neojou.humanformationcalculator.Res
import com.neojou.humanformationcalculator.cavalry_black
import com.neojou.humanformationcalculator.cavalry_white
import com.neojou.humanformationcalculator.core.CavalryView
import com.neojou.humanformationcalculator.core.SoldierView
import com.neojou.humanformationcalculator.soldier_black
import com.neojou.humanformationcalculator.soldier_white
import org.jetbrains.compose.resources.painterResource

private val Highlight = Color(0xFFFFC107)
private val BadgeInk = Color(0xFF3E2723)

@Composable
fun FormationSoldier(
    soldier: SoldierView,
    height: Dp = 58.dp,
    modifier: Modifier = Modifier,
) {
    val drawable = if (soldier.bit.isOne) Res.drawable.soldier_black else Res.drawable.soldier_white
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (soldier.changed) {
                Modifier.border(2.dp, Highlight, CircleShape).padding(2.dp)
            } else {
                Modifier
            },
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = soldier.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(height),
            )
            soldier.role.chestMark?.let { mark ->
                Text(
                    text = mark,
                    modifier = Modifier.offset(y = height * 0.14f),
                    color = BadgeInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
        Text(
            text = soldier.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun FormationCavalry(
    cavalry: CavalryView,
    height: Dp = 64.dp,
    modifier: Modifier = Modifier,
) {
    val drawable = if (cavalry.flag.isOne) Res.drawable.cavalry_black else Res.drawable.cavalry_white
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = if (cavalry.riding) {
                Modifier.border(2.dp, Highlight, CircleShape).padding(2.dp)
            } else {
                Modifier
            },
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = cavalry.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(height),
            )
        }
        Text(
            text = cavalry.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.75f), CircleShape)
                .padding(horizontal = 4.dp),
        )
    }
}
