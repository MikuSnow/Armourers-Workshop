package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

@Available("[1.18, 1.19.4)")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractItemStackRendererImpl extends BlockEntityWithoutLevelRenderer {

    public AbstractItemStackRendererImpl() {
        this(Minecraft.getInstance());
    }

    public AbstractItemStackRendererImpl(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }

    public void renderByItem(ItemStack itemStack, IItemTransformType transformType, IPoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        super.renderByItem(itemStack, ItemTransforms.ofType(transformType), poseStack.cast(), renderTypeBuffer, light, overlay);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        this.renderByItem(itemStack, ItemTransforms.ofType(transformType), MatrixUtils.of(poseStack), renderTypeBuffer, light, overlay);
    }
}
