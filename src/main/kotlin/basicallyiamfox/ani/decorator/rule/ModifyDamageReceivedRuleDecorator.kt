package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.*
import basicallyiamfox.ani.interfaces.IPlayerEntity
import basicallyiamfox.ani.util.StatModifier
import com.google.gson.JsonObject
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ModifyDamageReceivedRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<ModifyDamageReceivedRuleDecorator> {
        override fun toJson(obj: JsonObject, type: ModifyDamageReceivedRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("damage_type", type.damageTypeId)
            obj.addProperty("value", type.stat)
        }

        override fun fromJson(obj: JsonObject): ModifyDamageReceivedRuleDecorator {
            val inst = ModifyDamageReceivedRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.damageTypeId = obj.getIdentifier("damage_type")
            inst.stat = obj.getStatModifier("value")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: ModifyDamageReceivedRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.damageTypeId)
            buf.writeStatModifier(type.stat)
        }

        override fun fromPacket(buf: PacketByteBuf): ModifyDamageReceivedRuleDecorator {
            val inst = ModifyDamageReceivedRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.damageTypeId = buf.readIdentifier()
            inst.stat = buf.readStatModifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:modify_damage_received")
    }

    lateinit var damageTypeId: Identifier
    lateinit var stat: StatModifier

    constructor(damageType: RegistryKey<DamageType>, stat: StatModifier) : this() {
        id = ID
        damageTypeId = damageType.value
        this.stat = stat
    }

    override fun update(world: World, player: PlayerEntity) {
        player as IPlayerEntity

        if (player.damageTypeModifiers.containsKey(damageTypeId)) {
            player.damageTypeModifiers[damageTypeId] = player.damageTypeModifiers[damageTypeId]!!.combineWith(stat)
        }
        else {
            player.damageTypeModifiers[damageTypeId] = stat
        }
    }
}