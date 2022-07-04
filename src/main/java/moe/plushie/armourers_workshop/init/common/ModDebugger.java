package moe.plushie.armourers_workshop.init.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModDebugger {

    public static int rx = 0;
    public static int ry = 0;
    public static int rz = 0;
    public static int tx = 0;
    public static int ty = 0;
    public static int tz = 0;
    public static int sx = 1;
    public static int sy = 1;
    public static int sz = 1;

    public static boolean skinnableBlock = false;
    public static boolean hologramProjectorBlock = false;

    public static boolean skinBounds = false;
    public static boolean skinOrigin = false;

    public static boolean skinPartBounds = false;
    public static boolean skinPartOrigin = false;

    public static boolean targetBounds = false;

    public static boolean boundingBox = false;

    public static boolean mannequinCulling = false;
    public static boolean itemOverride = false;

    public static boolean textureBounds = false;
    public static boolean spin = false;

    public static boolean tooltip = false;

    // Debug tool
    public static boolean armourerDebugRender;
    public static boolean lodLevels;
    public static boolean skinBlockBounds;
    public static boolean skinRenderBounds;
    public static boolean sortOrderToolTip;

    @OnlyIn(Dist.CLIENT)
    public static void rotate(MatrixStack matrixStack) {
        matrixStack.mulPose(new Quaternion(rx, ry, rz, true));
    }

    @OnlyIn(Dist.CLIENT)
    public static void scale(MatrixStack matrixStack) {
        matrixStack.scale(sx, sy, sz);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translate(MatrixStack matrixStack) {
        matrixStack.translate(tx, ty, tz);
    }
}
