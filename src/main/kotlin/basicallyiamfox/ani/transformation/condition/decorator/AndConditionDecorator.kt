package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class AndConditionDecorator() : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:and")
    }

    var decorators: Array<out ConditionDecorator> = arrayOf()

    constructor(vararg decorator: ConditionDecorator) : this() {
        id = ID
        decorators = decorator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return decorators.all { it.isActive(world, player) }
    }
}