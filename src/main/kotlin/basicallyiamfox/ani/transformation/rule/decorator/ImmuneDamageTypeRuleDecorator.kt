package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.asIdentifier
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.rule.RuleDecorator
import com.google.gson.JsonObject
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

class ImmuneDamageTypeRuleDecorator() : RuleDecorator() {
    companion object {
        init {
            JsonSerializer.addSerializer<ImmuneDamageTypeRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("damageType", it.damageTypeId)
                return@addSerializer obj
            }
            PacketSender.addSender<ImmuneDamageTypeRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.damageTypeId)
            }
            JsonSerializer.addDeserializer<ImmuneDamageTypeRuleDecorator, JsonObject> {
                val inst = ImmuneDamageTypeRuleDecorator()
                inst.id = it.asIdentifier("id")
                inst.damageTypeId = it.asIdentifier("damageType")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = ImmuneDamageTypeRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.damageTypeId = buf.readIdentifier()
                return@addReceiver inst
            }
        }
    }

    private lateinit var damageTypeId: Identifier

    constructor(damageType: RegistryKey<DamageType>) : this() {
        id = Identifier("animorphs:immune_damage_type")
        damageTypeId = damageType.value
    }
}