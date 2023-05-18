package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import basicallyiamfox.ani.mixin.IEntityMixin
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class BeingRainedOnConditionDecorator : ConditionDecorator() {
    object Serializer : ISerializer<BeingRainedOnConditionDecorator> {
        override fun toJson(obj: JsonObject, type: BeingRainedOnConditionDecorator) {
            obj.addProperty("id", type.id)
        }

        override fun fromJson(obj: JsonObject): BeingRainedOnConditionDecorator {
            val inst = BeingRainedOnConditionDecorator()
            inst.id = obj.getIdentifier("id")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: BeingRainedOnConditionDecorator) {
            buf.writeIdentifier(type.id)
        }

        override fun fromPacket(buf: PacketByteBuf): BeingRainedOnConditionDecorator {
            val inst = BeingRainedOnConditionDecorator()
            inst.id = buf.readIdentifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:being_rained_on")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return (player as IEntityMixin).invokeIsBeingRainedOn()
    }
}