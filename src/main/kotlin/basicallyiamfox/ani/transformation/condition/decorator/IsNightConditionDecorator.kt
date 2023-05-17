package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class IsNightConditionDecorator : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:is_night")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.world.isNight
    }
}