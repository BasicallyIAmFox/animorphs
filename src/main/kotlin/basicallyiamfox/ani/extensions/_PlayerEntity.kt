package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.interfaces.IPlayerEntity
import basicallyiamfox.ani.item.TransformationItem
import net.minecraft.entity.player.PlayerEntity

fun PlayerEntity.isVisualActive(): Boolean {
    val duck = this as IPlayerEntity
    val item = duck.transformationItem ?: return false
    return item.orCreateNbt.getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)
}