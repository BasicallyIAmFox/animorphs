package basicallyiamfox.ani.interfaces

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

interface IPlayerEntity {
    fun getTransformationItem(): ItemStack?
    fun setTransformationItem(itemStack: ItemStack?)

    fun getActiveTransformation(): Identifier?
    fun setActiveTransformation(id: Identifier?)
}