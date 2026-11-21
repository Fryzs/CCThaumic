package com.ccthaumic.peripheral;

import com.leclowndu93150.thaumaturge.content.aura.node.BlockEntityNode;
import com.leclowndu93150.thaumaturge.content.crucible.BlockEntityCrucible;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Gives every Thaumaturge block that holds aspects a CC: Tweaked peripheral, through CC's public
 * {@link PeripheralCapability}. Blocks without aspects get none, so CC's own generic peripherals
 * (inventories and the like) still apply to them.
 */
public final class ThaumicPeripherals {
    private static final String THAUMATURGE = "thaumaturge";

    private ThaumicPeripherals() {}

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            var id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
            if (id != null && THAUMATURGE.equals(id.getNamespace())) {
                register(event, type);
            }
        }
    }

    private static <T extends BlockEntity> void register(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(PeripheralCapability.get(), type, (block, side) -> peripheralFor(block));
    }

    /** The peripheral for a Thaumaturge block, or null if it holds no aspects. */
    public static @Nullable IPeripheral peripheralFor(BlockEntity block) {
        if (block.getLevel() == null || block.getLevel().isClientSide()) {
            return null;
        }
        if (block instanceof BlockEntityNode node) {
            return new NodePeripheral(node);
        }
        if (block instanceof BlockEntityCrucible crucible) {
            return new CruciblePeripheral(crucible);
        }
        return AspectContainerPeripheral.containerOf(block) != null ? new AspectContainerPeripheral(block) : null;
    }
}
