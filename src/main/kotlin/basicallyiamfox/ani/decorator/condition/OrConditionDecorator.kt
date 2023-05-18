package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import basicallyiamfox.ani.extensions.toJsonArray
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getArray
import basicallyiamfox.ani.json.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class OrConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<OrConditionDecorator> {
        override fun toJson(obj: JsonObject, type: OrConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.add("elements", type.decorators.map { e ->
                val typeObj = JsonObject()
                TypeSerializers.getById<ConditionDecorator>(e.id)!!.toJson(typeObj, e)
                typeObj
            }.toJsonArray())
        }

        override fun fromJson(obj: JsonObject): OrConditionDecorator {
            val inst = OrConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.decorators = obj.getArray("elements").map { e ->
                val id = e.asJsonObject.getIdentifier("id")
                TypeSerializers.getById<ConditionDecorator>(id)!!.fromJson(e.asJsonObject)
            }.toTypedArray()
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: OrConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeByte(type.decorators.size)
            type.decorators.forEach { e ->
                buf.writeIdentifier(e.id)
                TypeSerializers.getById<ConditionDecorator>(e.id)!!.toPacket(buf, e)
            }
        }

        override fun fromPacket(buf: PacketByteBuf): OrConditionDecorator {
            val inst = OrConditionDecorator()
            inst.id = buf.readIdentifier()

            val eLen = buf.readByte().toInt()
            inst.decorators = Array(eLen) {
                val id = buf.readIdentifier()
                return@Array TypeSerializers.getById<ConditionDecorator>(id)!!.fromPacket(buf)
            }

            return inst
        }
    }

    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:or")
    }

    var decorators: Array<out ConditionDecorator> = arrayOf()

    constructor(vararg decorator: ConditionDecorator) : this() {
        id = ID
        decorators = decorator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return decorators.any { it.isActive(world, player) }
    }
}