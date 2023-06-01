package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.TransformationManager
import basicallyiamfox.ani.core.ability.AbilityManager
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import kotlin.math.abs

val LivingEntity.moving: Boolean
    get() {
        val absX = abs(velocity.x)
        val absY = abs(velocity.y)
        val absZ = abs(velocity.z)
        return absX + absY + absZ >= getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
    }

val LivingEntity.transformationManager: TransformationManager?
    get() {
        if (world == null)
            return null

        if (world.isClient) {
            return clientTransformationManager
        }
        else if (server != null) {
            return server!!.transformationLoader.manager
        }
        return null
    }
val LivingEntity.abilityManager: AbilityManager?
    get() {
        if (world == null)
            return null

        if (world.isClient) {
            return clientAbilityManager
        }
        else if (server != null) {
            return server!!.abilityLoader.manager
        }
        return null
    }