package basicallyiamfox.ani.interfaces

import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import java.util.function.Predicate


interface IPlayerInventory {
    fun getItem(stack: ItemStack?): ItemStack?

    fun findItems(predicate: Predicate<ItemStack?>): DefaultedList<ItemStack?>?
}