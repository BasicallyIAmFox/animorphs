package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import basicallyiamfox.ani.util.ComparisonOperator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class BiomeTemperatureConditionDecorator() : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:biome_temperature")
    }

    lateinit var operator: ComparisonOperator
    var value = 0.0f

    constructor(operator: ComparisonOperator, value: Float) : this() {
        id = ID
        this.value = value
        this.operator = operator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return operator.proceed(world.getBiome(player.blockPos).value().temperature, value)
    }
}