package basicallyiamfox.ani.transformation.condition

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.json.asIdentifier
import basicallyiamfox.ani.json.getObject
import basicallyiamfox.ani.json.hasJsonObject
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import com.google.gson.JsonObject

class Condition {
    companion object {
        init {
            JsonSerializer.addSerializer<Condition, JsonObject> {
                val obj = JsonObject()

                if (it.decorator != null) {
                    ConditionDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!, obj)
                }

                return@addSerializer obj
            }
            PacketSender.addSender<Condition> { inst, buf ->
                buf.writeBoolean(inst.decorator != null)
                if (inst.decorator != null) {
                    val id = inst.decorator!!.id
                    buf.writeIdentifier(id)
                    RuleDecorators.registry2[id]!!.serializer.applyObj(inst.decorator!!, buf)
                }
            }
            JsonSerializer.addDeserializer<Condition, JsonObject> {
                val inst = Condition()

                if (it.hasJsonObject("decorator")) {
                    val decObj = it.getObject("decorator")
                    val id = decObj.asIdentifier("id")
                    inst.decorator = ConditionDecorators.registry[id]!!.deserializer.apply(id, decObj)
                }

                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Condition()

                if (buf.readBoolean()) {
                    val id = buf.readIdentifier()
                    inst.decorator = ConditionDecorators.registry2[id]!!.deserializer.apply(buf)
                }

                return@addReceiver inst
            }
        }
    }

    var decorator: ConditionDecorator? = null
        private set

    fun setDecorator(decorator: ConditionDecorator): Condition {
        this.decorator = decorator
        return this
    }
}