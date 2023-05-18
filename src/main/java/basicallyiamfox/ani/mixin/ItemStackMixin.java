package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.cache.item.TooltipCache;
import basicallyiamfox.ani.extensions.ExtensionsKt;
import basicallyiamfox.ani.extensions._LivingEntityKt;
import basicallyiamfox.ani.interfaces.IPlayerEntity;
import basicallyiamfox.ani.item.TransformationItem;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow private @Nullable NbtCompound nbt;

    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Inject(method = "getName", at = @At(value = "RETURN"))
    private void animorphs$changeNameColor(CallbackInfoReturnable<Text> cir) {
        if (ExtensionsKt.getClientTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getClientTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return;
            }
            ((MutableText)cir.getReturnValue()).setStyle(Style.EMPTY.withColor(color));
        }
    }

    @Inject(method = "toHoverableText", at = @At(value = "RETURN"))
    private void animorphs$changeHoverNameColor(CallbackInfoReturnable<Text> cir) {
        if (ExtensionsKt.getClientTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getClientTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return;
            }
            ((MutableText)cir.getReturnValue()).setStyle(Style.EMPTY.withColor(color));
        }
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 0))
    private MutableText animorphs$changeNameColor2(MutableText orig) {
        if (ExtensionsKt.getClientTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getClientTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return orig;
            }
            return orig.setStyle(Style.EMPTY.withColor(color));
        }
        return orig;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 0), index = 3)
    private List animorphs$saveListRef(List list,
                                       @Share("list") LocalRef<List<Text>> listRef) {
        listRef.set(list);
        return list;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getHideFlags()I"))
    private void animorphs$addDescriptionAndAbilitiesList(PlayerEntity player,
                                       TooltipContext context,
                                       CallbackInfoReturnable<List<Text>> cir,
                                       @Share("list") LocalRef<List<Text>> listRef,
                                       @Share("transformItem") LocalBooleanRef transformItemRef) {
        transformItemRef.set(player != null && ExtensionsKt.getClientTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem())));
        if (!transformItemRef.get()) {
            return;
        }

        var transformation = ExtensionsKt.getClientTransformationManager().get(getItem());

        var descList = TooltipCache.loadDescForItem(getItem(), transformation);
        if (descList.size() > 0) {
            listRef.get().addAll(descList);
            listRef.get().add(Text.empty());
        }

        if (Screen.hasShiftDown()) {
            var cacheList = TooltipCache.loadForItem(getItem(), transformation);
            listRef.get().addAll(cacheList);
        }
        else {
            listRef.get().add(Text.translatable("animorphs.tooltip.animorphs.hold_shift_to_show"));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 4))
    private void animorphs$addVisualActiveTooltip(PlayerEntity player,
                                       TooltipContext context,
                                       CallbackInfoReturnable<List<Text>> cir,
                                       @Share("list") LocalRef<List<Text>> listRef,
                                       @Share("transformItem") LocalBooleanRef transformItemRef) {
        if (!transformItemRef.get())
            return;

        listRef.get().add(Text.empty());

        var text = TooltipCache.isVisualActiveOn();
        if (nbt != null && !nbt.getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)) {
            text = TooltipCache.isVisualActiveOff();
        }
        listRef.get().add(text);
    }

    @Inject(method = "use", at = @At(value = "HEAD"))
    private void animorphs$switchVisualActiveKey(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient || !_LivingEntityKt.getTransformationManager(user).getTypeByItemId().containsKey(Registries.ITEM.getId(getItem())))
            return;

        user.getStackInHand(hand).getOrCreateNbt().putBoolean(
                TransformationItem.VISUAL_ACTIVE_KEY,
                !user.getStackInHand(hand).getOrCreateNbt().getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)
        );
    }

    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void animorphs$addVisualActiveKeyAndSetActiveTransformation(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            var manager = _LivingEntityKt.getTransformationManager((PlayerEntity)entity);
            if (manager == null) return;

            var trans = manager.get(getItem());
            if (trans == null) return;

            if (nbt == null || nbt.isEmpty() || !nbt.contains(TransformationItem.VISUAL_ACTIVE_KEY)) {
                getOrCreateNbt().putBoolean(TransformationItem.VISUAL_ACTIVE_KEY, true);
            }

            var duck = (IPlayerEntity)entity;
            duck.setActiveTransformation(trans.getId());
            duck.setTransformationItem((ItemStack)(Object)this);
        }
    }
}
