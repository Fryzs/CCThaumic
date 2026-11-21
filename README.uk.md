<div align="center">

# 🔮 CC: Thaumic

**Thaumaturge для комп'ютерів CC: Tweaked: есенція, тигель, вузли аури й сама аура.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-E67E22)
![Requires](https://img.shields.io/badge/requires-Thaumaturge%20%2B%20CC%3A%20Tweaked-8E44AD)
![Version](https://img.shields.io/badge/version-0.1.0-2E86C1)
![License](https://img.shields.io/badge/license-MIT-27AE60)

[English](README.md) · **Українська**

</div>

---

Дай комп'ютерам побачити таємне: скільки есенції в кожній банці, що розчинено в тиглі, що тримає вузол аури — і
скільки вису та флюксу в повітрі навколо комп'ютера. Рахуй запаси есенції, стеж за флюксом, поки не поповзла
порча, або керуй алхімією з Lua. Усе **лише для читання**: CC: Thaumic нічого не змінює у світі.

## ✨ Периферія

Став комп'ютер поруч із блоком Thaumaturge (або підключи дротовим модемом) і
`peripheral.wrap("сторона")` чи `peripheral.find("тип")`.

| Тип | Блоки | Функції |
| --- | --- | --- |
| `aspect_container` | банки есенції (звичайна, порожнеча, креативна…), алембік, матриця інфузії, п'єдестал підзарядки, дзеркало есенції | `getAspects()`, `getAmount(аспект)`, `getTotal()` |
| `crucible` *(+ `aspect_container`)* | тигель | те саме + `getHeat()`, `getWater()`, `getWaterCapacity()` (мБ) |
| `aura_node` *(+ `aspect_container`)* | вузол аури | те саме + `getNodeType()`, `getModifier()`, `getBaseAspects()`, `isEnergized()`, `getVis()`, `getFlux()` |

- `getAspects()` повертає таблицю на кшталт `{ ignis = 12, aqua = 4 }`, найбільше — першим.
- Аспект можна назвати коротко (`"ignis"`) або повністю (`"thaumaturge:ignis"`).
- `getNodeType()`: `normal`, `unstable`, `dark`, `tainted`, `hungry`, `pure`; `getModifier()`: `bright`, `pale`,
  `fading` або `nil`.

Блоки Thaumaturge без аспектів периферією CC: Thaumic не стають, тож для них лишаються звичайні можливості
CC: Tweaked (наприклад, інвентарі).

## 🌫️ API `aura`

На кожному комп'ютері, черепашці й кишеньковому комп'ютері — аура чанка, де вони зараз стоять:

| Функція | Що повертає |
| --- | --- |
| `aura.getVis()` | вис у чанку |
| `aura.getFlux()` | флюкс у чанку |
| `aura.getBase()` | база аури — до скількох вису чанк відновлюється |
| `aura.getTotal()` | вис + флюкс |
| `aura.getFluxSaturation()` | насиченість флюксом (понад 1 — росте порча) |

## 🚀 Приклади

### Запас есенції в усій мережі

```lua
local total = {}
for _, name in ipairs(peripheral.getNames()) do
  if peripheral.hasType(name, "aspect_container") then
    for aspect, amount in pairs(peripheral.call(name, "getAspects")) do
      total[aspect] = (total[aspect] or 0) + amount
    end
  end
end
for aspect, amount in pairs(total) do print(aspect, amount) end
```

### Тривога через флюкс

```lua
while true do
  local sat = aura.getFluxSaturation()
  redstone.setOutput("top", sat > 0.5)   -- лампа чи сирена зверху
  print(("вис %.1f  флюкс %.1f  (%.0f%%)"):format(aura.getVis(), aura.getFlux(), sat * 100))
  sleep(5)
end
```

> 💡 З [CC: CSharp](https://github.com/Fryzs/CCCSharp) ці самі програми можна писати на C#:
> `Aura.GetFluxSaturation()`, `Peripheral.Find("crucible").GetHeat()`.

## 📦 Встановлення

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| Завантажувач | NeoForge 21.1.x |
| Обов'язково | Thaumaturge ≥ 1.1.0 (з Curios і Lithostitched), CC: Tweaked ≥ 1.110 |
| Сторона | **сервер** (і одиночна гра); гравцям на сервері не потрібен |

Поклади `ccthaumic-0.1.0.jar` у папку `mods`.

Працює разом із [Create: Thaumaturgy](https://www.curseforge.com/minecraft/mc-mods/createthaumaturgy): уся збірка
TiTy Aeronautics з обома модами завантажується й проходить тести.

## 🛠️ Збірка й тести

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` запускає сервер **без вікна**: ставить банку (і наливає в неї ignis), тигель, вузол аури й
звичайний блок, перевіряє, що CC: Tweaked бачить правильну периферію з правильними значеннями, тестує API `aura`
і сам завершується.

Для компіляції потрібен jar Thaumaturge у `libs/` (лише локально: не комітиться й не входить у мод). API
CC: Tweaked береться з `maven.squiddev.cc`. Для тестів у `run/mods`: Thaumaturge, Curios, Lithostitched,
CC: Tweaked.

## 🧩 Як це влаштовано

Периферія реєструється через публічну `PeripheralCapability` CC: Tweaked для блоків Thaumaturge, у яких є його
можливість `AspectCapabilities.CONTAINER`. API `aura` — через `ComputerCraftAPI.registerAPIFactory` і публічний
`AuraHelper` Thaumaturge. Код інших модів не копіюється.

## 📄 Ліцензія

Код CC: Thaumic — [MIT](LICENSE). Thaumaturge і CC: Tweaked — окремі проєкти під своїми ліцензіями; жодна їхня
частина сюди не входить.
