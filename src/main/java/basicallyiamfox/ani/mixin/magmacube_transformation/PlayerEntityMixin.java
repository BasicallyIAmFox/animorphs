package basicallyiamfox.ani.mixin.magmacube_transformation;

import basicallyiamfox.ani.transformation.rule.decorator.BeeflyRuleDecorator;
import basicallyiamfox.ani.transformation.rule.decorator.MagmaticJumpRuleDecorator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements MagmaticJumpRuleDecorator.MagmaticJumpPlayerEntity {
    @Unique
    private int magmaTick;
    @Unique
    private float magmaDamage;
    @Unique
    private float magmaScale;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Override
    public int getMagmaTick() {
        return magmaTick;
    }
    @Unique
    @Override
    public void setMagmaTick(int magmaTick) {
        this.magmaTick = magmaTick;
    }

    @Unique
    @Override
    public float getMagmaDamage() {
        return magmaDamage;
    }
    @Unique
    @Override
    public void setMagmaDamage(float magmaDamage) {
        this.magmaDamage = magmaDamage;
    }

    @Unique
    @Override
    public float getMagmaScale() {
        return magmaScale;
    }
    @Unique
    @Override
    public void setMagmaScale(float magmaScale) {
        this.magmaScale = magmaScale;
    }
}
