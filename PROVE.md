# 如何證明目前的加法器是正確的

本文件證明：人列四位元漣波進位加法器（設計 + `FormationMachine` 實作）在整個輸入域上等於整數加法。

**結論（定理 T）**  
對任意 \(a, b \in \{0,1,\ldots,15\}\)，執行 `load(a, b)` 再 `runToHalt()` 之後：

\[
a + b \;=\; S + 16\cdot C_{\mathrm{out}}
\]

其中 \(S =\) `sum.toInt()`（下方資料區 Sum，0–15），\(C_{\mathrm{out}} =\) `highCout`（bit3 的 Cout，0 或 1）。  
等價寫法：`S == (a + b) and 0xF`，且 `highCout == 1` 當且僅當 `a + b ≥ 16`。

證明分四層：編碼 → 一位全加器恆等式 → 四位串接歸納 → 模擬器確實停在該穩態。最後用 256 組窮舉測試獨立核對實作。

---

## 0. 編碼與約定

| 符號 | 程式 | 意義 |
|------|------|------|
| 白旗 | `Bit.ZERO` | 0 |
| 黑旗 | `Bit.ONE` | 1 |
| \(A[i]\) | `a.get(i)` | A 的第 \(i\) 位，**\(i=0\) 是最低位（LSB）** |
| \(B[i], S[i]\) | 同上 | 同樣 LSB = index 0 |
| 畫面左→右 | bit3 … bit0 | 只影響排版，不影響數值 |

`Register.load` / `toInt` 互為反函數（\(i=0\) 對應 `value shr 0`）：

\[
v = \sum_{i=0}^{3} v[i]\cdot 2^{i},\qquad v[i] = \bigl\lfloor v / 2^{i}\bigr\rfloor \bmod 2
\]

因此「旗子排列」和「0–15 整數」一一對應。以下把黑／白旗直接當 \(\{0,1\}\) 運算。

`Logic` 的定義就是標準布林（`Gate.kt`）：

```
xor(x,y) = 1  ⇔  x ≠ y
and(x,y) = 1  ⇔  x = 1 且 y = 1
or(x,y)  = 1  ⇔  x = 1 或 y = 1
```

---

## 1. 一位全加器：五個門 = 教科書全加器

每個 1-bit full-adder 的更新（`tickOnce`，看的是**這一拍開始時**的旗）是：

\[
\begin{align*}
\mathrm{Temp1}  &= A \oplus B \\
\mathrm{Carry1} &= A \land B \\
\mathrm{Sum}    &= \mathrm{Temp1} \oplus C_{\mathrm{in}} \\
\mathrm{Carry2} &= \mathrm{Temp1} \land C_{\mathrm{in}} \\
C_{\mathrm{out}}&= \mathrm{Carry1} \lor \mathrm{Carry2}
\end{align*}
\]

對應胸口字母：Temp1／Sum 為 **X**，Carry1／Carry2 為 **A**，Cout 為 **O**。

### 命題 1（位元恆等式）

在穩態（再算一拍旗也不變）下，

\[
\begin{align*}
\mathrm{Sum} &= A \oplus B \oplus C_{\mathrm{in}} \\
C_{\mathrm{out}} &= (A \land B) \lor \bigl((A \oplus B) \land C_{\mathrm{in}}\bigr)
\end{align*}
\]

**證明。** 把 Temp1、Carry1、Carry2 代入即可。第二式右邊正是三人中至少兩人為 1 的 majority 展開：

\[
\begin{align*}
&(A \land B) \lor (A \land C_{\mathrm{in}}) \lor (B \land C_{\mathrm{in}}) \\
&\quad= (A \land B) \lor \bigl(A \land C_{\mathrm{in}} \land (B \lor \lnot B)\bigr)
         \lor \bigl(B \land C_{\mathrm{in}} \land (A \lor \lnot A)\bigr) \\
&\quad= (A \land B) \lor (A \land \lnot B \land C_{\mathrm{in}}) \lor (\lnot A \land B \land C_{\mathrm{in}}) \\
&\quad= (A \land B) \lor \bigl((A \oplus B) \land C_{\mathrm{in}}\bigr).
\end{align*}
\]

### 命題 2（整數恆等式）

把 \(A,B,C_{\mathrm{in}},\mathrm{Sum},C_{\mathrm{out}} \in \{0,1\}\) 當整數，命題 1 等價於

\[
A + B + C_{\mathrm{in}} \;=\; \mathrm{Sum} + 2\cdot C_{\mathrm{out}}.
\]

左邊 ∈ {0,1,2,3}。八列真值表全部對得上（設計與程式用同一組式子）：

| A | B | Cin | Temp1 | Carry1 | Sum | Carry2 | Cout | A+B+Cin | Sum+2·Cout |
|---|---|-----|-------|--------|-----|--------|------|---------|------------|
| 0 | 0 | 0   | 0     | 0      | 0   | 0      | 0    | 0       | 0          |
| 0 | 0 | 1   | 0     | 0      | 1   | 0      | 0    | 1       | 1          |
| 0 | 1 | 0   | 1     | 0      | 1   | 0      | 0    | 1       | 1          |
| 0 | 1 | 1   | 1     | 0      | 0   | 1      | 1    | 2       | 2          |
| 1 | 0 | 0   | 1     | 0      | 1   | 0      | 0    | 1       | 1          |
| 1 | 0 | 1   | 1     | 0      | 0   | 1      | 1    | 2       | 2          |
| 1 | 1 | 0   | 0     | 1      | 0   | 0      | 1    | 2       | 2          |
| 1 | 1 | 1   | 0     | 1      | 1   | 0      | 1    | 3       | 3          |

因此**每一位**在穩態下是正確的 1-bit 全加器。這就是 `docs/1_full_adder.jpg` 的電路。

---

## 2. 四位串接：漣波進位

四個全加器的進位線（`docs/four_1_full_adder.jpg`、`tickOnce`）：

\[
C_{\mathrm{in}}[0] = 0,\qquad
C_{\mathrm{in}}[i] = C_{\mathrm{out}}[i-1]\quad (i=1,2,3).
\]

畫面「Cin 看右邊 adder 的 Cout；最右 bit0 釘白旗」就是這兩條。`highCout` 定義為 `adders[3].cout`，即最高位進位。

記 \(k\) 位無號整數

\[
\mathrm{val}_k(X) = \sum_{i=0}^{k} X[i]\cdot 2^{i}.
\]

完整的 A、B、S 就是 \(\mathrm{val}_3\)。

### 命題 3（漣波歸納）

在四個 adder **同時**處於命題 1 的穩態、且進位線如上連接時，對每個 \(k=0,1,2,3\)：

\[
\mathrm{val}_k(A) + \mathrm{val}_k(B)
\;=\;
\mathrm{val}_k(S) + 2^{k+1}\cdot C_{\mathrm{out}}[k].
\]

**證明。** 對 \(k\) 歸納。

- **基底 \(k=0\)。** \(C_{\mathrm{in}}[0]=0\)，命題 2 即  
  \(A[0]+B[0]=\mathrm{Sum}[0]+2\cdot C_{\mathrm{out}}[0]\)。

- **歸納步。** 假設 \(k-1\) 成立。連接規定 \(C_{\mathrm{in}}[k]=C_{\mathrm{out}}[k-1]\)，故  
  \(\mathrm{val}_{k-1}(A)+\mathrm{val}_{k-1}(B)=\mathrm{val}_{k-1}(S)+2^{k}\cdot C_{\mathrm{in}}[k]\)。  
  命題 2 在第 \(k\) 位：  
  \(A[k]+B[k]+C_{\mathrm{in}}[k]=\mathrm{Sum}[k]+2\cdot C_{\mathrm{out}}[k]\)。  
  兩邊乘 \(2^{k}\) 再與歸納假設相加：

\[
\begin{align*}
&\mathrm{val}_{k-1}(A)+A[k]2^{k} + \mathrm{val}_{k-1}(B)+B[k]2^{k} \\
&\quad= \mathrm{val}_{k-1}(S) + \mathrm{Sum}[k]2^{k} + 2^{k+1} C_{\mathrm{out}}[k],
\end{align*}
\]

即 \(\mathrm{val}_k(A)+\mathrm{val}_k(B)=\mathrm{val}_k(S)+2^{k+1}C_{\mathrm{out}}[k]\)。

取 \(k=3\) 即定理 T 的**電路版本**：

\[
A + B = S_{\mathrm{adder}} + 16\cdot C_{\mathrm{out}}[3].
\]

還差一步：程式停下來時，(1) 四個 adder 真的在這個穩態，(2) 下方資料區 Sum 等於各 adder 的 Sum 士兵。

---

## 3. 模擬器停在正確穩態

### 3.1 更新是同步組合邏輯（不會「用到這一拍才算出來的進位」）

`tickOnce` 先 `prev = adders.copy()`，再用 `prev` 算下一拍：

- 門：`out' = f(in1, in2)`，入一看的是舊旗  
- Cin：`Cin[0]' = 0`，`Cin[i]' = prev[i-1].cout`  
- 然後騎兵才走一格；送到才改 Adder 的 A／B 或資料區 Sum

因此同一 tick 內四個 adder **平行**，進位一拍只傳一位。這是有延遲的硬體，不是瞬間組合電路，但**穩態方程與第 1–2 節相同**。

依賴圖是 DAG（沒有迴路）：

```
A[i], B[i]  →  Temp1[i], Carry1[i]
Temp1, Cin  →  Sum[i], Carry2[i]
Carry1, Carry2 → Cout[i]
Cout[i] → Cin[i+1]   （只往高位）
```

A／B 送到之後不再被改（送 A／B 騎兵只跑一次）。在 DAG 上同步更新，有限深度後必達唯一穩態，不會振盪。

粗估上界（送到之後）：Temp1／Carry1 約 1 tick，bit0 的 Sum／Cout 再 1 tick，每位進位再 2 tick 量級，四位約十數 tick；加上騎兵路徑（送 A／B 兩格、寫 Sum 四格，Sum 若中途改過會再跑一趟）。`runToHalt(maxSteps = 256)` 遠大於此。

### 3.2 停止條件剛好是「穩態 + 抄完」

`isQuiet()`（`FormationMachine.kt`）四條同時成立才 `finish()`：

1. 沒有騎兵在跑  
2. 四個 bit 的 A、B **都已送到** adder（避免 0+0 開場全白就被當成做完）  
3. 每位寫 Sum 騎兵的 `lastDelivered ==` 該位 Adder Sum（資料區已跟上**現在**的 Sum）  
4. `wouldComputeChange() == false`：用**現在**的旗再套一次第 1 節公式，Cin／五個門都不會變

第 4 條 = 命題 1 的穩態。第 2 條 + 騎兵送達規則 ⇒ adder 的 A／B 等於資料區 A／B。第 3 條 ⇒ `sum.get(i) == adders[i].sum`。再加第 2 節歸納 ⇒ 定理 T。

Sum 在進位漣波時可能先錯後對（先當 Cin=0 算出一個 Sum，Cin 到來再改）。騎兵可能先抄錯的、再抄對的。第 3 條等到最後一次 Adder Sum 也抄完才停，所以下方資料區最後是穩態值，不是中間毛邊。

### 3.3 手動核對一例：7 + 5

\(7=0111_2\)，\(5=0101_2\)（寫成 bit3…bit0）。

| i | A | B | Cin | Sum | Cout | 整數  A+B+Cin |
|---|---|---|-----|-----|------|----------------|
| 0 | 1 | 1 | 0   | 0   | 1    | 2              |
| 1 | 1 | 0 | 1   | 0   | 1    | 2              |
| 2 | 1 | 1 | 1   | 1   | 1    | 3              |
| 3 | 0 | 0 | 1   | 1   | 0    | 1              |

\(S = 1100_2 = 12\)，\(C_{\mathrm{out}}[3]=0\)，\(7+5=12\)。  
測試 `sevenPlusFive` 查的就是這一組。`15+1`：\(1111+0001=10000_2\) → Sum=0、Cout=1（`overflowCarry`）。

---

## 4. 實作的獨立證據：窮舉

輸入只有 \(16\times 16=256\) 種，可以**全部跑過模擬器**，不依賴上面的代數論證：

```kotlin
// Add4BitTest.allPairsMatchIntegerAdd
for (a in 0..15) for (b in 0..15) {
    machine.load(a, b)
    machine.runToHalt()
    assertEquals((a + b) and 0xF, machine.sum.toInt())
    assertEquals(if (a + b >= 16) Bit.ONE else Bit.ZERO, machine.highCout)
}
```

這條測試一次檢查：編碼、五個門、進位串接、騎兵抄 Sum、停止條件、`runToHalt` 真的停。

重跑：

```bash
./gradlew :composeApp:desktopTest
```

代數證明的是**這個電路該算對**；窮舉證明的是**這份程式在整個定義域上算對了**。兩者一起，才叫做「目前設計的加法器是正確的」。

---

## 5. 證明覆蓋到哪裡、沒蓋到哪裡

**有證明／有測到**

- 一位全加器布林恆等式與 8 列真值表  
- 四位漣波進位 ⇒ \(A+B=S+16C_{\mathrm{out}}\)  
- 停機時資料區 Sum、highCout 等於該式  
- 256 組 I/O 與 Kotlin 整數加法一致  

**沒宣稱（也不該用加法正確性來混為一談）**

- Q 版精靈圖是否畫對——只是顯示，不進 `sum`  
- 騎兵路徑好不好看、一格幾像素  
- 加減以外的運算  

若以後改門的接線或 tick 語意，應先改本文件第 1–3 節，並維持 `allPairsMatchIntegerAdd` 全綠。
