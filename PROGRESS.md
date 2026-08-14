# Progress — Human-Formation Calculator v0.4

## 目前狀態

**Q 版舉旗畫面已可玩。** 士兵／騎兵依 bit 舉黑或白旗；運算士兵胸口 A=AND、O=OR、X=XOR。Desktop 編譯與 `desktopTest` 通過。

## 已完成

- [x] v0.3 空間並行四全加器
- [x] Q 版秦俑士兵、騎兵（白旗／黑旗）去背精靈圖
- [x] 運算士兵胸口字母；變色／騎行時金框提示

## 下一步（非本版）

- 騎兵路徑連續插值
- 加減以外的運算陣

## 怎麼測

```
./gradlew :composeApp:run
```

1. 開始後上方 A/B 士兵舉對應旗，騎兵騎馬把旗色送進各 adder
2. Temp1／Sum 胸口是 **X**，Carry1／Carry2 是 **A**，Cout 是 **O**
3. Play 跑完 7+5 → 下方 Sum=12

## 問題與決策

- 胸口字母用 Compose 疊在胸甲上，避免生成圖把 A/O/X 畫錯。
- 原先規劃的下一步是騎兵插值與其他運算；依你這次要求先把畫面做好看。
