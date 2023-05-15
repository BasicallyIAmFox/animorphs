package basicallyiamfox.ani.transformation.condition

import net.minecraft.server.network.ServerPlayerEntity

class Condition {
    var decorator: ConditionDecorator? = null
        private set

    fun isActive(player: ServerPlayerEntity): Boolean {
        if (decorator != null) {
            return decorator!!.isActive(player)
        }
        return true
    }

    fun setDecorator(decorator: ConditionDecorator): Condition {
        this.decorator = decorator
        return this
    }
}