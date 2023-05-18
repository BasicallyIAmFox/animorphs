package basicallyiamfox.ani.core.condition

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

open class ConditionDecorator {
    lateinit var id: Identifier

    open fun isActive(world: World, player: PlayerEntity): Boolean {
        return true
    }
}