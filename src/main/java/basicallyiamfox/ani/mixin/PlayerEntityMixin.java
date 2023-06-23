package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.extensions._LivingEntityKt;
import basicallyiamfox.ani.interfaces.IPlayerEntity;
import basicallyiamfox.ani.util.StatModifier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPlayerEntity {
    @Unique
    private Map<Identifier, Boolean> damageTypesImmunities = new HashMap<>();
    @Unique
    private Map<Identifier, StatModifier> damageTypeModifiers = new HashMap<>();
    @Unique
    @Nullable
    private ItemStack transformationItem;
    @Unique
    @Nullable
    private Identifier activeTransformation;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V"))
    private void animorphs$clearTransformations(CallbackInfo ci) {
        damageTypesImmunities.clear();
        damageTypeModifiers.clear();
        setActiveTransformation(null);
        setTransformationItem(null);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V", shift = At.Shift.AFTER))
    private void animorphs$tickTransformation(CallbackInfo ci) {
        if (getActiveTransformation() == null) return;

        _LivingEntityKt.getTransformationManager(this).get(getActiveTransformation()).tick(getWorld(), (PlayerEntity) (Object) this);
    }

    @Inject(method = "isInvulnerableTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z"), cancellable = true)
    private void animorphs$isImmuneTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (damageTypesImmunities.containsKey(getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getId(damageSource.getType()))) {
            cir.setReturnValue(true);
        }
    }

    private float animorphs$damageValue(float amount, DamageSource source) {
        var id = getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getId(source.getType());
        if (damageTypeModifiers.containsKey(id)) {
            return damageTypeModifiers.get(id).applyTo(amount);
        }
        return amount;
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float animorphs$modifyDamageValue(float amount, DamageSource source) {
        return animorphs$damageValue(amount, source);
    }
    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float animorphs$modifyAppliedDamageValue(float amount, DamageSource source) {
        return animorphs$damageValue(amount, source);
    }

    @NotNull
    @Override
    public Map<Identifier, Boolean> getDamageTypesImmunities() {
        return damageTypesImmunities;
    }

    @NotNull
    @Override
    public Map<Identifier, StatModifier> getDamageTypeModifiers() {
        return damageTypeModifiers;
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
