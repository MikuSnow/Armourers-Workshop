package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@Environment(value = EnvType.CLIENT)
public class HighlightPlacementRenderer {

    public static void renderBlock(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack poseStack, MultiBufferSource buffers) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() != SkinTypes.BLOCK) {
            return;
        }

        poseStack.pushPose();

        float f = 1 / 16.f;
        Vector3f origin = new Vector3f(renderInfo.getPosition());
        SkinBlockPlaceContext context = new SkinBlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, traceResult);
        BlockPos location = context.getClickedPos();

        poseStack.translate(location.getX() - origin.getX(), location.getY() - origin.getY(), location.getZ() - origin.getZ());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(f, f, f);

        for (SkinBlockPlaceContext.Part part : context.getParts()) {
            BlockPos pos = part.getOffset();
            UIColor color = UIColor.RED;
            if (context.canPlace(part)) {
                color = UIColor.WHITE;
            }
            poseStack.pushPose();
            poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
            RenderSystem.drawBoundingBox(poseStack, part.getShape(), color, buffers);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static void renderEntity(Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack poseStack, MultiBufferSource buffers) {
        Vector3f origin = new Vector3f(renderInfo.getPosition());
        MannequinHitResult target = MannequinHitResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        poseStack.pushPose();

        Vector3f location = new Vector3f(target.getLocation());

        poseStack.translate(location.getX() - origin.getX(), location.getY() - origin.getY(), location.getZ() - origin.getZ());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));

        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        if (model != null) {
            float f = target.getScale() * 0.9375f; // base scale from player model
            VertexConsumer builder = buffers.getBuffer(SkinRenderType.HIGHLIGHTED_ENTITY_LINES);
            poseStack.pushPose();
            poseStack.scale(f, f, f);
            poseStack.scale(-1, -1, 1);
            poseStack.translate(0.0f, -1.501f, 0.0f);
            model.renderToBuffer(poseStack, builder, 0xf000f0, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);  // m,vb,l,p,r,g,b,a
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
