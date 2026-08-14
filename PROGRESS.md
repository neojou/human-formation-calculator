# Progress — Human-Formation Calculator v0.2

## 目前狀態

**第一版可玩（v0.2）已完成。** Desktop / Wasm 編譯通過；`desktopTest` 含 16×16 加法與輸入解析皆通過。

## 已完成

- [x] 閱讀 `AGENTS.md`、`DESIGN.md`
- [x] 產出 `PLAN.md`
- [x] Phase 1：`Bit` / `Register` / `LogicGate` / `MicroOp` / `FormationMachine` / 4-bit 漣波進位微程式
- [x] Phase 1 測試：`7+5=12`、`15+1` 進位、0–15 全組合、`parseNibble`
- [x] Phase 2：左微指令 / 中門士兵 / 右資料士兵
- [x] Phase 3：開始載入 A/B、Step、Play/Pause、速度

## 下一步（v0.2 之後，非本版範圍）

- 騎兵路徑動畫
- 減法或其他運算微程式
- 更細的「這一步搬了哪個士兵」高亮

## 怎麼測

```
./gradlew :composeApp:run
```

1. 預設 A=7、B=5，按「開始」→ 右側 A 應為 `0111`、B 為 `0101`
2. 按 Step，看左側高亮下移、中間門旗變化
3. 按 Play 跑完 → Sum=12
4. 改輸入 `0111` 與 `0b0101`，應等同 7+5
5. 試 15+1 → Sum=0、Cout=1

## 問題與決策

- v0.2 不做騎兵移動動畫；MOVE 即時寫入，活動中的門會高亮。
- `10` 當十進位 10；剛好 4 位 `0/1` 或 `0b` 前綴才當二進位。
- 本環境沒有獨立的 `/goal` 工具介面，已在同一輪依計畫實作到可玩定義。
