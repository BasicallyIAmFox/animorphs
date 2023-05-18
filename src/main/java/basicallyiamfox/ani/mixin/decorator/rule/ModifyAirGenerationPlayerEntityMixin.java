package basicallyiamfox.ani.mixin.decorator.rule;

import basicallyiamfox.ani.decorator.rule.ModifyAirGenerationRuleDecorator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ModifyAirGenerationPlayerEntityMixin extends LivingEntity implements ModifyAirGenerationRuleDecorator.AirGenerationPlayerEntity {
    @Unique
    private int airGeneration;

    protected ModifyAirGenerationPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V"))
    private void animorphs$resetAirGen(CallbackInfo ci) {
        airGeneration = 0;
    }

    @Unique
    @Override
    public int getAirGeneration() {
        return airGeneration;
    }
    @Unique
    @Override
    public void setAirGeneration(int i) {
        airGeneration = i;
    }
}
