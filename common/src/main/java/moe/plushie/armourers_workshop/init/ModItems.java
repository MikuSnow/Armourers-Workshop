package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeItemRenderer;
import moe.plushie.armourers_workshop.builder.item.BlendingToolItem;
import moe.plushie.armourers_workshop.builder.item.BlockMarkerItem;
import moe.plushie.armourers_workshop.builder.item.BurnToolItem;
import moe.plushie.armourers_workshop.builder.item.ColorNoiseToolItem;
import moe.plushie.armourers_workshop.builder.item.ColorPickerItem;
import moe.plushie.armourers_workshop.builder.item.DodgeToolItem;
import moe.plushie.armourers_workshop.builder.item.HueToolItem;
import moe.plushie.armourers_workshop.builder.item.PaintRollerItem;
import moe.plushie.armourers_workshop.builder.item.PaintbrushItem;
import moe.plushie.armourers_workshop.builder.item.ShadeNoiseToolItem;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.builder.item.SoapItem;
import moe.plushie.armourers_workshop.core.client.render.MannequinItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.item.ArmourersHammerItem;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.item.GiftSackItem;
import moe.plushie.armourers_workshop.core.item.LinkingToolItem;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.item.MannequinToolItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.item.SkinUnlockItem;
import moe.plushie.armourers_workshop.core.item.WandOfStyleItem;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ModItems {

    private static final ItemBuilder MAIN = new ItemBuilder(ModItemGroups.MAIN_GROUP);
    private static final ItemBuilder BUILDING = new ItemBuilder(ModItemGroups.BUILDING_GROUP);
    private static final ItemBuilder NONE = new ItemBuilder(null);

    public static final IRegistryKey<Item> SKIN = NONE.block(SkinItem::new, ModBlocks.SKINNABLE).bind(() -> SkinItemRenderer::getInstance).build("skin");
    public static final IRegistryKey<Item> MANNEQUIN = MAIN.normal(MannequinItem::new).rarity(Rarity.RARE).bind(() -> MannequinItemRenderer::getInstance).build("mannequin");

    public static final IRegistryKey<Item> SKIN_LIBRARY = MAIN.block(ModBlocks.SKIN_LIBRARY).build("skin-library");
    public static final IRegistryKey<Item> SKIN_LIBRARY_CREATIVE = MAIN.block(ModBlocks.SKIN_LIBRARY_CREATIVE).rarity(Rarity.EPIC).build("skin-library-creative");
    public static final IRegistryKey<Item> SKIN_LIBRARY_GLOBAL = MAIN.block(ModBlocks.SKIN_LIBRARY_GLOBAL).build("skin-library-global");

    public static final IRegistryKey<Item> SKINNING_TABLE = MAIN.block(ModBlocks.SKINNING_TABLE).build("skinning-table");
    public static final IRegistryKey<Item> DYE_TABLE = MAIN.block(ModBlocks.DYE_TABLE).build("dye-table");
    public static final IRegistryKey<Item> OUTFIT_MAKER = MAIN.block(ModBlocks.OUTFIT_MAKER).build("outfit-maker");
    public static final IRegistryKey<Item> HOLOGRAM_PROJECTOR = MAIN.block(ModBlocks.HOLOGRAM_PROJECTOR).build("hologram-projector");

    public static final IRegistryKey<Item> BOTTLE = MAIN.normal(BottleItem::new).stacksTo(64).build("dye-bottle");
    public static final IRegistryKey<Item> MANNEQUIN_TOOL = MAIN.normal(MannequinToolItem::new).build("mannequin-tool");
    public static final IRegistryKey<Item> ARMOURERS_HAMMER = MAIN.normal(ArmourersHammerItem::new).build("armourers-hammer");
    public static final IRegistryKey<Item> WAND_OF_STYLE = MAIN.normal(WandOfStyleItem::new).build("wand-of-style");

    public static final IRegistryKey<Item> SKIN_UNLOCK_HEAD = MAIN.unlock(SkinSlotType.HEAD).build("skin-unlock-head");
    public static final IRegistryKey<Item> SKIN_UNLOCK_CHEST = MAIN.unlock(SkinSlotType.CHEST).build("skin-unlock-chest");
    public static final IRegistryKey<Item> SKIN_UNLOCK_FEET = MAIN.unlock(SkinSlotType.FEET).build("skin-unlock-feet");
    public static final IRegistryKey<Item> SKIN_UNLOCK_LEGS = MAIN.unlock(SkinSlotType.LEGS).build("skin-unlock-legs");
    public static final IRegistryKey<Item> SKIN_UNLOCK_WINGS = MAIN.unlock(SkinSlotType.WINGS).build("skin-unlock-wings");
    public static final IRegistryKey<Item> SKIN_UNLOCK_OUTFIT = MAIN.unlock(SkinSlotType.OUTFIT).build("skin-unlock-outfit");

    public static final IRegistryKey<Item> LINKING_TOOL = MAIN.normal(LinkingToolItem::new).build("linking-tool");
    public static final IRegistryKey<Item> SKIN_TEMPLATE = MAIN.normal(FlavouredItem::new).stacksTo(64).build("skin-template");
    public static final IRegistryKey<Item> SOAP = MAIN.normal(SoapItem::new).stacksTo(64).build("soap");
    public static final IRegistryKey<Item> GIFT_SACK = MAIN.normal(GiftSackItem::new).stacksTo(64).build("gift-sack");

    public static final IRegistryKey<Item> ARMOURER = BUILDING.block(ModBlocks.ARMOURER).rarity(Rarity.EPIC).build("armourer");
    public static final IRegistryKey<Item> COLOR_MIXER = BUILDING.block(ModBlocks.COLOR_MIXER).build("colour-mixer");
    public static final IRegistryKey<Item> ADVANCED_SKIN_BUILDER = BUILDING.block(ModBlocks.ADVANCED_SKIN_BUILDER).build("advanced-skin-builder");

    public static final IRegistryKey<Item> SKIN_CUBE = BUILDING.cube(ModBlocks.SKIN_CUBE).build("skin-cube");
    public static final IRegistryKey<Item> SKIN_CUBE_GLOWING = BUILDING.cube(ModBlocks.SKIN_CUBE_GLOWING).build("skin-cube-glowing");
    public static final IRegistryKey<Item> SKIN_CUBE_GLASS = BUILDING.cube(ModBlocks.SKIN_CUBE_GLASS).build("skin-cube-glass");
    public static final IRegistryKey<Item> SKIN_CUBE_GLASS_GLOWING = BUILDING.cube(ModBlocks.SKIN_CUBE_GLASS_GLOWING).build("skin-cube-glass-glowing");

    public static final IRegistryKey<Item> PAINT_BRUSH = BUILDING.normal(PaintbrushItem::new).build("paintbrush");
    public static final IRegistryKey<Item> PAINT_ROLLER = BUILDING.normal(PaintRollerItem::new).build("paint-roller");
    public static final IRegistryKey<Item> BURN_TOOL = BUILDING.normal(BurnToolItem::new).build("burn-tool");
    public static final IRegistryKey<Item> DODGE_TOOL = BUILDING.normal(DodgeToolItem::new).build("dodge-tool");
    public static final IRegistryKey<Item> SHADE_NOISE_TOOL = BUILDING.normal(ShadeNoiseToolItem::new).build("shade-noise-tool");
    public static final IRegistryKey<Item> COLOR_NOISE_TOOL = BUILDING.normal(ColorNoiseToolItem::new).build("colour-noise-tool");
    public static final IRegistryKey<Item> BLENDING_TOOL = BUILDING.normal(BlendingToolItem::new).build("blending-tool");
    public static final IRegistryKey<Item> HUE_TOOL = BUILDING.normal(HueToolItem::new).build("hue-tool");
    public static final IRegistryKey<Item> COLOR_PICKER = BUILDING.normal(ColorPickerItem::new).build("colour-picker");
    public static final IRegistryKey<Item> BLOCK_MARKER = BUILDING.normal(BlockMarkerItem::new).build("block-marker");

    public static void init() {
    }

    private static class ItemBuilder {

        IRegistryKey<IItemGroup> group;

        ItemBuilder(IRegistryKey<IItemGroup> group) {
            this.group = group;
        }

        IItemBuilder<Item> normal(Function<Item.Properties, Item> factory) {
            return ObjectUtils.unsafeCast(BuilderManager.getInstance().createItemBuilder(factory).stacksTo(1).group(group));
        }

        IItemBuilder<Item> rare(Function<Item.Properties, Item> factory) {
            return normal(factory).rarity(Rarity.RARE);
        }

        IItemBuilder<Item> block(IRegistryKey<Block> block) {
            return block(BlockItem::new, block).rarity(Rarity.RARE);
        }

        IItemBuilder<Item> block(BiFunction<Block, Item.Properties, Item> provider, IRegistryKey<Block> block) {
            return normal(properties -> provider.apply(block.get(), properties)).stacksTo(64);
        }

        IItemBuilder<Item> cube(IRegistryKey<Block> block) {
            return normal(properties -> new SkinCubeItem(block.get(), properties)).stacksTo(64).bind(() -> SkinCubeItemRenderer::getInstance);
        }

        IItemBuilder<Item> unlock(SkinSlotType slotType) {
            return normal(properties -> new SkinUnlockItem(slotType, properties)).stacksTo(16).rarity(Rarity.UNCOMMON);
        }
    }
}
