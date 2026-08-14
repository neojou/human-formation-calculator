# v0.2 實作計畫：第一版可玩的 4-bit 加法可視化

## 目標（完成定義）

使用者可以：

1. 輸入兩個 0–15 的數字（十進位，或 4-bit / `0b` 二進位）
2. 按「開始」後，A、B 暫存器士兵舉起對應黑／白旗
3. 逐步或自動執行 4-bit 漣波進位加法微指令
4. 畫面上同時看到：
   - 左：微指令列表，目前步驟高亮
   - 中：XOR / AND / OR / NOT 門士兵的輸入與輸出旗
   - 右：A / B / Sum / Cin / Cout / Temp 士兵旗變化
5. 至少有 **Step** 與 **Play / Pause**

v0.2 **不做**完整騎兵路徑動畫（DESIGN Phase 3 的視覺加強）。MOVE 微指令會立刻把來源 bit 寫到目的地，並用高亮表示「這一步搬了誰」。騎兵動畫留到後續版本。

## 原則

- 邏輯全在 `commonMain`，UI 只觀察 snapshot
- 微指令逐步、可讀，不平行硬體模擬
- 不過度工程：沒有 DI、沒有事件匯流排、沒有 generic 電路描述語言
- 沿用現有 `App()` 啟動與 `com.neojou.tools.*`

## 資料模型

```
Bit            ZERO / ONE（白旗 = 0，黑旗 = 1）
Register       固定寬度 bit 陣列；index 0 = LSB
LogicGate      XOR / AND / OR / NOT；in1, in2, out
BitLoc         暫存器位、Cin/Cout/Temp1/Carry1/Carry2、門輸入/輸出
MicroOp        Move(from, to) | Eval(gate, dest) | Halt
FormationMachine
               A, B, Sum, 暫存旗, 四個門, program[], pc, phase
```

一位全加器對應的微操作（DESIGN 第 40–46 行）：

```
Temp1  = A[i] XOR B[i]
Carry1 = A[i] AND B[i]
Sum[i] = Temp1 XOR Cin
Carry2 = Temp1 AND Cin
Cout   = Carry1 OR Carry2
Cin    = Cout                 // 進下一位
```

視覺上再拆成「騎兵搬 bit → 門運算」：

```
MOVE A[i] → XOR.in1
MOVE B[i] → XOR.in2
EVAL XOR  → Temp1
…（每位 16 步，4 位 + HALT ≈ 65 步）
```

`phase`：`Idle` → `Loaded`（開始載入 A/B）→ `Running` → `Halted`

輸入解析：

- `0`–`15` 十進位（`10` = 十，不是 binary 2）
- 剛好 4 個 `0/1` → 二進位（`1010` = 10）
- `0b` 前綴 → 二進位

## UI 佈局草圖

```
┌─────────────────────────────────────────────────────────────┐
│  About                                                      │
├──────────────┬────────────────────────────┬─────────────────┤
│ 微指令        │ 運算區（門士兵）              │ 資料區           │
│              │                            │                 │
│ > 騎兵 A[0]  │   XOR  入1 入2 → 出         │ A  [3][2][1][0] │
│   騎兵 B[0]  │   AND  入1 入2 → 出         │ B  [3][2][1][0] │
│   XOR → T1   │   OR   入1 入2 → 出         │ Sum[3][2][1][0] │
│   …          │   NOT  入1     → 出         │ Cin Cout T1 …  │
│              │                            │ 黑旗=1 白旗=0   │
├──────────────┴────────────────────────────┴─────────────────┤
│ A [ 7 ]  B [ 5 ]   開始   Step   Play/Pause   速度：慢中快   │
│ 7 + 5 = 12   目前：XOR：A[0] ⊕ B[0] → Temp1                  │
└─────────────────────────────────────────────────────────────┘
```

黑旗 = 1，白旗 = 0。士兵用圓形旗面即可，不做貼圖。

## 檔案

新增：

| 檔案 | 職責 |
|------|------|
| `core/Bit.kt` | 0/1 |
| `core/Register.kt` | 暫存器 |
| `core/Gate.kt` | 邏輯門 |
| `core/BitLoc.kt` | bit 位置 |
| `core/MicroOp.kt` | 微指令 |
| `core/FormationMachine.kt` | 執行器 + snapshot |
| `core/InputParse.kt` | 0–15 / 二進位解析 |
| `core/programs/Add4BitProgram.kt` | 4-bit 加法微程式 |
| `ui/FormationScreen.kt` | 三欄 + 控制列 |
| `ui/InstructionListPanel.kt` | 左側微指令 |
| `ui/GateYardPanel.kt` | 中間門士兵 |
| `ui/RegisterFieldPanel.kt` | 右側資料士兵 |
| `ui/ControlPanel.kt` | 輸入與 Step/Play |
| `ui/SoldierFlag.kt` | 單兵旗 |
| `commonTest/.../Add4BitTest.kt` | 0–15 全組合正確性 |
| `PLAN.md` / `PROGRESS.md` | 計畫與進度 |

修改：

| 檔案 | 變更 |
|------|------|
| `HumanFormationCalculator.kt` | 主畫面改掛 `FormationScreen` |
| `AppVersion.kt` | 0.2 |
| `composeApp/build.gradle.kts` | version + commonTest |
| `Main.kt` | 視窗加大 |
| `AGENTS.md` | 狀態改 v0.2 |

不動：`App()` 啟動、`com.neojou.tools.*`、Desktop / Wasm 進入點寫法。

## 階段

1. **核心邏輯 + 測試**（可先不開 UI）
2. **靜態三欄畫面**（載入後看得到旗與指令）
3. **開始 / Step / Play-Pause / 速度** → 達到可玩定義

## 驗證

- `desktopTest`：16×16 加法結果與 Cout 正確
- Desktop 手動：輸入 `7` 與 `5`，開始 → 士兵旗 `0111` / `0101`，Step 看門變化，Play 跑完得到 Sum=12
- 輸入 `0111` + `0b0101` 應等同 7+5
- Wasm 編譯通過（與 Desktop 共用 commonMain）
