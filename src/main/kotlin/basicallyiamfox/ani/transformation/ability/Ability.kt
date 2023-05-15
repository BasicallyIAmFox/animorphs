package basicallyiamfox.ani.transformation.ability

import basicallyiamfox.ani.json.*
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.toJsonArray
import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.rule.Rule
import com.google.gson.JsonObject
import net.minecraft.util.Identifier
import java.awt.Color

class Ability {
    companion object {
        init {
            JsonSerializer.addSerializer<Ability, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("key", it.name)
                obj.addProperty("sign", it.sign)
                obj.addProperty("color", it.colorHex)

                if (it.conditions.size > 0) {
                    obj.add("conditions", it.conditions.map { e ->
                        JsonSerializer.toJson<Condition, JsonObject>(e)
                    }.toJsonArray())
                }
                if (it.rules.size > 0) {
                    obj.add("rules", it.rules.map { e ->
                        JsonSerializer.toJson<Rule, JsonObject>(e)
                    }.toJsonArray())
                }
                return@addSerializer obj
            }
            PacketSender.addSender<Ability> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeString(inst.name)
                buf.writeEnumConstant(inst.sign)
                buf.writeByte(inst.colorHex.and(0xFF))
                buf.writeByte(inst.colorHex.shr(8).and(0xFF))
                buf.writeByte(inst.colorHex.shr(16).and(0xFF))

                buf.writeByte(inst.conditions.size)
                if (inst.conditions.size > 0) {
                    inst.conditions.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }

                buf.writeByte(inst.rules.size)
                if (inst.rules.size > 0) {
                    inst.rules.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }
            }
            JsonSerializer.addDeserializer<Ability, JsonObject> {
                val inst = Ability()
                inst.setId(it.asIdentifier("id"))
                inst.setName(it.getString("key"))
                inst.setSign(it.asEnum("sign"))
                inst.setColor(it.getInt("color"))

                println("conditions")
                if (it.hasArray("conditions")) {
                    inst.addConditions(it.getArray("conditions").map { e ->
                        JsonSerializer.fromJson<Condition, JsonObject>(e.asJsonObject)
                    })
                }
                println("rules")
                if (it.hasArray("rules")) {
                    inst.addRules(it.getArray("rules").map { e ->
                        JsonSerializer.fromJson<Rule, JsonObject>(e.asJsonObject)
                    })
                }
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Ability()
                inst.id = buf.readIdentifier()
                inst.name = buf.readString()
                inst.sign = buf.readEnumConstant(Sign::class.java)
                val colorR = buf.readByte().toInt()
                val colorG = buf.readByte().toInt()
                val colorB = buf.readByte().toInt()
                inst.colorHex = (colorR).and(colorG.shl(8)).and(colorB.shl(16))

                val conditionLength = buf.readByte()
                for (i in 0 until conditionLength) {
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                val ruleLength = buf.readByte()
                for (i in 0 until ruleLength) {
                    inst.rules.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }
    }
    enum class Sign(val text: String, val colorHex: Int) {
        POSITIVE("[+]", 0x0FBE19), // 15, 190, 25
        NEUTRAL("[=]", 0xBE0F19),  // 190, 15, 25
        NEGATIVE("[-]", 0xBEBE28); // 190, 190, 40
    }

    val conditions = arrayListOf<Condition>()
    val rules = arrayListOf<Rule>()

    lateinit var id: Identifier
        private set
    lateinit var name: String
        private set
    var sign = Sign.NEUTRAL
        private set
    var colorHex = 0xFFFFFF
        private set

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
}