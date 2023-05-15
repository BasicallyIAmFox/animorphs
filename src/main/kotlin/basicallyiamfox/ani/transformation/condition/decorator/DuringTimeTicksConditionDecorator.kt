package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class DuringTimeTicksConditionDecorator() : ConditionDecorator() {
    var from: Int = 0
    var to: Int = 0

    constructor(from: Int, to: Int) : this() {
        id = Identifier("animorphs:during_time_ticks")
        this.from = from
        this.to = to
    }

    override fun isActive(playerEntity: ServerPlayerEntity): Boolean {
        return (playerEntity.world.time % 24000) in from..to
    }
}