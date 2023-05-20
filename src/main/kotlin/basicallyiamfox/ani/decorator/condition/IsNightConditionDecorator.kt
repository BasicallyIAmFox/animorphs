package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class IsNightConditionDecorator : ConditionDecorator() {
    object Serializer : ISerializer<IsNightConditionDecorator> {
        override fun toJson(obj: JsonObject, type: IsNightConditionDecorator) {
            obj.addProperty("id", type.id)
        }

        override fun fromJson(obj: JsonObject): IsNightConditionDecorator {
            val inst = IsNightConditionDecorator()
            inst.id = obj.getIdentifier("id")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: IsNightConditionDecorator) {
            buf.writeIdentifier(type.id)
        }

        override fun fromPacket(buf: PacketByteBuf): IsNightConditionDecorator {
            val inst = IsNightConditionDecorator()
            inst.id = buf.readIdentifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:is_night")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.world.isNight
    }
}