package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getArray
import basicallyiamfox.ani.extensions.getIdentifier
import basicallyiamfox.ani.extensions.toJsonArray
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class NotConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<NotConditionDecorator> {
        override fun toJson(obj: JsonObject, type: NotConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.add("elements", type.decorators.map { e ->
                val typeObj = JsonObject()
                TypeSerializers.getById<ConditionDecorator>(e.id)!!.toJson(typeObj, e)
                typeObj
            }.toJsonArray())
        }

        override fun fromJson(obj: JsonObject): NotConditionDecorator {
            val inst = NotConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.decorators = obj.getArray("elements").map { e ->
                val id = e.asJsonObject.getIdentifier("id")
                TypeSerializers.getById<ConditionDecorator>(id)!!.fromJson(e.asJsonObject)
            }.toTypedArray()
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: NotConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeByte(type.decorators.size)
            type.decorators.forEach { e ->
                buf.writeIdentifier(e.id)
                TypeSerializers.getById<ConditionDecorator>(e.id)!!.toPacket(buf, e)
            }
        }

        override fun fromPacket(buf: PacketByteBuf): NotConditionDecorator {
            val inst = NotConditionDecorator()
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
        val ID = Identifier("animorphs:not")
    }

    var decorators: Array<out ConditionDecorator> = arrayOf()

    constructor(vararg decorator: ConditionDecorator) : this() {
        id = ID
        decorators = decorator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return !decorators.all { it.isActive(world, player) }
    }
}