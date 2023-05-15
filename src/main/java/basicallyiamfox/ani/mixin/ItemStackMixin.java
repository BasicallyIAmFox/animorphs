package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.ExtensionsKt;
import basicallyiamfox.ani.cache.item.TooltipCache;
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
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 0), index = 3)
    private List saveList(List list, @Share("list") LocalRef<List<Text>> listRef) {
        listRef.set(list);
        return list;
    }

    @Inject(method = "getName", at = @At(value = "RETURN"))
    private void animorphs$onGetName(CallbackInfoReturnable<Text> cir) {
        if (ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return;
            }
            ((MutableText)cir.getReturnValue()).setStyle(Style.EMPTY.withColor(color));
        }
    }

    @Inject(method = "toHoverableText", at = @At(value = "RETURN"))
    private void animorphs$toHoverableText(CallbackInfoReturnable<Text> cir) {
        if (ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return;
            }
            ((MutableText)cir.getReturnValue()).setStyle(Style.EMPTY.withColor(color));
        }
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 0))
    private MutableText animorphs$injectTooltip(MutableText orig) {
        if (ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem()))) {
            var color = ExtensionsKt.getTransformationManager().get(getItem()).getColor();
            if (color == null) {
                return orig;
            }
            return orig.setStyle(Style.EMPTY.withColor(color));
        }
        return orig;
    }

    @Unique
    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getHideFlags()I"))
    private void animorphs$getTooltip1(PlayerEntity player,
                                       TooltipContext context,
                                       CallbackInfoReturnable<List<Text>> cir,
                                       @Share("list") LocalRef<List<Text>> listRef,
                                       @Share("transformItem") LocalBooleanRef transformItemRef) {
        transformItemRef.set(player != null && ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem())));
        if (!transformItemRef.get()) {
            return;
        }

        var transformation = ExtensionsKt.getTransformationManager().get(getItem());

        if (transformation.getColor() != null) {
            var list = listRef.get();
            list.set(0, list.get(0).copy().setStyle(Style.EMPTY.withColor(transformation.getColor())));
        }

        if (Screen.hasShiftDown()) {
            var cacheList = TooltipCache.loadForItem(getItem(), transformation);
            for (var line : cacheList) {
                listRef.get().add(line);
            }
        }
        else {
            listRef.get().add(Text.translatable("animorphs.tooltip.animorphs.hold_shift_to_show"));
        }
    }

    @Unique
    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 4))
    private void animorphs$getTooltip2(PlayerEntity player,
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

    @Unique
    @Inject(method = "use", at = @At(value = "HEAD"))
    private void animorphs$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient || !ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem())))
            return;

        user.getStackInHand(hand).getOrCreateNbt().putBoolean(
                TransformationItem.VISUAL_ACTIVE_KEY,
                !user.getStackInHand(hand).getOrCreateNbt().getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)
        );
    }

    @Unique
    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void animorphs$inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!world.isClient || !ExtensionsKt.getTransformationManager().getTypeByItemId().containsKey(Registries.ITEM.getId(getItem())))
            return;

        if (nbt == null || nbt.isEmpty() || !nbt.contains(TransformationItem.VISUAL_ACTIVE_KEY)) {
            getOrCreateNbt().putBoolean(TransformationItem.VISUAL_ACTIVE_KEY, true);
        }
    }
}
