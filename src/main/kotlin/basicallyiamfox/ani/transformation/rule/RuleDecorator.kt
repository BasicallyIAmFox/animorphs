package basicallyiamfox.ani.transformation.rule

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

open class RuleDecorator {
    lateinit var id: Identifier

    open fun update(world: World, player: PlayerEntity) {
    }
}