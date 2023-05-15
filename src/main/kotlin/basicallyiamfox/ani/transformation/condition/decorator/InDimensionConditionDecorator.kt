package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class InDimensionConditionDecorator() : ConditionDecorator() {
    lateinit var worldId: Identifier

    constructor(world: RegistryKey<World>) : this() {
        id = Identifier("animorphs:in_dimension")
        worldId = world.value
    }

    override fun isActive(playerEntity: ServerPlayerEntity): Boolean {
        return playerEntity.world.registryKey.value.equals(worldId)
    }
}