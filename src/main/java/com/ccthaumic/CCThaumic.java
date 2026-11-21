package com.ccthaumic;

import com.ccthaumic.aura.AuraApi;
import com.ccthaumic.peripheral.ThaumicPeripherals;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * CC: Thaumic: Thaumaturge for CC: Tweaked computers. Both mods are required dependencies.
 *
 * <ul>
 *   <li>Every Thaumaturge block that holds aspects (jars, crucible, nodes, alembic, infusion
 *       matrix, recharge pedestal, essentia mirror) is a peripheral: see {@link ThaumicPeripherals}.
 *   <li>Every computer gets an {@code aura} API for the chunk it stands in: see {@link AuraApi}.
 * </ul>
 */
@Mod(CCThaumic.MOD_ID)
public final class CCThaumic {
    public static final String MOD_ID = "ccthaumic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CCThaumic(IEventBus modBus) {
        modBus.addListener(ThaumicPeripherals::registerCapabilities);
        modBus.addListener(CCThaumic::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComputerCraftAPI.registerAPIFactory(AuraApi::new);
            LOGGER.info("CC: Thaumic: Thaumaturge peripherals and the aura API are on");
        });
    }
}
