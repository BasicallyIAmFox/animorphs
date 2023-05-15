/*package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.ExtensionsKt;
import basicallyiamfox.ani.TooltipManager;
import basicallyiamfox.ani.item.TransformationItem;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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

import java.awt.*;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow private @Nullable NbtCompound nbt;

    @Shadow public abstract boolean isDamageable();

    @Shadow public abstract NbtCompound getOrCreateNbt();

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 0), index = 3)
    private List saveList(List list, @Share("list") LocalRef<List<Text>> listRef) {
        listRef.set(list);
        return list;
    }

    @Unique
    private boolean isTransformItem(Entity player) {
        return player != null && player.getServer() != null && ExtensionsKt.getTransformationLoader(player.getServer()).getByItemStack((ItemStack)(Object)this) != null;
    }

    @Unique
    private Identifier[] getEffects(Entity player) {
        return ExtensionsKt.getTransformationLoader(player.getServer()).getByItemStack((ItemStack)(Object)this).getAbilities();
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getHideFlags()I"))
    private void animorphs$getTooltip1(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Share("list") LocalRef<List<Text>> listRef) {
        if (!isTransformItem(player))
            return;

        if (Screen.hasShiftDown()) {
            for (var e : getEffects(player)) {
                MutableText text = Text.literal("* ").setStyle(Style.EMPTY.withColor(Color.WHITE.getRGB()));
                var ability = ExtensionsKt.getAbilityLoader(player.world.getServer()).get(e);
                if (ability == null) {
                    listRef.get().add(text.append(Text.translatable(e.toString())));
                    continue;
                }

                var sign = ability.getSign();
                if (sign == AbilitySign.POSITIVE) {
                    text = text.append(Text.literal("[+] ").setStyle(Style.EMPTY.withColor(new Color(15, 190, 25).getRGB())));
                }
                else if (sign == AbilitySign.NEGATIVE) {
                    text = text.append(Text.literal("[-] ").setStyle(Style.EMPTY.withColor(new Color(190, 15, 25).getRGB())));
                }
                else {
                    text = text.append(Text.literal("[~] ").setStyle(Style.EMPTY.withColor(new Color(190, 190, 40).getRGB())));
                }

                text = text.append(Text.translatable(ability.getNameKey()).setStyle(Style.EMPTY.withColor(ability.getNameColor().getRGB())));
                listRef.get().add(text);

                if (ability.getDescKey() == null) {
                    continue;
                }

                String tsd = I18n.translate(ability.getDescKey());
                if (tsd.contains("\n")) {
                    var spl = tsd.split("\n");
                    for (var s : spl) {
                        MutableText text2 = Text.literal("    ");
                        text2 = text2.append(Text.literal(s));
                        listRef.get().add(text2);
                    }
                }
                else {
                    MutableText text2 = Text.literal("    ");
                    text2 = text2.append(Text.translatable(ability.getDescKey()));
                    listRef.get().add(text2);
                }
            }
        }
        else {
            listRef.get().add(Text.translatable("tooltip.animorphs.shift_to_show"));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 4))
    private void animorphs$getTooltip2(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Share("list") LocalRef<List<Text>> listRef) {
        if (!isTransformItem(player))
            return;

        listRef.get().add(Text.empty());

        var text = TooltipManager.getIS_VISUAL_ACTIVE_ON();
        if (nbt != null && !nbt.getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)) {
            text = TooltipManager.getIS_VISUAL_ACTIVE_OFF();
        }
        listRef.get().add(text);
    }

    @Inject(method = "use", at = @At(value = "HEAD"))
    private void animorphs$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!isTransformItem(user))
            return;

        user.getStackInHand(hand).getOrCreateNbt().putBoolean(
                TransformationItem.VISUAL_ACTIVE_KEY,
                !user.getStackInHand(hand).getOrCreateNbt().getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)
        );
    }

    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void animorphs$inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!isTransformItem(entity))
            return;

        if (nbt == null || nbt.isEmpty() || !nbt.contains(TransformationItem.VISUAL_ACTIVE_KEY)) {
            getOrCreateNbt().putBoolean(TransformationItem.VISUAL_ACTIVE_KEY, true);
        }
    }
}*/