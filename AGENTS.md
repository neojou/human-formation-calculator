# Human-Formation Calculator / 人陣計算機

## Project Overview
This is a Kotlin Multiplatform (Compose Multiplatform) educational visualization tool inspired by the "human-formation computer" from Liu Cixin's *The Three-Body Problem*.

Goal: Visually simulate how a computer works using soldiers raising black/white flags (0/1) and cavalry delivering data. The first demo is a 4-bit ripple-carry adder laid out as four 1-bit full-adders in space.

## Current Status (v0.4)
- Spatial parallel 4-bit adder is playable
- Q-style Qin soldier / cavalry sprites raise black (1) or white (0) flags
- Operation soldiers show chest marks: A = AND, O = OR, X = XOR

## Core Design Principles
1. **Spatial hardware parallelism** as in the novel — not a von Neumann micro-program.
2. Visual language stays faithful to *Three-Body*:
   - Black flag = 1, white flag = 0
   - Cavalry are messengers
   - Gate soldiers work in groups of three (in1, in2, operator)
3. Clarity of the parallel process over spectacle.
4. Execution is tick-based and controllable (play / pause / step / speed).

## Architecture Preferences
- Shared logic in `commonMain`
- UI in Compose Multiplatform only reads snapshots
- Names: `Bit`, `Register`, `Cavalry`, `Soldier`, `AdderState`, `FormationMachine`
- Four 1-bit full-adders, bit3 left … bit0 right; Cin[0] is fixed 0

## Naming Conventions
- English identifiers
- Chinese welcome in UI strings and comments

## Important Constraints
- Do not over-engineer
- One tick = everyone looks at the current flags, then everyone updates
- Cavalry move along discrete waypoints (one hop per tick)

## Next Priorities
1. Optional: smoother cavalry path interpolation between ticks
2. Optional: more operations beyond 4-bit add
