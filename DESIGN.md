# Human-Formation Calculator — Design Document

## 靈感來源

來自劉慈欣《三體》中的「人列計算機」（英文官方譯名：human-formation computer）。

原著是**硬體空間並行**：一次排出所有邏輯門，士兵盯著入一、入二，同時舉旗。本專案跟這條路線走，**不再使用微指令序列**。

- 騎兵：搬運資料（看旗、跑位、把顏色交給目標士兵）
- 門士兵：三三一組，每一 tick 同時看著入一／入二，依自己的運算舉旗
- 門士兵的旗也可以是另一組的入一／入二
- 每一步 = 一次 tick，場上所有騎兵與士兵各動作一次

參考手稿：`docs/1_full_adder.jpg`、`docs/four_1_full_adder.jpg`。

## 視覺佈局（上 → 中 → 下）

```
        A[3] A[2] A[1] A[0]                 B[3] B[2] B[1] B[0]   ← 上方資料區（A 左、B 右）
            │             │             │             │
       ┌────┴────┐   ┌────┴────┐   ┌────┴────┐   ┌────┴────┐
Cout ← │ FA bit3 │←──│ FA bit2 │←──│ FA bit1 │←──│ FA bit0 │← Cin=0
       └────┬────┘   └────┬────┘   └────┬────┘   └────┬────┘
            │             │             │             │
          S[3]          S[2]          S[1]          S[0]        ← 下方資料區
```

- **上方資料區**：A[3:0] 整組在左、B[3:0] 整組在右（高位在左，方便和輸入值比對）。開始後依輸入舉黑／白旗（黑=1，白=0）。送 A／B 的騎兵走不同高度的橫向航道，交叉時不會寫錯 bit。
- **中間運算區**：四個 1-bit full-adder 橫向串接。左為高位 bit3，右為最低位 bit0。**沒有微指令列表。**
- **下方資料區**：Sum[3:0]。由 Sum 騎兵從各 adder 的 Sum 士兵抄回。

## 一個 1-bit full-adder

與手稿相同，五組「入一 + 入二 + 運算士兵」：

| 組 | 入一 | 入二 | 運算 | 舉旗士兵 |
|----|------|------|------|----------|
| Temp1 | A | B | XOR | Temp1 |
| Carry1 | A | B | AND | Carry1 |
| Sum | Temp1 | Cin | XOR | Sum |
| Carry2 | Temp1 | Cin | AND | Carry2 |
| Cout | Carry1 | Carry2 | OR | Cout |

空間位置（對應 `docs/1_full_adder.jpg`）：

```
            A ○          B ○
     Carry1 AND ○          Temp1 XOR ○     Cin ○
Cout OR ○        Carry2 AND ○
                               Sum XOR ○
```

另外：

畫面角色是 Q 版秦俑士兵／騎兵精靈圖：黑旗=1、白旗=0。運算士兵胸口字母 **A**=AND、**O**=OR、**X**=XOR（Compose 疊字，保證正確）。

- **A / B 士兵**：不自己算，等騎兵把上方資料區的顏色送來後才改旗。
- **Cin 士兵**：看**右邊**那一個 adder 的 Cout 顏色來舉旗。最右邊（bit0）的 Cin **固定白旗**。
- **Sum 騎兵**：Adder 的 Sum 一變色，就舉同樣的旗跑到下方資料區改 Sum，再跑回 Adder Sum 旁邊待命。

## Tick 模型

同一 tick 內，所有人先看**這一拍開始時**的旗，再一起改：

1. 門士兵：`out' = f(in1, in2)`（五組同時算）
2. Cin：`Cin[0]' = 0`，`Cin[i]' = Cout[i-1]`（看右邊鄰座目前的 Cout）
3. 騎兵：沿路徑走一格；走到終點才改目標士兵的旗
4. Sum 騎兵若待命且 Adder Sum ≠ 上次抄走的顏色 → 舉新旗出發

因此進位是真的「漣波」：Cout[0] 變了，下一 tick Cin[1] 才跟，再下一 tick bit1 的 Sum／Cout 才跟上。這就是人列計算機的空間延遲。

## 騎兵

| 騎兵 | 路徑 | 到達時 |
|------|------|--------|
| 送 A | 上方 A[i] → Adder A[i] | Adder A 改成騎兵的旗色 |
| 送 B | 上方 B[i] → Adder B[i] | Adder B 改成騎兵的旗色 |
| 寫 Sum | Adder Sum[i] → 下方 S[i] → 回到 Adder Sum | 到達下方時改資料區 Sum |

開始時全場運算士兵與 Sum 皆白旗；上方 A／B 依輸入舉旗，送 A／B 的騎兵從資料區出發。

## 控制

- **開始**：載入 A、B，重置人陣為白旗，派出送 A／B 騎兵
- **Step**：推進 1 tick
- **Play / Pause**：依速度自動 tick
- 安靜（A／B 已送到、門不再變、Sum 騎兵都回來且抄完）→ 完成

## 核心對應

| 角色 | 對應 | 職責 |
|------|------|------|
| 騎兵 | 匯流排 / 搬移 | 看旗、跑位、改對方的旗 |
| 資料區士兵 | Register | 上方保存 A／B，下方保存 Sum |
| 門士兵 | 組合邏輯 | 每 tick 盯著入一入二舉旗 |
| Cin 士兵 | 進位線 | 看右座 Cout；bit0 釘在 0 |

## 技術備註

- Kotlin Multiplatform + Compose Multiplatform（Desktop + Browser）
- 座標與規則在 `commonMain`；UI 只畫 snapshot
- 騎兵走離散路徑點（每 tick 一格），不做物理引擎
