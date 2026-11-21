package com.ccthaumic.peripheral;

import com.ccthaumic.Aspects;
import com.leclowndu93150.thaumaturge.api.aura.AuraHelper;
import com.leclowndu93150.thaumaturge.api.nodes.NodeModifier;
import com.leclowndu93150.thaumaturge.api.nodes.NodeType;
import com.leclowndu93150.thaumaturge.content.aura.node.BlockEntityNode;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * An aura node (type {@code aura_node}, also {@code aspect_container}): its current aspects plus
 * the node's own properties and the aura of its chunk.
 *
 * <pre>{@code
 * local node = peripheral.find("aura_node")
 * print(node.getNodeType(), node.getModifier())
 * }</pre>
 */
public final class NodePeripheral extends AspectContainerPeripheral {
    public static final String TYPE = "aura_node";

    private final BlockEntityNode node;

    public NodePeripheral(BlockEntityNode node) {
        super(node);
        this.node = node;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Set<String> getAdditionalTypes() {
        return Set.of(AspectContainerPeripheral.TYPE);
    }

    /** {@code "normal"}, {@code "unstable"}, {@code "dark"}, {@code "tainted"}, {@code "hungry"} or {@code "pure"}. */
    @LuaFunction(mainThread = true)
    public final @Nullable String getNodeType() throws LuaException {
        level();
        NodeType type = node.getNodeType();
        return type == null ? null : type.getSerializedName();
    }

    /** {@code "bright"}, {@code "pale"}, {@code "fading"}, or nil for none. */
    @LuaFunction(mainThread = true)
    public final @Nullable String getModifier() throws LuaException {
        level();
        NodeModifier modifier = node.getNodeModifier();
        return modifier == null ? null : modifier.getSerializedName();
    }

    /** The aspects the node regrows to, like {@link #getAspects()}. */
    @LuaFunction(mainThread = true)
    public final Map<String, Integer> getBaseAspects() throws LuaException {
        level();
        return Aspects.toLua(node.getAspectsBase());
    }

    /** Whether a node transducer has energized it. */
    @LuaFunction(mainThread = true)
    public final boolean isEnergized() throws LuaException {
        level();
        return node.isEnergized();
    }

    /** Vis in the node's chunk. */
    @LuaFunction(mainThread = true)
    public final double getVis() throws LuaException {
        return AuraHelper.getVis(level(), node.getBlockPos());
    }

    /** Flux in the node's chunk. */
    @LuaFunction(mainThread = true)
    public final double getFlux() throws LuaException {
        return AuraHelper.getFlux(level(), node.getBlockPos());
    }
}
