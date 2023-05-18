package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.TransformationManager
import basicallyiamfox.ani.core.ability.AbilityManager
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import kotlin.math.abs

fun LivingEntity.isMoving(): Boolean {
    val absX = abs(velocity.x)
    val absY = abs(velocity.y)
    val absZ = abs(velocity.z)
    return absX + absY + absZ >= getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
}

fun LivingEntity.getTransformationManager(): TransformationManager? {
    if (world == null)
        return null

    if (world.isClient) {
        return getClientTransformationManager()
    }
    else if (server != null) {
        return server!!.getTransformationLoader().manager
    }
    return null
}
fun LivingEntity.getAbilityManager(): AbilityManager? {
    if (world == null)
        return null

    if (world.isClient) {
        return getClientAbilityManager()
    }
    else if (server != null) {
        return server!!.getAbilityLoader().manager
    }
    return null
}