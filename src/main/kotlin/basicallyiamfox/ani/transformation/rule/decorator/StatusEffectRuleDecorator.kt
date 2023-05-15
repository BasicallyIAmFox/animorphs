package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.asIdentifier
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.rule.RuleDecorator
import com.google.gson.JsonObject
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class StatusEffectRuleDecorator() : RuleDecorator() {
    companion object {
        init {
            JsonSerializer.addSerializer<StatusEffectRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("effect", it.statusEffectId)
                return@addSerializer obj
            }
            PacketSender.addSender<StatusEffectRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.statusEffectId)
            }
            JsonSerializer.addDeserializer<StatusEffectRuleDecorator, JsonObject> {
                val inst = StatusEffectRuleDecorator()
                inst.id = it.asIdentifier("id")
                inst.statusEffectId = it.asIdentifier("effect")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = StatusEffectRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.statusEffectId = buf.readIdentifier()
                return@addReceiver inst
            }
        }
    }

    private lateinit var statusEffectId: Identifier

    constructor(statusEffect: StatusEffect) : this() {
        id = Identifier("animorphs:status_effect")
        statusEffectId = Registries.STATUS_EFFECT.getId(statusEffect)!!
    }
}