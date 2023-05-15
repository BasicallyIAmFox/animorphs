package basicallyiamfox.ani.packet

import basicallyiamfox.ani.util.Action
import net.minecraft.network.PacketByteBuf
import java.util.function.Function

class PacketSender {
    companion object {
        class Self<T>(var value: T)

        val toJsonFunctions: MutableMap<Class<*>, Action<Self<*>, PacketByteBuf>> =
            HashMap()
        val fromJsonFunctions: MutableMap<Class<*>, Function<PacketByteBuf, *>> =
            HashMap()

        inline fun <reified TType> addSender(function: Action<TType, PacketByteBuf>) {
            val typeClass: Class<TType> = TType::class.java

            if (!toJsonFunctions.containsKey(typeClass)) {
                toJsonFunctions[typeClass] = Action { self, packetByteBuf -> function.apply(self.value as TType, packetByteBuf) }
            }
        }

        inline fun <reified TType> addReceiver(function: Function<PacketByteBuf, TType>) {
            val typeClass: Class<TType> = TType::class.java

            if (!fromJsonFunctions.containsKey(typeClass)) {
                fromJsonFunctions[typeClass] = function
            }
        }

        inline fun <reified TType> toPacket(value: TType, obj: PacketByteBuf) {
            toJsonFunctions[TType::class.java]!!.apply(Self(value), obj)
        }

        inline fun <reified TType> fromPacket(obj: PacketByteBuf): TType {
            return fromJsonFunctions[TType::class.java]!!.apply(obj) as TType
        }
    }
}