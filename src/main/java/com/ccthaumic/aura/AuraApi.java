package com.ccthaumic.aura;

import com.leclowndu93150.thaumaturge.api.aura.AuraHelper;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * The {@code aura} API on every computer, turtle and pocket computer: the aura of the chunk the
 * computer is in right now (a turtle or pocket computer reads where it currently is).
 *
 * <pre>{@code
 * print(("vis %.1f, flux %.1f of base %d"):format(aura.getVis(), aura.getFlux(), aura.getBase()))
 * }</pre>
 */
public final class AuraApi implements ILuaAPI {
    private final Supplier<ServerLevel> level;
    private final Supplier<BlockPos> pos;

    public AuraApi(IComputerSystem computer) {
        this(computer::getLevel, computer::getPosition);
    }

    /** For tests: an aura reader for a fixed place. */
    public AuraApi(Supplier<ServerLevel> level, Supplier<BlockPos> pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public String[] getNames() {
        return new String[] {"aura"};
    }

    /** Vis in this chunk. */
    @LuaFunction(mainThread = true)
    public final double getVis() {
        return AuraHelper.getVis(level.get(), pos.get());
    }

    /** Flux in this chunk. */
    @LuaFunction(mainThread = true)
    public final double getFlux() {
        return AuraHelper.getFlux(level.get(), pos.get());
    }

    /** The chunk's base aura: how much vis it regrows to. */
    @LuaFunction(mainThread = true)
    public final int getBase() {
        return AuraHelper.getAuraBase(level.get(), pos.get());
    }

    /** Vis plus flux. */
    @LuaFunction(mainThread = true)
    public final double getTotal() {
        return AuraHelper.getTotalAura(level.get(), pos.get());
    }

    /** How saturated the chunk is with flux, 0 to 1 and beyond (taint grows past 1). */
    @LuaFunction(mainThread = true)
    public final double getFluxSaturation() {
        return AuraHelper.getFluxSaturation(level.get(), pos.get());
    }
}
