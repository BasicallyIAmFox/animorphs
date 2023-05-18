package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.interfaces.IPlayerEntity
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ImmuneDamageTypeRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<ImmuneDamageTypeRuleDecorator> {
        override fun toJson(obj: JsonObject, type: ImmuneDamageTypeRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("type", type.damageTypeId)
        }

        override fun fromJson(obj: JsonObject): ImmuneDamageTypeRuleDecorator {
            val inst = ImmuneDamageTypeRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.damageTypeId = obj.getIdentifier("type")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: ImmuneDamageTypeRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.damageTypeId)
        }

        override fun fromPacket(buf: PacketByteBuf): ImmuneDamageTypeRuleDecorator {
            val inst = ImmuneDamageTypeRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.damageTypeId = buf.readIdentifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:immune_damage_type")
    }

    lateinit var damageTypeId: Identifier

    constructor(damageType: RegistryKey<DamageType>) : this() {
        id = ID
        damageTypeId = damageType.value
    }

    override fun update(world: World, player: PlayerEntity) {
        (player as IPlayerEntity).damageTypesImmunities[damageTypeId] = true
    }
}