package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class IsSkyVisibleConditionDecorator : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:is_sky_visible")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.world.isSkyVisible(player.blockPos)
    }
}