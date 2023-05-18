package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class IsDayConditionDecorator : ConditionDecorator() {
    object Serializer : ISerializer<IsDayConditionDecorator> {
        override fun toJson(obj: JsonObject, type: IsDayConditionDecorator) {
            obj.addProperty("id", type.id)
        }

        override fun fromJson(obj: JsonObject): IsDayConditionDecorator {
            val inst = IsDayConditionDecorator()
            inst.id = obj.getIdentifier("id")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: IsDayConditionDecorator) {
            buf.writeIdentifier(type.id)
        }

        override fun fromPacket(buf: PacketByteBuf): IsDayConditionDecorator {
            val inst = IsDayConditionDecorator()
            inst.id = buf.readIdentifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:is_day")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.world.isDay
    }
}