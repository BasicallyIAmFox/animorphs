package basicallyiamfox.ani.mixin.decorator.rule;

import basicallyiamfox.ani.decorator.rule.MagmaticJumpRuleDecorator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class MagmaticJumpPlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MagmaticJumpPlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @ModifyConstant(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
            constant = @Constant(floatValue = 0.9375F, ordinal = 2))
    private float animorphs$magmaScale(float constant, AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (((MagmaticJumpRuleDecorator.MagmaticJumpPlayerEntity)abstractClientPlayerEntity).getMagmaTick() > 0) {
            return constant - ((MagmaticJumpRuleDecorator.MagmaticJumpPlayerEntity)abstractClientPlayerEntity).getMagmaScale() * 0.5F;
        }
        return constant;
    }
}
