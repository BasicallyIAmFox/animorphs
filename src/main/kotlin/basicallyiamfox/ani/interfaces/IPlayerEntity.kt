package basicallyiamfox.ani.interfaces

import basicallyiamfox.ani.util.StatModifier
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

interface IPlayerEntity {
    val damageTypesImmunities: MutableMap<Identifier, Boolean>
    val damageTypeModifiers: MutableMap<Identifier, StatModifier>

    var transformationItem: ItemStack?
    var activeTransformation: Identifier?
}