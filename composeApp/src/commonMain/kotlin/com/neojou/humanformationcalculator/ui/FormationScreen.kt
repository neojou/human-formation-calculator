package com.neojou.humanformationcalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neojou.humanformationcalculator.core.FormationMachine
import com.neojou.humanformationcalculator.core.MachinePhase
import com.neojou.humanformationcalculator.core.MachineSnapshot
import com.neojou.humanformationcalculator.core.parseNibble
import kotlinx.coroutines.delay

private class FormationController {
    private val machine = FormationMachine()

    var snapshot by mutableStateOf(machine.snapshot())
        private set
    var aText by mutableStateOf("7")
    var bText by mutableStateOf("5")
    var inputError by mutableStateOf<String?>(null)
    var playing by mutableStateOf(false)
    var speedMs by mutableIntStateOf(450)

    val canStep: Boolean
        get() = snapshot.phase == MachinePhase.Loaded || snapshot.phase == MachinePhase.Running

    val canPlay: Boolean
        get() = canStep

    fun start() {
        val a = parseNibble(aText)
        val b = parseNibble(bText)
        if (a == null || b == null) {
            inputError = "請輸入 0–15，或 4 位元二進位（例如 0111）／0bxxxx"
            playing = false
            return
        }
        inputError = null
        playing = false
        machine.load(a, b)
        snapshot = machine.snapshot()
    }

    fun step(): Boolean {
        if (!canStep) return false
        val moved = machine.step()
        snapshot = machine.snapshot()
        if (!moved) playing = false
        return moved
    }

    fun togglePlay() {
        if (!canPlay && !playing) return
        playing = !playing
    }
}

@Composable
fun FormationScreen(modifier: Modifier = Modifier) {
    val controller = remember { FormationController() }
    val snap: MachineSnapshot = controller.snapshot

    LaunchedEffect(controller.playing, controller.speedMs) {
        if (!controller.playing) return@LaunchedEffect
        while (controller.playing) {
            if (!controller.step()) break
            delay(controller.speedMs.toLong())
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FormationField(
            snapshot = snap,
            modifier = Modifier.weight(1f).fillMaxSize(),
        )
        ControlPanel(
            aText = controller.aText,
            bText = controller.bText,
            onAChange = { controller.aText = it },
            onBChange = { controller.bText = it },
            error = controller.inputError,
            status = snap.lastMessage,
            canStep = controller.canStep,
            canPlay = controller.canPlay,
            playing = controller.playing,
            speedMs = controller.speedMs,
            onStart = controller::start,
            onStep = { controller.step() },
            onTogglePlay = controller::togglePlay,
            onSpeed = { controller.speedMs = it },
        )
    }
}
