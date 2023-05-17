package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.ExtensionsKt;
import basicallyiamfox.ani.interfaces.IPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPlayerEntity {
    @Unique
    private List<Identifier> damageTypesImmunities = new ArrayList<>();
    @Unique
    @Nullable
    private ItemStack transformationItem;
    @Unique
    @Nullable
    private Identifier activeTransformation;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void animorphs$clearTransformations(CallbackInfo ci) {
        damageTypesImmunities.clear();
        setActiveTransformation(null);
        setTransformationItem(null);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void animorphs$tickTransformation(CallbackInfo ci) {
        if (getActiveTransformation() == null) return;

        ExtensionsKt.getTransformationManager(this).get(getActiveTransformation()).tick(world, (PlayerEntity) (Object) this);
    }

    @Inject(method = "isInvulnerableTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z"), cancellable = true)
    private void animorphs$isImmuneTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        for (var id : damageTypesImmunities) {
            if (damageSource.getTypeRegistryEntry().matchesId(id)) {
                cir.setReturnValue(true);
                break;
            }
        }
    }

    @NotNull
    @Override
    public List<Identifier> getDamageTypesImmunities() {
        return damageTypesImmunities;
    }

    @Unique
    @Nullable
    @Override
    public ItemStack getTransformationItem() {
        return transformationItem;
    }

    @Unique
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
