# Human-Formation Calculator — Design Document

## 靈感來源
來自劉慈欣《三體》中的「人列計算機」（英文官方譯名：human-formation computer）。

原著是硬體空間並行思維（一次排出所有邏輯門），本專案改採**軟體馮·諾伊曼風格**：
- 有明確的微指令序列
- 騎兵負責搬運資料（bit）
- 門士兵負責運算
- 可逐步執行，方便教學與觀察

## 視覺佈局規劃（由左到右）

1. **左側：指令區（Micro-instruction List）**
   - 顯示目前的微指令序列
   - 當前正在執行的指令高亮
   - 支援逐步執行 / 自動播放 / 調整速度

2. **中間：運算區（Gate Soldiers）**
   - 固定幾個邏輯門士兵：XOR、AND、OR、NOT
   - 每個門有兩個輸入位置 + 一個輸出位置
   - 騎兵把資料送到輸入位置後，門士兵舉起結果旗

3. **右側：資料區（Registers）**
   - A[3:0]
   - B[3:0]
   - Sum[3:0]
   - Carry / Temp 暫存旗
   - 每個 bit 是一個士兵，舉黑旗（1）或白旗（0）

4. **騎兵**
   - 負責在資料區與運算區之間移動
   - 一次通常只搬一個 bit
   - 視覺上要清楚看到「資料正在被傳送」

## 4-bit 加法實作方式

使用經典的**漣波進位加法器（Ripple Carry Adder）**。

每一位全加器拆解為以下微操作概念：

1. Temp1 = A[i] XOR B[i]
2. Carry1 = A[i] AND B[i]
3. Sum[i] = Temp1 XOR Cin
4. Carry2 = Temp1 AND Cin
5. Cout = Carry1 OR Carry2   （成為下一位的 Cin）

初始 Cin = 0，依序處理 bit0 → bit3。

## 核心概念對應

| 角色           | 對應真實電腦概念     | 職責                     |
|----------------|----------------------|--------------------------|
| 騎兵           | Data Bus / 搬移單元  | 傳送 bit                 |
| 資料區士兵     | Register             | 靜態保存 0/1             |
| 門士兵         | ALU / Logic Gate     | 執行 AND / XOR / OR 等   |
| 微指令序列     | Microcode / Control  | 決定下一步做什麼         |

## 建議實作階段

### Phase 1（基礎）
- Bit、Register、Gate 資料模型
- 微指令（MicroOp）定義
- 4-bit 加法的微程式

### Phase 2（視覺）
- 靜態佈局（士兵位置、旗子顏色）
- 簡單的指令列表 UI

### Phase 3（動畫與互動）
- 騎兵移動動畫
- 逐步執行控制（Step / Play / Pause）
- 輸入數字 → 自動載入 A/B 並開始運算

## 技術備註
- Kotlin Multiplatform + Compose Multiplatform
- 目標平台：Desktop + Browser（Web）
- 優先保證 commonMain 邏輯清晰，UI 層再做動畫包裝
