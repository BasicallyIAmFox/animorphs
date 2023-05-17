package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class DuringTimeTicksConditionDecorator() : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:during_time_ticks")
    }

    var from: Int = 0
    var to: Int = 24000

    constructor(from: Int, to: Int) : this() {
        id = ID
        this.from = from
        this.to = to
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return (world.timeOfDay % 24000) in from..to
    }
}