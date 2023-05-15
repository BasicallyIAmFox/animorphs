package basicallyiamfox.ani.transformation

import basicallyiamfox.ani.json.*
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.toJsonArray
import basicallyiamfox.ani.transformation.condition.Condition
import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class Transformation {
    companion object {
        init {
            JsonSerializer.addSerializer<Transformation, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("skin", it.skin)
                obj.addProperty("slim", it.skinSlim)
                obj.addProperty("item", it.itemId)

                if (it.abilities.size > 0) {
                    obj.add("abilities", it.abilities.map { e ->
                        com.google.gson.JsonPrimitive(e.toString())
                    }.toJsonArray())
                }
                if (it.conditions.size > 0) {
                    obj.add("conditions", it.conditions.map { e ->
                        JsonSerializer.toJson<Condition, JsonObject>(e)
                    }.toJsonArray())
                }
                return@addSerializer obj
            }
            PacketSender.addSender<Transformation> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.skin)
                buf.writeIdentifier(inst.skinSlim)
                buf.writeIdentifier(inst.itemId)

                buf.writeByte(inst.abilities.size)
                if (inst.abilities.size > 0) {
                    inst.abilities.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }
                buf.writeByte(inst.conditions.size)
                if (inst.conditions.size > 0) {
                    inst.conditions.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }
            }
            JsonSerializer.addDeserializer<Transformation, JsonObject> {
                val inst = Transformation()
                inst.setId(it.asIdentifier("id"))
                inst.setSkin(it.asIdentifier("skin"))
                inst.setSlim(it.asIdentifier("slim"))
                inst.setItem(Registries.ITEM.get(it.asIdentifier("item")))

                if (it.hasArray("abilities")) {
                    inst.addAbilities(it.getArray("abilities").map { e ->
                        Identifier(e.asString)
                    })
                }
                if (it.hasArray("conditions")) {
                    inst.addConditions(it.getArray("conditions").map { e ->
                        JsonSerializer.fromJson<Condition, JsonObject>(e.asJsonObject)
                    })
                }
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Transformation()
                inst.id = buf.readIdentifier()
                inst.skin = buf.readIdentifier()
                inst.skinSlim = buf.readIdentifier()
                inst.itemId = buf.readIdentifier()

                val abilitiesLength = buf.readByte()
                for (i in 0 until abilitiesLength) {
                    inst.abilities.add(PacketSender.fromPacket(buf))
                }
                val conditionLength = buf.readByte()
                for (i in 0 until conditionLength) {
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }
    }

    val abilities = arrayListOf<Identifier>()
    val conditions = arrayListOf<Condition>()

    lateinit var id: Identifier
        private set
    lateinit var skin: Identifier
        private set
    lateinit var skinSlim: Identifier
        private set
    lateinit var itemId: Identifier
        private set

    fun addAbilities(abilities: Iterable<Identifier>): Transformation {
        this.abilities.addAll(abilities)
        return this
    }
    fun addConditions(conditions: Iterable<Condition>): Transformation {
        this.conditions.addAll(conditions)
        return this
    }

    fun setId(id: Identifier): Transformation {
        this.id = id
        return this
    }
    fun setSkin(skin: Identifier): Transformation {
        this.skin = skin
        return this
    }
    fun setSlim(skinSlim: Identifier): Transformation {
        this.skinSlim = skinSlim
        return this
    }
    fun setItem(item: Item): Transformation {
        itemId = Registries.ITEM.getId(item)
        return this
    }
}