package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.interfaces.IPlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable, IPlayerInventory {
    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Unique
    @Nullable
    @Override
    public ItemStack getItem(ItemStack stack) {
        for (DefaultedList<ItemStack> itemStacks : combinedInventory) {
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty() && itemStack.isItemEqual(stack)) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    @Unique
    @Nullable
    @Override
    public DefaultedList<ItemStack> findItems(@NotNull Predicate<ItemStack> predicate) {
        DefaultedList<ItemStack> set = DefaultedList.of();
        for (DefaultedList<ItemStack> itemStacks : combinedInventory) {
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                    set.add(itemStack);
                }
            }
        }
        return set;
    }
}
