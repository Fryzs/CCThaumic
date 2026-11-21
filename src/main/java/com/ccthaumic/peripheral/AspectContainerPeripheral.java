package com.ccthaumic.peripheral;

import com.ccthaumic.Aspects;
import com.leclowndu93150.thaumaturge.api.aspect.AspectCapabilities;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectContainer;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Any Thaumaturge block that holds aspects: essentia jars, the alembic, the infusion matrix, the
 * recharge pedestal, the essentia mirror (type {@code aspect_container}). Read-only.
 *
 * <pre>{@code
 * local jar = peripheral.find("aspect_container")
 * for aspect, amount in pairs(jar.getAspects()) do print(aspect, amount) end
 * }</pre>
 */
public class AspectContainerPeripheral implements IPeripheral {
    public static final String TYPE = "aspect_container";

    protected final BlockEntity block;

    public AspectContainerPeripheral(BlockEntity block) {
        this.block = block;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Set<String> getAdditionalTypes() {
        return Set.of();
    }

    @Override
    public Object getTarget() {
        return block;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other != null && other.getClass() == getClass() && ((AspectContainerPeripheral) other).block == block;
    }

    /** Aspects held, as {@code {ignis = 12, aqua = 4}}, largest first. */
    @LuaFunction(mainThread = true)
    public final Map<String, Integer> getAspects() throws LuaException {
        return Aspects.toLua(container().getAspects());
    }

    /** How much of one aspect it holds ({@code "ignis"} or {@code "thaumaturge:ignis"}). */
    @LuaFunction(mainThread = true)
    public final int getAmount(String aspect) throws LuaException {
        return container().amountOf(Aspects.byName(level(), aspect));
    }

    /** Total of all aspects held. */
    @LuaFunction(mainThread = true)
    public final int getTotal() throws LuaException {
        AspectList aspects = container().getAspects();
        return aspects == null ? 0 : aspects.totalAmount();
    }

    protected final Level level() throws LuaException {
        Level level = block.getLevel();
        if (level == null || block.isRemoved()) {
            throw new LuaException("the block is gone");
        }
        return level;
    }

    protected final IAspectContainer container() throws LuaException {
        IAspectContainer container = containerOf(block);
        if (container == null) {
            throw new LuaException("this block holds no aspects right now");
        }
        return container;
    }

    /** The block's aspect container: Thaumaturge's capability, or the block itself. */
    static @Nullable IAspectContainer containerOf(BlockEntity block) {
        Level level = block.getLevel();
        if (level != null) {
            IAspectContainer viaCapability = level.getCapability(
                    AspectCapabilities.CONTAINER, block.getBlockPos(), block.getBlockState(), block, null);
            if (viaCapability != null) {
                return viaCapability;
            }
        }
        return block instanceof IAspectContainer self ? self : null;
    }
}
