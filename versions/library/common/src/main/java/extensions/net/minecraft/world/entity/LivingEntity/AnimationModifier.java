package extensions.net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.LivingEntity;

@Extension
@Available("[1.16, 1.19.4)")
public class AnimationModifier {

    public static void applyLimitLimbs(@This LivingEntity entity) {
        if (entity.animationSpeed > 0.25F) {
            entity.animationSpeed = 0.25F;
            entity.animationSpeedOld = 0.25F;
        }
    }
}
