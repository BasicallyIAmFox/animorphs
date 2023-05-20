package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

class InDimensionConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<InDimensionConditionDecorator> {
        override fun toJson(obj: JsonObject, type: InDimensionConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("world", type.worldId)
        }

        override fun fromJson(obj: JsonObject): InDimensionConditionDecorator {
            val inst = InDimensionConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.worldId = obj.getIdentifier("world")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: InDimensionConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.worldId)
        }

        override fun fromPacket(buf: PacketByteBuf): InDimensionConditionDecorator {
            val inst = InDimensionConditionDecorator()
            inst.id = buf.readIdentifier()
            inst.worldId = buf.readIdentifier()
            return inst
        }
    }
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