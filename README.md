<div align="center">

# 🔮 CC: Thaumic

**Thaumaturge for CC: Tweaked computers: essentia, crucibles, aura nodes and the aura itself.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-E67E22)
![Requires](https://img.shields.io/badge/requires-Thaumaturge%20%2B%20CC%3A%20Tweaked-8E44AD)
![Version](https://img.shields.io/badge/version-0.1.0-2E86C1)
![License](https://img.shields.io/badge/license-MIT-27AE60)

**English** · [Українська](README.uk.md)

</div>

---

Let your computers see the arcane: how much essentia sits in every jar, what is dissolved in the crucible, what
an aura node holds — and how much vis and flux is in the air around the computer. Count your essentia stock,
watch for flux before taint spreads, or drive alchemy with Lua. Everything is **read-only**: CC: Thaumic never
changes the world.

## ✨ Peripherals

Put a computer next to a Thaumaturge block (or connect it with a wired modem) and use
`peripheral.wrap("side")` or `peripheral.find("type")`.

| Type | Blocks | Functions |
| --- | --- | --- |
| `aspect_container` | essentia jars (normal, void, creative…), alembic, infusion matrix, recharge pedestal, essentia mirror | `getAspects()`, `getAmount(aspect)`, `getTotal()` |
| `crucible` *(+ `aspect_container`)* | crucible | the above + `getHeat()`, `getWater()`, `getWaterCapacity()` (mB) |
| `aura_node` *(+ `aspect_container`)* | aura node | the above + `getNodeType()`, `getModifier()`, `getBaseAspects()`, `isEnergized()`, `getVis()`, `getFlux()` |

- `getAspects()` returns a table like `{ ignis = 12, aqua = 4 }`, largest first.
- Aspects can be named short (`"ignis"`) or in full (`"thaumaturge:ignis"`).
- `getNodeType()`: `normal`, `unstable`, `dark`, `tainted`, `hungry`, `pure`; `getModifier()`: `bright`, `pale`,
  `fading` or `nil`.

Thaumaturge blocks without aspects get no CC: Thaumic peripheral, so CC: Tweaked's own generic peripherals
(inventories and so on) still apply to them.

## 🌫️ The `aura` API

Every computer, turtle and pocket computer gets an `aura` API for the chunk it is in right now:

| Function | Returns |
| --- | --- |
| `aura.getVis()` | vis in the chunk |
| `aura.getFlux()` | flux in the chunk |
| `aura.getBase()` | the base aura — how much vis the chunk regrows to |
| `aura.getTotal()` | vis + flux |
| `aura.getFluxSaturation()` | how saturated the chunk is with flux (above 1, taint grows) |

## 🚀 Examples

### Essentia stock across the whole network

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

### Flux alarm

```lua
while true do
  local sat = aura.getFluxSaturation()
  redstone.setOutput("top", sat > 0.5)   -- a lamp or siren on top
  print(("vis %.1f  flux %.1f  (%.0f%%)"):format(aura.getVis(), aura.getFlux(), sat * 100))
  sleep(5)
end
```

> 💡 With [CC: CSharp](https://github.com/Fryzs/CCCSharp) you can write the same programs in C#:
> `Aura.GetFluxSaturation()`, `Peripheral.Find("crucible").GetHeat()`.

## 📦 Installation

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.x |
| Required | Thaumaturge ≥ 1.1.0 (with Curios and Lithostitched), CC: Tweaked ≥ 1.110 |
| Side | **server** (and single player); clients joining a server do not need it |

Drop `ccthaumic-0.1.0.jar` into the `mods` folder.

Works alongside [Create: Thaumaturgy](https://www.curseforge.com/minecraft/mc-mods/createthaumaturgy): the whole
TiTy Aeronautics pack with both mods loads and passes the tests.

## 🛠️ Building and tests

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` starts a **headless** server (no window): it places a jar (and pours ignis into it), a crucible,
an aura node and a plain block, checks that CC: Tweaked sees the right peripheral with the right values, tests the
`aura` API, and exits.

Compiling needs the Thaumaturge jar in `libs/` (local only: never committed, never shipped). The CC: Tweaked API
comes from `maven.squiddev.cc`. For the tests, `run/mods` holds Thaumaturge, Curios, Lithostitched and CC: Tweaked.

## 🧩 How it works

Peripherals are registered through CC: Tweaked's public `PeripheralCapability` for Thaumaturge blocks that have its
`AspectCapabilities.CONTAINER` capability. The `aura` API uses `ComputerCraftAPI.registerAPIFactory` and
Thaumaturge's public `AuraHelper`. No code from other mods is copied.

## 📄 License

The CC: Thaumic code is [MIT](LICENSE). Thaumaturge and CC: Tweaked are separate projects under their own licenses;
no part of them is included here.
