package basicallyiamfox.ani.mixin.decorator.rule;

import basicallyiamfox.ani.decorator.rule.ModifyAirGenerationRuleDecorator;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class ModifyAirGenerationLivingEntityMixin extends Entity {
    public ModifyAirGenerationLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "getNextAirUnderwater", at = @At(value = "RETURN", ordinal = 1))
    private int animorphs$getNextAirUnderwater(int air) {
        if (this instanceof ModifyAirGenerationRuleDecorator.AirGenerationPlayerEntity duck) {
            return Math.max(air + duck.getAirGeneration(), -20);
        }
        return air;
    }
}
