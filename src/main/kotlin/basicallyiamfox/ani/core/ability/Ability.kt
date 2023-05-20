package basicallyiamfox.ani.core.ability

import basicallyiamfox.ani.core.condition.Condition
import basicallyiamfox.ani.core.rule.Rule
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.*
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.awt.Color

class Ability {
    object Serializer : ISerializer<Ability> {
        override fun toJson(obj: JsonObject, type: Ability) {
            obj.addProperty("id", type.id)
            obj.addProperty("key", type.name)
            obj.addProperty("sign", type.sign)
            obj.addProperty("color", type.colorHex.and(0xFFFFFF))
            if (!type.visible) {
                obj.addProperty("is_visible", type.visible)
            }

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

            if (type.conditions.size > 0) {
                obj.add("conditions", type.conditions.map { e ->
                    val typeObj = JsonObject()
                    Condition.Serializer.toJson(typeObj, e)
                    typeObj
                }.toJsonArray())
            }
            if (type.rules.size > 0) {
                obj.add("rules", type.rules.map { e ->
                    val typeObj = JsonObject()
                    Rule.Serializer.toJson(typeObj, e)
                    typeObj
                }.toJsonArray())
            }
        }

        override fun fromJson(obj: JsonObject): Ability {
            val inst = Ability()
            inst.setId(obj.getIdentifier("id"))
            inst.setName(obj.getString("key"))
            inst.setSign(obj.getEnum("sign"))
            inst.setColor(obj.getInt("color").or(0xFF000000.toInt()))
            if (obj.hasBoolean("is_visible")) {
                inst.setVisible(obj.getBoolean("is_visible"))
            }

            if (obj.hasArray("description")) {
                inst.setDesc(obj.getArray("description").map { e ->
                    e.asString
                })
            }
            else if (obj.hasString("description")) {
                inst.setDesc(arrayListOf(obj.getString("description")))
            }

            if (obj.hasArray("conditions")) {
                inst.addConditions(obj.getArray("conditions").map { e ->
                    Condition.Serializer.fromJson(e.asJsonObject)
                })
            }
            if (obj.hasArray("rules")) {
                inst.addRules(obj.getArray("rules").map { e ->
                    Rule.Serializer.fromJson(e.asJsonObject)
                })
            }
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: Ability) {
            buf.writeIdentifier(type.id)
            buf.writeString(type.name)
            buf.writeEnumConstant(type.sign)
            buf.writeInt(type.colorHex)
            buf.writeBoolean(type.visible)

            buf.writeBoolean(type.desc != null)
            if (type.desc != null) {
                buf.writeByte(type.desc!!.size)
                type.desc!!.forEach { e ->
                    buf.writeString(e)
                }
            }

            buf.writeByte(type.conditions.size)
            if (type.conditions.size > 0) {
                type.conditions.forEach { e ->
                    Condition.Serializer.toPacket(buf, e)
                }
            }

            buf.writeByte(type.rules.size)
            if (type.rules.size > 0) {
                type.rules.forEach { e ->
                    Rule.Serializer.toPacket(buf, e)
                }
            }
        }

        override fun fromPacket(buf: PacketByteBuf): Ability {
            val inst = Ability()
            inst.setId(buf.readIdentifier())
            inst.setName(buf.readString())
            inst.setSign(buf.readEnumConstant(Sign::class.java))
            inst.setColor(buf.readInt())
            inst.setVisible(buf.readBoolean())

            if (buf.readBoolean()) {
                val length = buf.readByte()
                val list = arrayListOf<String>()
                for (i in 0 until length) {
                    list.add(buf.readString())
                }
                inst.setDesc(list)
            }

            val conditionLength = buf.readByte()
            for (i in 0 until conditionLength) {
                inst.conditions.add(Condition.Serializer.fromPacket(buf))
            }
            val ruleLength = buf.readByte()
            for (i in 0 until ruleLength) {
                inst.rules.add(Rule.Serializer.fromPacket(buf))
            }
            return inst
        }
    }

    enum class Sign(val text: String, val colorHex: Int) {
        POSITIVE("animorphs.ability_sign.animorphs.positive", Color(15, 190, 25).rgb),
        NEUTRAL("animorphs.ability_sign.animorphs.neutral", Color(190, 190, 40).rgb),
        NEGATIVE("animorphs.ability_sign.animorphs.negative", Color(190, 15, 25).rgb);
    }

    val conditions = arrayListOf<Condition>()
    val rules = arrayListOf<Rule>()

    lateinit var id: Identifier
        private set
    lateinit var name: String
        private set
    var visible: Boolean = true
        private set
    var desc: List<String>? = null
        private set
    var sign = Sign.NEUTRAL
        private set
    var colorHex = 0xFFFFFF
        private set

    fun tick(world: World, player: PlayerEntity) {
        if (conditions.all { condition -> condition.isActive(world, player) }) {
            rules.forEach {
                it.tick(world, player)
            }
        }
    }

    fun addConditions(conditions: Iterable<Condition>): Ability {
        this.conditions.addAll(conditions)
        return this
    }
    fun addRules(rules: Iterable<Rule>): Ability {
        this.rules.addAll(rules)
        return this
    }

    fun setId(id: Identifier): Ability {
        this.id = id
        return this
    }
    fun setName(key: String): Ability {
        name = key
        return this
    }
    fun setDesc(keys: Iterable<String>): Ability {
        desc = keys.toList()
        return this
    }
    fun setSign(sign: Sign): Ability {
        this.sign = sign
        return this
    }
    fun setColor(colorHex: Int): Ability {
        this.colorHex = colorHex
        return this
    }
    fun setColor(color: Color): Ability {
        colorHex = color.rgb
        return this
    }
    fun setVisible(visible: Boolean): Ability {
        this.visible = visible
        return this
    }
}