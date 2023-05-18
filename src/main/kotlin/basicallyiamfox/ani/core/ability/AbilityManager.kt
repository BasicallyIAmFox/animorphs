package basicallyiamfox.ani.core.ability

import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.toJsonArray
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class AbilityManager {
    object Serializer : ISerializer<AbilityManager> {
        override fun toJson(obj: JsonObject, type: AbilityManager) {
            obj.add("elements", type.map.map { (k, v) ->
                val abilityObj = JsonObject()
                abilityObj.addProperty("id", k)
                Ability.Serializer.toJson(abilityObj, v)
                abilityObj
            }.toJsonArray())
        }

        override fun fromJson(obj: JsonObject): AbilityManager {
            val manager = AbilityManager()
            val map = hashMapOf<Identifier, Ability>()
            obj.getAsJsonArray("elements").forEach {
                val eObj = it.asJsonObject
                val id = eObj.getIdentifier("id")
                val value = Ability.Serializer.fromJson(eObj)
                map[id] = value
            }
            manager.load(map)
            return manager
        }

        override fun toPacket(buf: PacketByteBuf, type: AbilityManager) {
            buf.writeShort(type.map.count())
            type.map.forEach { (t, u) ->
                buf.writeIdentifier(t)
                Ability.Serializer.toPacket(buf, u)
            }
        }

        override fun fromPacket(buf: PacketByteBuf): AbilityManager {
            val manager = AbilityManager()
            val map = hashMapOf<Identifier, Ability>()

            val count = buf.readShort().toInt()
            for (i in 0 until count) {
                val id = buf.readIdentifier()
                val e = Ability.Serializer.fromPacket(buf)
                map[id] = e
            }

            manager.load(map)
            return manager
        }
    }

    lateinit var map: MutableMap<Identifier, Ability>

    fun load(map: Map<Identifier, Ability>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(): Iterable<Ability> {
        return map.values
    }

    fun get(id: Identifier): Ability? {
        return map[id]
    }
}