package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

class InDimensionConditionDecorator() : ConditionDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:in_dimension")
    }

    lateinit var worldId: Identifier

    constructor(world: RegistryKey<World>) : this() {
        id = ID
        worldId = world.value
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return world.registryKey.value.equals(worldId)
    }
}