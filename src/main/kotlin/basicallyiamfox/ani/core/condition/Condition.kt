package basicallyiamfox.ani.core.condition

import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import basicallyiamfox.ani.extensions.getIdentifier
import basicallyiamfox.ani.extensions.getObject
import basicallyiamfox.ani.extensions.hasJsonObject
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.world.World

class Condition {
    object Serializer : ISerializer<Condition> {
        override fun toJson(obj: JsonObject, type: Condition) {
            if (type.decorator != null) {
                val decObj = JsonObject()
                TypeSerializers.getById<ConditionDecorator>(type.decorator!!.id)!!.toJson(decObj, type.decorator!!)
                obj.add("decorator", decObj)
            }
        }

        override fun fromJson(obj: JsonObject): Condition {
            val inst = Condition()

            if (obj.hasJsonObject("decorator")) {
                val decObj = obj.getObject("decorator")
                val id = decObj.getIdentifier("id")
                inst.setDecorator(TypeSerializers.getById<ConditionDecorator>(id)!!.fromJson(decObj))
            }

            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: Condition) {
            buf.writeBoolean(type.decorator != null)
            if (type.decorator != null) {
                val id = type.decorator!!.id
                buf.writeIdentifier(id)
                TypeSerializers.getById<ConditionDecorator>(type.decorator!!.id)!!.toPacket(buf, type.decorator!!)
            }
        }

        override fun fromPacket(buf: PacketByteBuf): Condition {
            val inst = Condition()

            if (buf.readBoolean()) {
                val id = buf.readIdentifier()
                inst.setDecorator(TypeSerializers.getById<ConditionDecorator>(id)!!.fromPacket(buf))
            }

            return inst
        }
    }

    var decorator: ConditionDecorator? = null
        private set

    fun isActive(world: World, player: PlayerEntity): Boolean {
        if (decorator != null) {
            return decorator!!.isActive(world, player)
        }
        return true
    }

    fun setDecorator(decorator: ConditionDecorator): Condition {
        this.decorator = decorator
        return this
    }
}