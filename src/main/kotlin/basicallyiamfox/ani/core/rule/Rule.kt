package basicallyiamfox.ani.core.rule

import basicallyiamfox.ani.core.condition.Condition
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import basicallyiamfox.ani.extensions.toJsonArray
import basicallyiamfox.ani.json.*
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.world.World

class Rule {
    object Serializer : ISerializer<Rule> {
        override fun toJson(obj: JsonObject, type: Rule) {
            if (type.decorator != null) {
                val decObj = JsonObject()
                TypeSerializers.getById<RuleDecorator>(type.decorator!!.id)!!.toJson(decObj, type.decorator!!)
                obj.add("decorator", decObj)
            }

            if (type.conditions.size > 0) {
                obj.add("conditions", type.conditions.map { e ->
                    val typeObj = JsonObject()
                    Condition.Serializer.toJson(typeObj, e)
                    typeObj
                }.toJsonArray())
            }
        }

        override fun fromJson(obj: JsonObject): Rule {
            val inst = Rule()

            if (obj.hasJsonObject("decorator")) {
                val decObj = obj.getObject("decorator")
                val id = decObj.getIdentifier("id")
                inst.setDecorator(TypeSerializers.getById<RuleDecorator>(id)!!.fromJson(decObj))
            }

            if (obj.hasArray("conditions")) {
                inst.addConditions(obj.getArray("conditions").map { e ->
                    Condition.Serializer.fromJson(e.asJsonObject)
                })
            }

            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: Rule) {
            buf.writeBoolean(type.decorator != null)
            if (type.decorator != null) {
                val id = type.decorator!!.id
                buf.writeIdentifier(id)
                TypeSerializers.getById<RuleDecorator>(type.decorator!!.id)!!.toPacket(buf, type.decorator!!)
            }

            buf.writeByte(type.conditions.size)
            if (type.conditions.size > 0) {
                type.conditions.forEach { e ->
                    Condition.Serializer.toPacket(buf, e)
                }
            }
        }

        override fun fromPacket(buf: PacketByteBuf): Rule {
            val inst = Rule()

            if (buf.readBoolean()) {
                val id = buf.readIdentifier()
                inst.setDecorator(TypeSerializers.getById<RuleDecorator>(id)!!.fromPacket(buf))
            }

            val conditionLength = buf.readByte()
            for (i in 0 until conditionLength) {
                inst.conditions.add(Condition.Serializer.fromPacket(buf))
            }
            return inst
        }
    }

    val conditions = arrayListOf<Condition>()

    var decorator: RuleDecorator? = null
        private set

    fun tick(world: World, player: PlayerEntity) {
        if (conditions.all { condition -> condition.isActive(world, player) }) {
            decorator?.update(world, player)
        }
    }

    fun addConditions(conditions: Iterable<Condition>): Rule {
        this.conditions.addAll(conditions)
        return this
    }

    fun setDecorator(decorator: RuleDecorator): Rule {
        this.decorator = decorator
        return this
    }
}