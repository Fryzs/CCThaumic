package com.ccthaumic;

import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import dan200.computercraft.api.lua.LuaException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** Turning Thaumaturge aspects into Lua values and back. Aspects are named like {@code "ignis"}. */
public final class Aspects {
    private static final String THAUMATURGE = "thaumaturge";

    private Aspects() {}

    /** {@code {ignis = 12, aqua = 4}}, largest first. Empty table for nothing. */
    public static Map<String, Integer> toLua(AspectList list) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (list == null) {
            return out;
        }
        for (AspectInstance entry : list.sortedByAmount()) {
            out.merge(name(entry.aspect()), entry.amount(), Integer::sum);
        }
        return out;
    }

    /** Short name for Lua: {@code "ignis"} for Thaumaturge's own aspects, the full id for others. */
    public static String name(Holder<IAspect> aspect) {
        return aspect.unwrapKey()
                .map(key -> key.location())
                .map(id -> THAUMATURGE.equals(id.getNamespace()) ? id.getPath() : id.toString())
                .orElse("unknown");
    }

    /** The aspect named by {@code name} ({@code "ignis"} or {@code "thaumaturge:ignis"}). */
    public static Holder<IAspect> byName(Level level, String name) throws LuaException {
        ResourceLocation id = ResourceLocation.tryParse(name.contains(":") ? name : THAUMATURGE + ":" + name);
        if (id == null) {
            throw new LuaException("bad aspect name: " + name);
        }
        Optional<? extends Holder<IAspect>> holder = level.registryAccess()
                .registryOrThrow(IAspect.REGISTRY_KEY)
                .getHolder(id);
        if (holder.isEmpty()) {
            throw new LuaException("no such aspect: " + name);
        }
        return holder.get();
    }
}
