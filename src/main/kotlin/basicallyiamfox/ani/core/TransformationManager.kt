package basicallyiamfox.ani.core

import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getIdentifier
import basicallyiamfox.ani.extensions.toJsonArray
import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class TransformationManager {
    object Serializer : ISerializer<TransformationManager> {
        override fun toJson(obj: JsonObject, type: TransformationManager) {
            obj.add("elements", type.map.map { (k, v) ->
                val transObj = JsonObject()
                transObj.addProperty("id", k)
                Transformation.Serializer.toJson(transObj, v)
                transObj
            }.toJsonArray())
        }

        override fun fromJson(obj: JsonObject): TransformationManager {
            val manager = TransformationManager()
            val map = hashMapOf<Identifier, Transformation>()
            obj.getAsJsonArray("elements").forEach {
                val eObj = it.asJsonObject
                val id = eObj.getIdentifier("id")
                val value = Transformation.Serializer.fromJson(eObj)
                map[id] = value
            }
            manager.load(map)
            return manager
        }

        override fun toPacket(buf: PacketByteBuf, type: TransformationManager) {
            buf.writeShort(type.map.count())
            type.map.forEach { (t, u) ->
                buf.writeIdentifier(t)
                Transformation.Serializer.toPacket(buf, u)
            }
        }

        override fun fromPacket(buf: PacketByteBuf): TransformationManager {
            val manager = TransformationManager()
            val map = hashMapOf<Identifier, Transformation>()

            val count = buf.readShort().toInt()
            for (i in 0 until count) {
                val id = buf.readIdentifier()
                val e = Transformation.Serializer.fromPacket(buf)
                map[id] = e
            }

            manager.load(map)
            return manager
        }
    }

    var map: MutableMap<Identifier, Transformation> = hashMapOf()
    var typeByItemId: MutableMap<Identifier, Transformation> = hashMapOf()

    fun load(map: Map<Identifier, Transformation>) {
        map.forEach { (t, u) ->
            this.map[t] = u
            typeByItemId[u.itemId] = u
        }
    }

    fun get(): Iterable<Transformation> {
        return map.values
    }

    fun get(id: Identifier): Transformation? = map[id]
    fun get(item: Item): Transformation? = typeByItemId[Registries.ITEM.getId(item)]
}