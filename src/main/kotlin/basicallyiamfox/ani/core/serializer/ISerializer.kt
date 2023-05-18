package basicallyiamfox.ani.core.serializer

import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf

interface ISerializer<T> {
    fun toJson(obj: JsonObject, type: T)
    fun fromJson(obj: JsonObject): T

    fun toPacket(buf: PacketByteBuf, type: T)
    fun fromPacket(buf: PacketByteBuf): T
}