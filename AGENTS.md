# Human-Formation Calculator / 人陣計算機

## Project Overview
This is a Kotlin Multiplatform (Compose Multiplatform) educational visualization tool inspired by the "human-formation computer" from Liu Cixin's *The Three-Body Problem*.

Goal: Visually simulate how a computer works using soldiers raising black/white flags (0/1) and cavalry delivering data, starting with a clear 4-bit adder demonstration.

## Current Status (v0.2)
- 4-bit ripple-carry adder microcode + FormationMachine in `commonMain`
- Desktop / Browser UI: left micro-ops, middle gates, right register soldiers
- Input 0–15 (or binary) → Start loads A/B flags → Step / Play / Pause

## Core Design Principles
1. **Software von Neumann style** (preferred) rather than pure spatial hardware parallelism from the novel.
2. Visual language must stay faithful to *Three-Body*:
   - Soldiers hold black flag = 1, white flag = 0
   - Cavalry act as data messengers / bus
   - Gate soldiers (XOR, AND, OR, etc.) perform logic
3. Prioritize clarity of the computation process over pure spectacle.
4. Make the execution step-by-step controllable (play / pause / step / speed).

## Architecture Preferences
- Shared logic in `commonMain`
- UI & animation in Compose Multiplatform
- Clear separation:
  - Data model (Bits, Registers, Gates)
  - Micro-instruction system
  - Visual components (Soldier, Cavalry, Flag, InstructionList)
- Start with 4-bit ripple-carry adder

## Naming Conventions
- Use English for code identifiers
- Chinese is welcome in UI strings and comments
- Prefer clear names: `Bit`, `Register`, `Gate`, `MicroOp`, `Cavalry`, `Soldier`

## Important Constraints
- Do not over-engineer the first version
- Prefer readable step-by-step microcode over highly optimized parallel simulation
- Keep the visual layout simple and educational

## Next Priorities
1. Define core data models
2. Define micro-instruction set
3. Implement 4-bit addition microcode
4. Build basic static visual layout (registers + gates)
5. Add cavalry animation and step execution
