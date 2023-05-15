package basicallyiamfox.ani.transformation.rule

import basicallyiamfox.ani.json.*
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.toJsonArray
import basicallyiamfox.ani.transformation.condition.Condition
import com.google.gson.JsonObject

class Rule {
    companion object {
        init {
            JsonSerializer.addSerializer<Rule, JsonObject> {
                val obj = JsonObject()

                if (it.decorator != null) {
                    RuleDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!, obj)
                }

                if (it.conditions.size > 0) {
                    obj.add("conditions", it.conditions.map { e ->
                        JsonSerializer.toJson<Condition, JsonObject>(e)
                    }.toJsonArray())
                }
                return@addSerializer obj
            }
            PacketSender.addSender<Rule> { inst, buf ->
                buf.writeBoolean(inst.decorator != null)
                if (inst.decorator != null) {
                    val id = inst.decorator!!.id
                    buf.writeIdentifier(id)
                    RuleDecorators.registry2[id]!!.serializer.applyObj(inst.decorator!!, buf)
                }

                buf.writeByte(inst.conditions.size)
                if (inst.conditions.size > 0) {
                    inst.conditions.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }
            }
            JsonSerializer.addDeserializer<Rule, JsonObject> {
                val inst = Rule()

                if (it.hasJsonObject("decorator")) {
                    val decObj = it.getObject("decorator")
                    val id = decObj.asIdentifier("id")
                    inst.decorator = RuleDecorators.registry[id]!!.deserializer.apply(id, decObj)
                }

                if (it.hasArray("conditions")) {
                    inst.addConditions(it.getArray("conditions").map { e ->
                        JsonSerializer.fromJson<Condition, JsonObject>(e.asJsonObject)
                    })
                }
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Rule()

                if (buf.readBoolean()) {
                    val id = buf.readIdentifier()
                    inst.decorator = RuleDecorators.registry2[id]!!.deserializer.apply(buf)
                }

                val conditionLength = buf.readByte()
                for (i in 0 until conditionLength) {
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }
    }

    val conditions = arrayListOf<Condition>()

    var decorator: RuleDecorator? = null
        private set

    fun addConditions(conditions: Iterable<Condition>): Rule {
        this.conditions.addAll(conditions)
        return this
    }

    fun setDecorator(decorator: RuleDecorator): Rule {
        this.decorator = decorator
        return this
    }
}