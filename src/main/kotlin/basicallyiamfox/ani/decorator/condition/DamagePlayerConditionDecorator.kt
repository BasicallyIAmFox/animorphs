package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getFloat
import basicallyiamfox.ani.json.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

class DamagePlayerConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<DamagePlayerConditionDecorator> {
        override fun toJson(obj: JsonObject, type: DamagePlayerConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("damage_type", type.damageTypeId)
            obj.addProperty("damage_value", type.damageValue)
        }

        override fun fromJson(obj: JsonObject): DamagePlayerConditionDecorator {
            val inst = DamagePlayerConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.damageTypeId = obj.getIdentifier("damage_type")
            inst.damageValue = obj.getFloat("damage_value")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: DamagePlayerConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.damageTypeId)
            buf.writeFloat(type.damageValue)
        }

        override fun fromPacket(buf: PacketByteBuf): DamagePlayerConditionDecorator {
            val inst = DamagePlayerConditionDecorator()
            inst.id = buf.readIdentifier()
            inst.damageTypeId = buf.readIdentifier()
            inst.damageValue = buf.readFloat()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:damage_player")
    }

    lateinit var damageTypeId: Identifier
    var damageValue: Float = 0f

    constructor(damageType: RegistryKey<DamageType>, damageValue: Float) : this() {
        id = ID
        damageTypeId = damageType.value
        this.damageValue = damageValue
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.damage(DamageSource(
            world.registryManager.get(RegistryKeys.DAMAGE_TYPE).getEntry(
                world.registryManager.get(RegistryKeys.DAMAGE_TYPE).get(damageTypeId))
        ), damageValue)
    }
}