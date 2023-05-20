package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World

class IsSkyVisibleConditionDecorator : ConditionDecorator() {
    object Serializer : ISerializer<IsSkyVisibleConditionDecorator> {
        override fun toJson(obj: JsonObject, type: IsSkyVisibleConditionDecorator) {
            obj.addProperty("id", type.id)
        }

        override fun fromJson(obj: JsonObject): IsSkyVisibleConditionDecorator {
            val inst = IsSkyVisibleConditionDecorator()
            inst.id = obj.getIdentifier("id")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: IsSkyVisibleConditionDecorator) {
            buf.writeIdentifier(type.id)
        }

        override fun fromPacket(buf: PacketByteBuf): IsSkyVisibleConditionDecorator {
            val inst = IsSkyVisibleConditionDecorator()
            inst.id = buf.readIdentifier()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:is_sky_visible")
    }

    init {
        id = ID
    }

    override fun isActive(world: World, player: PlayerEntity): Boolean {
        return player.world.isSkyVisible(player.blockPos)
    }
}