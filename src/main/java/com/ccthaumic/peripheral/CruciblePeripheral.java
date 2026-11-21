package com.ccthaumic.peripheral;

import com.leclowndu93150.thaumaturge.content.crucible.BlockEntityCrucible;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Set;

/**
 * The crucible (type {@code crucible}, also {@code aspect_container}): the essentia dissolved in
 * it plus its heat and water.
 *
 * <pre>{@code
 * local c = peripheral.find("crucible")
 * print("heat", c.getHeat(), "water", c.getWater(), "mB")
 * }</pre>
 */
public final class CruciblePeripheral extends AspectContainerPeripheral {
    public static final String TYPE = "crucible";

    private final BlockEntityCrucible crucible;

    public CruciblePeripheral(BlockEntityCrucible crucible) {
        super(crucible);
        this.crucible = crucible;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Set<String> getAdditionalTypes() {
        return Set.of(AspectContainerPeripheral.TYPE);
    }

    /** Heat of the crucible (it needs a heat source underneath). */
    @LuaFunction(mainThread = true)
    public final int getHeat() throws LuaException {
        level();
        return crucible.getHeat();
    }

    /** Water in the crucible, in mB. */
    @LuaFunction(mainThread = true)
    public final int getWater() throws LuaException {
        level();
        return crucible.getTank().getFluidAmount();
    }

    /** Most water it holds, in mB. */
    @LuaFunction(mainThread = true)
    public final int getWaterCapacity() throws LuaException {
        level();
        return crucible.getTank().getCapacity();
    }
}
