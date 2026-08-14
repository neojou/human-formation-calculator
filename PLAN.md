# v0.3 實作計畫：空間並行的 4-bit 人列加法

取代 v0.2 微指令機。完成定義見 `DESIGN.md`。

## 要刪的

- 微指令列表 UI、單一 XOR/AND/OR/NOT 場
- `MicroOp`、`BitLoc`、`Add4BitProgram`

## 要留的

- `Bit`、`Register`、`Logic`、`parseNibble`
- `App()` 啟動、`com.neojou.tools.*`、Step / Play / Pause

## 新核心

- `FieldLayout`：全場座標（資料區 + 四個 adder）
- `FormationMachine.tick()`：並行更新門士兵、Cin、騎兵
- Snapshot 給 UI 畫士兵與騎兵

## 新畫面

```
上方：A[3]B[3] … A[0]B[0]
中間：四個 1-bit full-adder（手稿佈局）+ 騎兵
下方：S[3] … S[0]
底列：輸入 / 開始 / Step / Play
```

## 驗證

- 16×16 加法與 Cout
- 單 tick 不會一次做完 7+5
- Desktop / Wasm 編譯
