package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.asEnum
import basicallyiamfox.ani.json.getFloat
import basicallyiamfox.ani.json.getIdentifier
import basicallyiamfox.ani.util.ComparisonOperator
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class BiomeTemperatureConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<BiomeTemperatureConditionDecorator> {
        override fun toJson(obj: JsonObject, type: BiomeTemperatureConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("operator", type.operator)
            obj.addProperty("compared", type.value)
        }

        override fun fromJson(obj: JsonObject): BiomeTemperatureConditionDecorator {
            val inst = BiomeTemperatureConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.operator = obj.asEnum("operator")
            inst.value = obj.getFloat("compared")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: BiomeTemperatureConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeEnumConstant(type.operator)
            buf.writeFloat(type.value)
        }

        override fun fromPacket(buf: PacketByteBuf): BiomeTemperatureConditionDecorator {
            val inst = BiomeTemperatureConditionDecorator()
            inst.id = buf.readIdentifier()
            inst.operator = buf.readEnumConstant(ComparisonOperator::class.java)
            inst.value = buf.readFloat()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:biome_temperature")
    }

    lateinit var operator: ComparisonOperator
    var value = 0.0f

    constructor(operator: ComparisonOperator, value: Float) : this() {
        id = ID
        this.value = value
        this.operator = operator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return operator.proceed(world.getBiome(player.blockPos).value().temperature, value)
    }
}