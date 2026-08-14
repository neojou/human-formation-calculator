# Progress — Human-Formation Calculator v0.3

## 目前狀態

**空間並行人列加法已可玩。** Desktop / Wasm 編譯通過；`desktopTest`（含 16×16 與「單 tick 不會做完全部」）通過。

## 已完成

- [x] 依手稿重寫 `DESIGN.md` / `AGENTS.md`
- [x] 拿掉微指令機（`MicroOp`、左欄指令列表、共用單一 XOR/AND/OR）
- [x] 四個 1-bit full-adder 橫向串接；每 tick 全體並行
- [x] 騎兵送 A／B、Sum 變色後抄回下方資料區再回來待命
- [x] Cin 看右邊 adder 的 Cout；bit0 Cin 固定白旗
- [x] 畫面：上 A/B、中運算、下 Sum；Step / Play / Pause

## 下一步（非本版）

- 騎兵路徑做連續插值
- 加減以外的運算陣

## 怎麼測

```
./gradlew :composeApp:run
```

1. 預設 7 與 5，按「開始」→ 上方 A/B 舉旗，運算區仍是白旗，騎兵出發
2. Step：每步所有人動一次；門組橢圓內三個士兵會一起變
3. Play 跑完 → 下方 Sum=12
4. 15+1 → Sum=0、Cout=1

## 問題與決策

- 同一 tick 先看舊旗再一起改，進位會一格一格漣波上去。
- 騎兵每 tick 走一個路徑點。
