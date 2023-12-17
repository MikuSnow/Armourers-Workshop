package extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, 1.19)")
@Extension
public class ForgeProvider {

    public static final IRegistry<Block> BLOCKS = new Proxy<>(Block.class, ForgeRegistries.BLOCKS);
    public static final IRegistry<Item> ITEMS = new Proxy<>(Item.class, ForgeRegistries.ITEMS);
    public static final IRegistry<MenuType<?>> MENU_TYPES = new Proxy<>(MenuType.class, ForgeRegistries.CONTAINERS);
    public static final IRegistry<EntityType<?>> ENTITY_TYPES = new Proxy<>(EntityType.class, ForgeRegistries.ENTITIES);
    public static final IRegistry<DataSerializerEntry> ENTITY_DATA_SERIALIZER = new Proxy<>(EntityDataSerializer.class, ForgeRegistries.DATA_SERIALIZERS);
    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new Proxy<>(BlockEntityType.class, ForgeRegistries.BLOCK_ENTITIES);
    public static final IRegistry<SoundEvent> SOUND_EVENTS = new Proxy<>(SoundEvent.class, ForgeRegistries.SOUND_EVENTS);

    public static <T extends Item> IRegistryKey<T> registerItemFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static <T extends IItemTag> IRegistryKey<T> registerItemTagFO(@ThisClass Class<?> clazz, String name) {
        ResourceLocation registryName = ModConstants.key(name);
        Tag<Item> tag = ItemTags.createOptional(registryName);
        ModLog.debug("Registering Item Tag '{}'", registryName);
        return AbstractForgeRegistryEntry.cast(registryName, () -> (IItemTag) itemStack -> itemStack.is(tag));
    }

    public static <T extends CreativeModeTab> IRegistryKey<T> registerItemGroupFO(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ResourceLocation registryName = ModConstants.key(name);
        CreativeModeTab tab = new CreativeModeTab(registryName.getNamespace() + "." + registryName.getPath()) {
            @Override
            public ItemStack makeIcon() {
                return icon.get().get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> arg) {
                itemProvider.accept(arg);
            }
        };
        ModLog.debug("Registering Creative Mode Tab '{}'", registryName);
        return AbstractForgeRegistryEntry.cast(registryName, () -> tab);
    }

    public static <T extends Block> IRegistryKey<T> registerBlockFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static <T extends BlockEntity, V extends BlockEntityType<T>> IRegistryKey<V> registerBlockEntityTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return BLOCK_ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends Entity, V extends EntityType<T>> IRegistryKey<V> registerEntityTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends EntityDataSerializer<?>> IRegistryKey<T> registerEntityDataSerializerFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        IRegistryKey<DataSerializerEntry> entry = ENTITY_DATA_SERIALIZER.register(name, () -> new DataSerializerEntry(supplier.get()));
        return AbstractForgeRegistryEntry.cast(entry.getRegistryName(), () -> entry.get().getSerializer());
    }

    public static <T extends AbstractContainerMenu, V extends MenuType<T>> IRegistryKey<V> registerMenuTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return MENU_TYPES.register(name, supplier);
    }

    public static <T extends SoundEvent> IRegistryKey<T> registerSoundEventFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return SOUND_EVENTS.register(name, supplier);
    }

    public static class Proxy<T extends IForgeRegistryEntry<T>> extends AbstractForgeRegistry<T> {

        private final Supplier<IForgeRegistry<T>> registry;
        private final DeferredRegister<T> deferredRegistry;

        public Proxy(Class<?> type, IForgeRegistry<T> registry) {
            this(type, () -> registry, DeferredRegister.create(registry, ModConstants.MOD_ID));
        }

        public Proxy(Class<?> type, Supplier<IForgeRegistry<T>> registry, DeferredRegister<T> deferredRegistry) {
            super(type, deferredRegistry);
            this.registry = registry;
            this.deferredRegistry = deferredRegistry;
        }

        @Override
        public <I extends T> Supplier<I> deferredRegister(String name, Supplier<? extends I> provider) {
            return deferredRegistry.register(name, provider);
        }

        @Override
        public ResourceLocation getKey(T object) {
            return registry.get().getKey(object);
        }

        @Override
        public T getValue(ResourceLocation registryName) {
            return registry.get().getValue(registryName);
        }
    }
}
