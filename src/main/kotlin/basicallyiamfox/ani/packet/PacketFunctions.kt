package basicallyiamfox.ani.packet

import basicallyiamfox.ani.util.Action
import net.minecraft.network.PacketByteBuf
import java.util.function.Function

open class PacketFunctions<TType>(var serializer: Action<out TType, PacketByteBuf>, var deserializer: Function<PacketByteBuf, TType>)