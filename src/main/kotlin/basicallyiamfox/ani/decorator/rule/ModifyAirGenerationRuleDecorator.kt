package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import basicallyiamfox.ani.json.getInt
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ModifyAirGenerationRuleDecorator() : RuleDecorator() {
    interface AirGenerationPlayerEntity {
        var airGeneration: Int
    }
    object Serializer : ISerializer<ModifyAirGenerationRuleDecorator> {
        override fun toJson(obj: JsonObject, type: ModifyAirGenerationRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("value", type.value)
        }

        override fun fromJson(obj: JsonObject): ModifyAirGenerationRuleDecorator {
            val inst = ModifyAirGenerationRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.value = obj.getInt("value")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: ModifyAirGenerationRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeInt(type.value)
        }

        override fun fromPacket(buf: PacketByteBuf): ModifyAirGenerationRuleDecorator {
            val inst = ModifyAirGenerationRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.value = buf.readInt()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:modify_air_generation")
    }

    var value: Int = 0

    constructor(value: Int) : this() {
        id = ID
        this.value = value
    }

    override fun update(world: World, player: PlayerEntity) {
        player as AirGenerationPlayerEntity
        player.airGeneration += value
    }
}