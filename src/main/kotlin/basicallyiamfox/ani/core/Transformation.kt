package basicallyiamfox.ani.core

import basicallyiamfox.ani.core.condition.Condition
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.*
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.World

class Transformation {
    object Serializer : ISerializer<Transformation> {
        override fun toJson(obj: JsonObject, type: Transformation) {
            obj.addProperty("id", type.id)
            obj.addProperty("skin", type.skin)
            obj.addProperty("slim", type.skinSlim)
            obj.addProperty("item", type.itemId)

            if (type.desc != null) {
                if (type.desc!!.size == 1) {
                    obj.addProperty("description", type.desc!![0])
                }
                else {
                    obj.add("description", type.desc!!.map { e ->
                        JsonPrimitive(e)
                    }.toJsonArray())
                }
            }

            if (type.color != null) {
                obj.addProperty("color", type.color!!.and(0xFFFFFF))
            }

            if (type.abilities.size > 0) {
                obj.add("abilities", type.abilities.map { e ->
                    JsonPrimitive(e.toString())
                }.toJsonArray())
            }
            if (type.conditions.size > 0) {
                obj.add("conditions", type.conditions.map { e ->
                    val typeObj = JsonObject()
                    Condition.Serializer.toJson(typeObj, e)
                    typeObj
                }.toJsonArray())
            }
        }

        override fun fromJson(obj: JsonObject): Transformation {
            val inst = Transformation()
            inst.setId(obj.getIdentifier("id"))
            inst.setSkin(obj.getIdentifier("skin"))
            inst.setSlim(obj.getIdentifier("slim"))
            inst.setItem(Registries.ITEM.get(obj.getIdentifier("item")))

            if (obj.hasArray("description")) {
                inst.setDesc(obj.getArray("description").map { e ->
                    e.asString
                })
            }
            else if (obj.hasString("description")) {
                inst.setDesc(arrayListOf(obj.getString("description")))
            }

            if (obj.hasNumber("color")) {
                inst.setColor(obj.getInt("color").or(0xFF000000.toInt()))
            }

            if (obj.hasArray("abilities")) {
                inst.addAbilities(obj.getArray("abilities").map { e ->
                    Identifier(e.asString)
                })
            }
            if (obj.hasArray("conditions")) {
                inst.addConditions(obj.getArray("conditions").map { e ->
                    Condition.Serializer.fromJson(e.asJsonObject)
                })
            }
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: Transformation) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.skin)
            buf.writeIdentifier(type.skinSlim)
            buf.writeIdentifier(type.itemId)

            buf.writeBoolean(type.desc != null)
            if (type.desc != null) {
                buf.writeByte(type.desc!!.size)
                type.desc!!.forEach { e ->
                    buf.writeString(e)
                }
            }

            buf.writeBoolean(type.color != null)
            if (type.color != null) {
                buf.writeInt(type.color!!)
            }

            buf.writeByte(type.abilities.size)
            if (type.abilities.size > 0) {
                type.abilities.forEach { e ->
                    buf.writeIdentifier(e)
                }
            }
            buf.writeByte(type.conditions.size)
            if (type.conditions.size > 0) {
                type.conditions.forEach { e ->
                    Condition.Serializer.toPacket(buf, e)
                }
            }
        }

        override fun fromPacket(buf: PacketByteBuf): Transformation {
            val inst = Transformation()
            inst.setId(buf.readIdentifier())
            inst.setSkin(buf.readIdentifier())
            inst.setSlim(buf.readIdentifier())
            inst.setItem(Registries.ITEM.get(buf.readIdentifier()))

            if (buf.readBoolean()) {
                val length = buf.readByte()
                val list = arrayListOf<String>()
                for (i in 0 until length) {
                    list.add(buf.readString())
                }
                inst.setDesc(list)
            }

            if (buf.readBoolean()) {
                inst.setColor(buf.readInt())
            }

            val abilitiesLength = buf.readByte()
            for (i in 0 until abilitiesLength) {
                inst.abilities.add(buf.readIdentifier())
            }
            val conditionLength = buf.readByte()
            for (i in 0 until conditionLength) {
                inst.conditions.add(Condition.Serializer.fromPacket(buf))
            }
            return inst
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
    var desc: List<String>? = null
        private set
    var color: Int? = null
        private set

    fun isActive(world: World, player: PlayerEntity): Boolean {
        return conditions.all { condition -> condition.isActive(world, player) }
    }

    fun tick(world: World, player: PlayerEntity) {
        if (isActive(world, player)) {
            abilities.map { player.abilityManager!!.get(it) }.forEach {
                it?.tick(world, player)
            }
        }
    }

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
    fun setColor(color: Int): Transformation {
        this.color = color
        return this
    }
    fun setDesc(keys: Iterable<String>): Transformation {
        desc = keys.toList()
        return this
    }
}