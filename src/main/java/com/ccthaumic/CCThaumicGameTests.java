package com.ccthaumic;

import com.ccthaumic.aura.AuraApi;
import com.ccthaumic.peripheral.AspectContainerPeripheral;
import com.ccthaumic.peripheral.CruciblePeripheral;
import com.ccthaumic.peripheral.NodePeripheral;
import com.leclowndu93150.thaumaturge.api.aspect.AspectCapabilities;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.IAspectContainer;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Headless checks for CC: Thaumic ({@code ./gradlew runGameTestServer}): each test places a
 * Thaumaturge block, asks for the peripheral through CC's public capability (as a computer would)
 * and checks its type and values. Only run when game tests are enabled.
 */
@GameTestHolder(CCThaumic.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CCThaumicGameTests {
    private static final BlockPos AT = new BlockPos(2, 1, 2);

    @GameTest(template = "empty")
    public static void jarShowsItsEssentia(GameTestHelper helper) throws Exception {
        place(helper, "thaumaturge:jar_normal", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        helper.assertTrue(AspectContainerPeripheral.TYPE.equals(peripheral.getType()),
                "jar type should be aspect_container, was " + peripheral.getType());
        AspectContainerPeripheral jar = (AspectContainerPeripheral) peripheral;
        helper.assertTrue(jar.getTotal() == 0, "a new jar should be empty");

        IAspectContainer container = helper.getLevel().getCapability(
                AspectCapabilities.CONTAINER, helper.absolutePos(AT), null);
        helper.assertTrue(container != null, "the jar should expose Thaumaturge's aspect container");
        Holder<IAspect> ignis = Aspects.byName(helper.getLevel(), "ignis");
        // Thaumaturge's fill() returns what did NOT fit.
        int filled = 10 - container.fill(ignis, 10);
        helper.assertTrue(filled > 0, "could not put ignis into the jar (filled " + filled + ")");

        Map<String, Integer> aspects = jar.getAspects();
        helper.assertTrue(aspects.getOrDefault("ignis", 0) == filled,
                "getAspects should show " + filled + " ignis, got " + aspects);
        helper.assertTrue(jar.getAmount("ignis") == filled, "getAmount(ignis) should be " + filled);
        helper.assertTrue(jar.getAmount("thaumaturge:ignis") == filled, "full aspect ids should work too");
        helper.assertTrue(jar.getTotal() == filled, "getTotal should be " + filled);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void crucibleIsPeripheral(GameTestHelper helper) throws Exception {
        place(helper, "thaumaturge:crucible", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        helper.assertTrue(peripheral instanceof CruciblePeripheral, "crucible should get the crucible peripheral");
        helper.assertTrue(peripheral.getAdditionalTypes().contains(AspectContainerPeripheral.TYPE),
                "crucible should also be an aspect_container");
        CruciblePeripheral crucible = (CruciblePeripheral) peripheral;
        helper.assertTrue(crucible.getWater() == 0, "a new crucible has no water");
        helper.assertTrue(crucible.getWaterCapacity() > 0, "water capacity should be positive");
        helper.assertTrue(crucible.getAspects().isEmpty(), "a new crucible holds no essentia");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void nodeIsPeripheral(GameTestHelper helper) throws Exception {
        place(helper, "thaumaturge:node", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        helper.assertTrue(peripheral instanceof NodePeripheral, "node should get the aura_node peripheral, got "
                + peripheral.getType());
        NodePeripheral node = (NodePeripheral) peripheral;
        helper.assertTrue(node.getNodeType() != null, "node type should be set");
        helper.assertTrue(node.getVis() >= 0, "vis cannot be negative");
        node.getBaseAspects();
        node.getAspects();
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void plainBlockGetsNoPeripheral(GameTestHelper helper) {
        place(helper, "thaumaturge:node_stabilizer", AT);
        BlockPos pos = helper.absolutePos(AT);
        IPeripheral peripheral = helper.getLevel().getCapability(PeripheralCapability.get(), pos, Direction.UP);
        helper.assertTrue(peripheral == null, "a block without aspects should get no CC: Thaumic peripheral, got "
                + (peripheral == null ? null : peripheral.getType()));
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void auraApiReadsTheChunk(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(AT);
        AuraApi aura = new AuraApi(helper::getLevel, () -> pos);
        helper.assertTrue(aura.getNames().length == 1 && "aura".equals(aura.getNames()[0]), "API should be named aura");
        helper.assertTrue(aura.getBase() >= 0, "aura base cannot be negative");
        helper.assertTrue(aura.getVis() >= 0, "vis cannot be negative");
        helper.assertTrue(aura.getFlux() >= 0, "flux cannot be negative");
        helper.assertTrue(Math.abs(aura.getTotal() - (aura.getVis() + aura.getFlux())) < 0.01,
                "total should be vis + flux");
        helper.succeed();
    }

    // --- helpers ---------------------------------------------------------------------------

    private static void place(GameTestHelper helper, String blockId, BlockPos pos) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block).toString().equals(blockId),
                "block " + blockId + " is not registered");
        helper.setBlock(pos, block);
    }

    private static IPeripheral peripheralAt(GameTestHelper helper, BlockPos relative) {
        IPeripheral peripheral = helper.getLevel().getCapability(
                PeripheralCapability.get(), helper.absolutePos(relative), Direction.UP);
        helper.assertTrue(peripheral != null, "CC: Tweaked sees no peripheral at " + relative);
        return peripheral;
    }
}
