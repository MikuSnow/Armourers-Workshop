package extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, 1.18.2)")
@Extension
public class LootFunction {

    public static <T extends LootItemFunctionType> IRegistryKey<T> registerItemLootFunctionFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        T value = supplier.get();
        ResourceLocation registryName = ModConstants.key(name);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, registryName, supplier.get());
        ModLog.debug("Registering Loot Item Function Type '{}'", registryName);
        return AbstractForgeRegistryEntry.of(registryName, () -> value);
    }
}
