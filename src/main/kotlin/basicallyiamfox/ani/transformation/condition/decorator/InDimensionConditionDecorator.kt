package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.asIdentifier
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import com.google.gson.JsonObject
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World

class InDimensionConditionDecorator() : ConditionDecorator() {
    companion object {
        init {
            JsonSerializer.addSerializer<InDimensionConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("world", it.worldId)
                return@addSerializer obj
            }
            PacketSender.addSender<InDimensionConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.worldId)
            }
            JsonSerializer.addDeserializer<InDimensionConditionDecorator, JsonObject> {
                val inst = InDimensionConditionDecorator()
                inst.id = it.asIdentifier("id")
                inst.worldId = it.asIdentifier("world")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = InDimensionConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.worldId = buf.readIdentifier()
                return@addReceiver inst
            }
        }
    }

    private lateinit var worldId: Identifier

    constructor(world: RegistryKey<World>) : this() {
        id = Identifier("animorphs:in_dimension")
        worldId = world.value
    }

    override fun isActive(playerEntity: ServerPlayerEntity): Boolean {
        return playerEntity.world.registryKey.value.equals(worldId)
    }
}