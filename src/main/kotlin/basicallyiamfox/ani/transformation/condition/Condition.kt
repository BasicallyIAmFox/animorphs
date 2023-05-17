package basicallyiamfox.ani.transformation.condition

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World

class Condition {
    var decorator: ConditionDecorator? = null
        private set

    fun isActive(world: World, player: PlayerEntity): Boolean {
        if (decorator != null) {
            return decorator!!.isActive(world, player)
        }
        return true
    }

    fun setDecorator(decorator: ConditionDecorator): Condition {
        this.decorator = decorator
        return this
    }
}