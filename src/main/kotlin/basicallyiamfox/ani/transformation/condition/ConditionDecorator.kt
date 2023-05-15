package basicallyiamfox.ani.transformation.condition

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class ConditionDecorator {
    lateinit var id: Identifier

    open fun isActive(playerEntity: ServerPlayerEntity): Boolean {
        return true
    }
}