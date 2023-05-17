package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import basicallyiamfox.ani.util.ComparisonOperator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class LightLevelConditionDecorator() : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:light_level")
    }

    lateinit var operator: ComparisonOperator
    var value = 0

    constructor(operator: ComparisonOperator, value: Int) : this() {
        id = ID
        this.value = value
        this.operator = operator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return operator.proceed(world.getLightLevel(player.blockPos), value)
    }
}