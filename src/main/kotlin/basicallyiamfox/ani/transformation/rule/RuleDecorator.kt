package basicallyiamfox.ani.transformation.rule

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class RuleDecorator {
    lateinit var id: Identifier
        protected set

    fun update(playerEntity: ServerPlayerEntity) {
    }
}