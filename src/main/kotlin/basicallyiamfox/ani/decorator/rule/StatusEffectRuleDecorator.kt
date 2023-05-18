package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import basicallyiamfox.ani.json.getInt
import basicallyiamfox.ani.json.hasNumber
import com.google.gson.JsonObject
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.World

class StatusEffectRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<StatusEffectRuleDecorator> {
        override fun toJson(obj: JsonObject, type: StatusEffectRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("effect", type.statusEffectId)

            if (type.amplifier != 0) {
                obj.addProperty("amplifier", type.amplifier)
            }
        }

        override fun fromJson(obj: JsonObject): StatusEffectRuleDecorator {
            val inst = StatusEffectRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.statusEffectId = obj.getIdentifier("effect")

            if (obj.hasNumber("amplifier")) {
                inst.amplifier = obj.getInt("amplifier")
            }

            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: StatusEffectRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.statusEffectId)
            buf.writeByte(type.amplifier)
        }

        override fun fromPacket(buf: PacketByteBuf): StatusEffectRuleDecorator {
            val inst = StatusEffectRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.statusEffectId = buf.readIdentifier()
            inst.amplifier = buf.readByte().toInt()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:status_effect")
    }

    lateinit var statusEffectId: Identifier
    var amplifier: Int = 0

    constructor(statusEffect: StatusEffect) : this() {
        id = ID
        statusEffectId = Registries.STATUS_EFFECT.getId(statusEffect)!!
    }

    fun setAmplifier(amplifier: Int): StatusEffectRuleDecorator {
        this.amplifier = amplifier
        return this
    }

    override fun update(world: World, player: PlayerEntity) {
        if (!world.isClient) {
            player.addStatusEffect(StatusEffectInstance(Registries.STATUS_EFFECT.get(statusEffectId), 20, amplifier, true, false, true))
        }
    }
}
