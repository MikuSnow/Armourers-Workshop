package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.builder.client.render.PaintingHighlightPlacementRenderer;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.core.data.LocalDataService;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.SkinServerType;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import manifold.ext.rt.api.auto;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientProxyImpl {
    private static SimpleChannel channel;
    private static HashMap<UUID, HashMap<String, String>> playerFashions;

    public static void init() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);
        EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);

        // listen the fml events.
        NotificationCenterImpl.observer(FMLClientSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.CLIENT));
        NotificationCenterImpl.observer(FMLLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.CLIENT)));

        // listen the block highlight events.
        Registry.willRenderBlockHighlightFO((traceResult, camera, poseStack, buffers) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            // hidden hit box at inside
            // if (event.getTarget().isInside()) {
            //     BlockState state = player.level.getBlockState(event.getTarget().getBlockPos());
            //     if (state.is(ModBlocks.BOUNDING_BOX)) {
            //         event.setCanceled(true);
            //         return;
            //     }
            // }
            ItemStack itemStack = player.getMainHandItem();
            if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.is(ModItems.MANNEQUIN.get())) {
                HighlightPlacementRenderer.renderEntity(player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.is(ModItems.SKIN.get())) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.is(ModItems.BLENDING_TOOL.get())) {
                PaintingHighlightPlacementRenderer.renderPaintTool(itemStack, player, traceResult, camera, poseStack, buffers);
            }
        });

        Registry.willRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingPre);
        Registry.didRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingPost);

        NotificationCenterImpl.observer(RenderArmEvent.class, event -> {
            if (!ModConfig.enableFirstPersonSkinRenderer()) {
                return;
            }
            int light = event.getPackedLight();
            auto player = Minecraft.getInstance().player;
            auto poseStack = event.getPoseStack();
            auto buffers = event.getMultiBufferSource();
            auto transformType = AbstractItemTransformType.FIRST_PERSON_LEFT_HAND;
            if (event.getArm() == HumanoidArm.RIGHT) {
                transformType = AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND;
            }
            ClientWardrobeHandler.onRenderSpecificHand(player, 0, light, transformType, poseStack, buffers, () -> {
                event.setCanceled(true);
            });
        });

        String PROTOCOL_VERSION = "1";
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("heypixel", "armourers_workshop"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> ClientProxyImpl::setupChannels);
    }

    private static void setupChannels() {
        ModLog.info("正在 初始化 监听频道！！！！！！！！！！！！！");
        LocalDataService.start(EnvironmentManager.getSkinLibraryDirectory());
        SkinLoader.getInstance().prepare(SkinServerType.CLIENT);
        SkinLoader.getInstance().start();
        channel.registerMessage(233, String.class, ClientProxyImpl::enc, ClientProxyImpl::dec, ClientProxyImpl::proc);
        playerFashions = new HashMap<>();
    }

    private static void enc(String str, FriendlyByteBuf buffer) {
        buffer.writeBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    private static String dec(FriendlyByteBuf buffer) {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void proc(String str, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 处理数据包内容
            if (Minecraft.getInstance().player == null) {
                return;
            }
            JsonObject mapInfo = JsonParser.parseString(str).getAsJsonObject();

            if (!mapInfo.has("plugin") || !mapInfo.has("event") || !mapInfo.has("data")) {
                return;
            }

            String plugin = mapInfo.get("plugin").getAsString();

            String eventName = mapInfo.get("event").getAsString();
            JsonObject data = mapInfo.getAsJsonObject("data");

            if ("equipFashionEvent".equals(eventName)) {
                equip(data);
            }
        });
        context.setPacketHandled(true);
    }

    private static void equip(JsonObject data) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer self = mc.player;
        if (level == null || self == null) {
            return;
        }

        System.out.println("equip #1 -> ");
        String uuid = data.get("uuid").getAsString();
        AbstractClientPlayer player = null;

        UUID targetUUID = UUID.fromString(uuid);
        if (self.getUUID().equals(targetUUID)) {
            player = self;
        } else {
            for (AbstractClientPlayer p : level.players()) {
                if (p.getUUID().equals(targetUUID)) {
                    player = p;
                    break;
                }
            }
        }
        String path = data.get("fashion").getAsString();
        String type = data.get("group").getAsString();
        if (playerFashions == null) {
            playerFashions = new HashMap<>();
        }
        HashMap<String, String> playerInfo = playerFashions.getOrDefault(targetUUID, new HashMap<>());
        playerInfo.put(type, path);
        playerFashions.put(targetUUID, playerInfo);

        if (player == null) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null) {
            ModLog.info("衣橱为空！");
            return;
        }
        wardrobe.clear();
        for (Map.Entry<String, String> fs : playerInfo.entrySet()) {
            String k = fs.getKey();
            String v = fs.getValue();

            // data -> {"equip":true,"uuid":"c66e6f3c-8be2-3dfe-9cb5-d758be4d0ee5","fashion":"suit/fox.armour"}
            SkinSlotType st = "suit".equals(k) ? SkinSlotType.OUTFIT : "suitaddon".equals(k) ? SkinSlotType.WINGS : null;
            if (st == null) {
                return;
            }

            SkinDescriptor descriptor = loadSkinFromDB("rp:skins/" + v);
            ItemStack itemStack = descriptor.asItemStack();
            wardrobe.setItem(st, 0, itemStack);
        }

    }



    public static SkinDescriptor loadSkinFromDB(String identifier) {
        Skin skin = LocalDataService.localSkins.get(identifier);
        if (skin != null) {
            return new SkinDescriptor(identifier, skin.getType(), ColorScheme.EMPTY);
        }
        return SkinDescriptor.EMPTY;
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof Player player) {
            UUID uuid = player.getUUID();
            HashMap<String, String> playerInfo = playerFashions.get(uuid);
            if (playerInfo == null) {
                return;
            }
            SkinWardrobe wardrobe = SkinWardrobe.of(player);
            if (wardrobe == null) {
                ModLog.info("衣橱为空！");
                return;
            }
            wardrobe.clear();
            for (Map.Entry<String, String> fs : playerInfo.entrySet()) {
                String k = fs.getKey();
                String v = fs.getKey();

                SkinSlotType st = "suit".equals(k) ? SkinSlotType.OUTFIT : "suitaddon".equals(k) ? SkinSlotType.WINGS : null;
                if (st == null) {
                    return;
                }

                SkinDescriptor descriptor = loadSkinFromDB("rp:skins/" + v);
                ItemStack itemStack = descriptor.asItemStack();
                wardrobe.setItem(st, 0, itemStack);
            }
        }
    }
}
