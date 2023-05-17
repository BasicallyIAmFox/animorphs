package basicallyiamfox.ani.interfaces

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

interface IPlayerEntity {
    val damageTypesImmunities: MutableList<Identifier>

    var transformationItem: ItemStack?
    var activeTransformation: Identifier?
}