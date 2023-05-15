package basicallyiamfox.ani.packet

import basicallyiamfox.ani.util.Action
import net.minecraft.network.PacketByteBuf
import java.util.function.Function

class PacketSender {
    companion object {
        class Self<T>(var value: T)

        val toJsonFunctions: MutableMap<String, Action<Self<*>, PacketByteBuf>> =
            hashMapOf()
        val fromJsonFunctions: MutableMap<String, Function<PacketByteBuf, *>> =
            hashMapOf()

        inline fun <reified TType> addSender(function: Action<TType, PacketByteBuf>) {
            val typeClass = TType::class.java.name

            if (!toJsonFunctions.containsKey(typeClass)) {
                toJsonFunctions[typeClass] = Action { self, packetByteBuf -> function.apply(self.value as TType, packetByteBuf) }
            }
        }

        inline fun <reified TType> addReceiver(function: Function<PacketByteBuf, TType>) {
            val typeClass = TType::class.java.name

            if (!fromJsonFunctions.containsKey(typeClass)) {
                fromJsonFunctions[typeClass] = function
            }
        }

        inline fun <reified TType> toPacket(value: TType, obj: PacketByteBuf) {
            val func = toJsonFunctions[TType::class.java.name]
            func?.apply(Self(value), obj)
        }

        inline fun <reified TType> fromPacket(obj: PacketByteBuf): TType {
            val func = fromJsonFunctions[TType::class.java.name]
            return func?.apply(obj) as TType
        }
    }
}