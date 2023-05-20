package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getEnum
import basicallyiamfox.ani.extensions.getIdentifier
import basicallyiamfox.ani.extensions.getInt
import basicallyiamfox.ani.util.ComparisonOperator
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class LightLevelConditionDecorator() : ConditionDecorator() {
    object Serializer : ISerializer<LightLevelConditionDecorator> {
        override fun toJson(obj: JsonObject, type: LightLevelConditionDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("operator", type.operator)
            obj.addProperty("compared", type.value)
        }

        override fun fromJson(obj: JsonObject): LightLevelConditionDecorator {
            val inst = LightLevelConditionDecorator()
            inst.id = obj.getIdentifier("id")
            inst.operator = obj.getEnum("operator")
            inst.value = obj.getInt("compared")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: LightLevelConditionDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeEnumConstant(type.operator)
            buf.writeByte(type.value)
        }

        override fun fromPacket(buf: PacketByteBuf): LightLevelConditionDecorator {
            val inst = LightLevelConditionDecorator()
            inst.id = buf.readIdentifier()
            inst.operator = buf.readEnumConstant(ComparisonOperator::class.java)
            inst.value = buf.readByte().toInt()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:light_level")
    }

    lateinit var operator: ComparisonOperator
    var value = 0

    constructor(operator: ComparisonOperator, value: Int) : this() {
        id = ID
        this.value = value
        this.operator = operator
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return operator.proceed(world.getLightLevel(player.blockPos), value)
    }
}