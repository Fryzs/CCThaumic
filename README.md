# CC: Thaumic

Аддон для Minecraft 1.21.1 (NeoForge), що з'єднує **Thaumaturge** і **CC: Tweaked**: комп'ютери читають
есенцію в банках, тигель, вузли аури та інші блоки з аспектами, а також вис і флюкс аури навколо себе.
Потрібні обидва моди. Усе лише для читання: CC: Thaumic нічого не змінює у світі.

## Периферія

Став комп'ютер поруч із блоком Thaumaturge (або підключи дротовим модемом) і
`peripheral.wrap("сторона")` чи `peripheral.find("тип")`.

| Тип | Блоки | Функції |
| --- | --- | --- |
| `aspect_container` | банки есенції (звичайна, порожнеча, креативна…), алембік, матриця інфузії, п'єдестал підзарядки, дзеркало есенції | `getAspects()`, `getAmount(аспект)`, `getTotal()` |
| `crucible` (+ `aspect_container`) | тигель | те саме + `getHeat()`, `getWater()`, `getWaterCapacity()` (мБ) |
| `aura_node` (+ `aspect_container`) | вузол аури | те саме + `getNodeType()`, `getModifier()`, `getBaseAspects()`, `isEnergized()`, `getVis()`, `getFlux()` |

- `getAspects()` повертає таблицю `{ ignis = 12, aqua = 4 }` (найбільше — першим).
- Аспект можна назвати коротко (`"ignis"`) або повністю (`"thaumaturge:ignis"`).
- `getNodeType()`: `normal`, `unstable`, `dark`, `tainted`, `hungry`, `pure`; `getModifier()`: `bright`, `pale`,
  `fading` або `nil`.

Блоки Thaumaturge без аспектів периферією CC: Thaumic не стають (для них лишаються звичайні можливості
CC: Tweaked, наприклад інвентарі).

## API `aura`

На кожному комп'ютері, черепашці й кишеньковому комп'ютері — аура чанка, де вони зараз стоять:

| Функція | Що повертає |
| --- | --- |
| `aura.getVis()` | вис у чанку |
| `aura.getFlux()` | флюкс у чанку |
| `aura.getBase()` | база аури — до скількох вису чанк відновлюється |
| `aura.getTotal()` | вис + флюкс |
| `aura.getFluxSaturation()` | насиченість флюксом (понад 1 — росте порча) |

## Приклади

### Склад есенції: усі банки в мережі

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

### Сигнал, коли флюкс у чанку зростає

```lua
while true do
  local sat = aura.getFluxSaturation()
  redstone.setOutput("top", sat > 0.5)   -- лампа/сирена зверху
  print(("вис %.1f  флюкс %.1f  (%.0f%%)"):format(aura.getVis(), aura.getFlux(), sat * 100))
  sleep(5)
end
```

## Збірка й тести

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` — сервер **без вікна**: ставить банку (і наливає в неї ignis), тигель, вузол і звичайний
блок, перевіряє, що CC: Tweaked бачить правильну периферію з правильними значеннями, перевіряє API `aura`,
і сам завершується.

Для компіляції потрібен jar Thaumaturge у `libs/` (локально; не комітиться й не входить у мод). API
CC: Tweaked береться з `maven.squiddev.cc`. Для тестів у `run/mods`: Thaumaturge, Curios, Lithostitched,
CC: Tweaked.

Jar: `build/libs/ccthaumic-0.1.0.jar` — ставиться на сервер (і в одиночну гру); клієнтам на сервері
не обов'язковий.

## Як влаштовано

Периферія реєструється через публічну `PeripheralCapability` CC: Tweaked для блоків Thaumaturge, у яких є
його можливість `AspectCapabilities.CONTAINER`. API `aura` — через `ComputerCraftAPI.registerAPIFactory` і
публічний `AuraHelper` Thaumaturge. Код інших модів не копіюється.

## Ліцензія

Код CC: Thaumic — [MIT](LICENSE). Thaumaturge і CC: Tweaked — окремі проєкти під своїми ліцензіями;
жодна їхня частина сюди не входить.
