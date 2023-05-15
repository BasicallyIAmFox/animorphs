package basicallyiamfox.ani.transformation.condition.decorator

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.asIdentifier
import basicallyiamfox.ani.json.getInt
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.condition.ConditionDecorator
import com.google.gson.JsonObject
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class DuringTimeTicksConditionDecorator() : ConditionDecorator() {
    companion object {
        init {
            JsonSerializer.addSerializer<DuringTimeTicksConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("from", it.from)
                obj.addProperty("to", it.to)
                return@addSerializer obj
            }
            PacketSender.addSender<DuringTimeTicksConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeShort(inst.from)
                buf.writeShort(inst.to)
            }
            JsonSerializer.addDeserializer<DuringTimeTicksConditionDecorator, JsonObject> {
                val inst = DuringTimeTicksConditionDecorator()
                inst.id = it.asIdentifier("id")
                inst.from = it.getInt("from")
                inst.to = it.getInt("to")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = DuringTimeTicksConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.from = buf.readShort().toInt()
                inst.to = buf.readShort().toInt()
                return@addReceiver inst
            }
        }
    }

    private var from: Int = 0
    private var to: Int = 0

    constructor(from: Int, to: Int) : this() {
        id = Identifier("animorphs:during_time_ticks")
        this.from = from
        this.to = to
    }

    override fun isActive(playerEntity: ServerPlayerEntity): Boolean {
        return (playerEntity.world.time % 24000) in from..to
    }
}