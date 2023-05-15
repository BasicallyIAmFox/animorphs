package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.interfaces.IPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPlayerEntity {
    @Unique
    @Nullable
    private ItemStack transformationItem;
    @Unique
    @Nullable
    private Identifier activeTransformation;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Inject(method = "tick", at = @At("RETURN"))
    private void animorphs$tick(CallbackInfo ci) {
    }

    @Unique
    @Nullable
    @Override
    public ItemStack getTransformationItem() {
        return transformationItem;
    }

    @Unique
    @Nullable
    @Override
    public void setTransformationItem(@Nullable ItemStack transformationItem) {
        this.transformationItem = transformationItem;
    }

    @Unique
    @Nullable
    @Override
    public Identifier getActiveTransformation() {
        return activeTransformation;
    }

    @Unique
    @Override
    public void setActiveTransformation(@Nullable Identifier activeTransformation) {
        this.activeTransformation = activeTransformation;
    }
}
