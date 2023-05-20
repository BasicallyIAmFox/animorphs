package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getIdentifier
import basicallyiamfox.ani.extensions.getInt
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class DuringTimeTicksConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<DuringTimeTicksConditionDecorator> {
        override fun toJson(obj: JsonObject, type: DuringTimeTicksConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("from", type.from)
            obj.addProperty("to", type.to)
        }

        override fun fromJson(obj: JsonObject): DuringTimeTicksConditionDecorator {
            val inst = DuringTimeTicksConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.from = obj.getInt("from")
            inst.to = obj.getInt("to")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: DuringTimeTicksConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeShort(type.from)
            buf.writeShort(type.to)
        }

        override fun fromPacket(buf: PacketByteBuf): DuringTimeTicksConditionDecorator {
            val inst = DuringTimeTicksConditionDecorator()
            inst.id = buf.readIdentifier()
            inst.from = buf.readShort().toInt()
            inst.to = buf.readShort().toInt()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:during_time_ticks")
    }

    var from: Int = 0
    var to: Int = 24000

    constructor(from: Int, to: Int) : this() {
        id = ID
        this.from = from
        this.to = to
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return (world.timeOfDay % 24000) in from..to
    }
}