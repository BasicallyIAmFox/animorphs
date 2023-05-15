package basicallyiamfox.ani.json

import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.toJsonArray
import basicallyiamfox.ani.transformation.Transformation
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.Ability
import basicallyiamfox.ani.transformation.ability.AbilityManager
import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.condition.ConditionDecorators
import basicallyiamfox.ani.transformation.condition.decorator.DuringTimeTicksConditionDecorator
import basicallyiamfox.ani.transformation.condition.decorator.InDimensionConditionDecorator
import basicallyiamfox.ani.transformation.rule.Rule
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import basicallyiamfox.ani.transformation.rule.decorator.ImmuneDamageTypeRuleDecorator
import basicallyiamfox.ani.transformation.rule.decorator.StatusEffectRuleDecorator
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class Serializers {
    companion object {
        fun init() {
            init_Managers()

            init_Transformation()
            init_Condition()
            init_Ability()
            init_Rule()

            init_DuringTimeTicksConditionDecorator()
            init_InDimensionConditionDecorator()

            init_ImmuneDamageTypeRuleDecorator()
            init_StatusEffectRuleDecorator()
        }

        private fun init_Managers() {
            PacketSender.addSender<TransformationManager> { inst, buf ->
                buf.writeShort(inst.map.count())
                inst.map.forEach { (t, u) ->
                    buf.writeIdentifier(t)
                    PacketSender.toPacket(u, buf)
                }
            }
            PacketSender.addReceiver { buf ->
                val manager = TransformationManager()
                val map = hashMapOf<Identifier, Transformation>()

                val count = buf.readShort().toInt()
                for (i in 0 until count) {
                    val id = buf.readIdentifier()
                    val e = PacketSender.fromPacket<Transformation>(buf)
                    map[id] = e
                }

                manager.load(map)
                return@addReceiver manager
            }

            PacketSender.addSender<AbilityManager> { inst, buf ->
                buf.writeShort(inst.map.count())
                inst.map.forEach { (t, u) ->
                    buf.writeIdentifier(t)
                    PacketSender.toPacket(u, buf)
                }
            }
            PacketSender.addReceiver { buf ->
                val manager = AbilityManager()
                val map = hashMapOf<Identifier, Ability>()

                val count = buf.readShort().toInt()
                for (i in 0 until count) {
                    val id = buf.readIdentifier()
                    val e = PacketSender.fromPacket<Ability>(buf)
                    map[id] = e
                }

                manager.load(map)
                return@addReceiver manager
            }
        }

        private fun init_Transformation() {
            JsonSerializer.addSerializer<Transformation, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("skin", it.skin)
                obj.addProperty("slim", it.skinSlim)
                obj.addProperty("item", it.itemId)

                if (it.desc != null) {
                    if (it.desc!!.size == 1) {
                        obj.addProperty("description", it.desc!![0])
                    }
                    else {
                        obj.add("description", it.desc!!.map { e ->
                            JsonPrimitive(e)
                        }.toJsonArray())
                    }
                }

                if (it.color != null) {
                    obj.addProperty("color", it.color)
                }

                if (it.abilities.size > 0) {
                    obj.add("abilities", it.abilities.map { e ->
                        JsonPrimitive(e.toString())
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

                buf.writeBoolean(inst.desc != null)
                if (inst.desc != null) {
                    buf.writeByte(inst.desc!!.size)
                    inst.desc!!.forEach { e ->
                        buf.writeString(e)
                    }
                }

                buf.writeBoolean(inst.color != null)
                if (inst.color != null) {
                    buf.writeInt(inst.color!!)
                }

                buf.writeByte(inst.abilities.size)
                if (inst.abilities.size > 0) {
                    inst.abilities.forEach { e ->
                        buf.writeIdentifier(e)
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
                inst.setId(it.getIdentifier("id"))
                inst.setSkin(it.getIdentifier("skin"))
                inst.setSlim(it.getIdentifier("slim"))
                inst.setItem(Registries.ITEM.get(it.getIdentifier("item")))

                if (it.hasArray("description")) {
                    inst.setDesc(it.getArray("description").map { e ->
                        e.asString
                    })
                }
                else if (it.hasString("description")) {
                    inst.setDesc(arrayListOf(it.getString("description")))
                }

                if (it.hasNumber("color")) {
                    inst.setColor(it.getInt("color"))
                }

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
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }
        private fun init_Condition() {
            JsonSerializer.addSerializer<Condition, JsonObject> {
                val obj = JsonObject()

                if (it.decorator != null) {
                    ConditionDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!, obj)
                }

                return@addSerializer obj
            }
            PacketSender.addSender<Condition> { inst, buf ->
                buf.writeBoolean(inst.decorator != null)
                if (inst.decorator != null) {
                    val id = inst.decorator!!.id
                    buf.writeIdentifier(id)
                    ConditionDecorators.registry2[id]!!.serializer.applyObj(inst.decorator!!, buf)
                }
            }
            JsonSerializer.addDeserializer<Condition, JsonObject> {
                val inst = Condition()

                if (it.hasJsonObject("decorator")) {
                    val decObj = it.getObject("decorator")
                    val id = decObj.getIdentifier("id")
                    inst.setDecorator(ConditionDecorators.registry[id]!!.deserializer.apply(id, decObj))
                }

                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Condition()

                if (buf.readBoolean()) {
                    val id = buf.readIdentifier()
                    inst.setDecorator(ConditionDecorators.registry2[id]!!.deserializer.apply(buf))
                }

                return@addReceiver inst
            }
        }
        private fun init_Ability() {
            JsonSerializer.addSerializer<Ability, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("key", it.name)
                obj.addProperty("sign", it.sign)
                obj.addProperty("color", it.colorHex)

                if (it.desc != null) {
                    if (it.desc!!.size == 1) {
                        obj.addProperty("description", it.desc!![0])
                    }
                    else {
                        obj.add("description", it.desc!!.map { e ->
                            JsonPrimitive(e)
                        }.toJsonArray())
                    }
                }

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
                buf.writeInt(inst.colorHex)

                buf.writeBoolean(inst.desc != null)
                if (inst.desc != null) {
                    buf.writeByte(inst.desc!!.size)
                    inst.desc!!.forEach { e ->
                        buf.writeString(e)
                    }
                }

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
                inst.setId(it.getIdentifier("id"))
                inst.setName(it.getString("key"))
                inst.setSign(it.asEnum("sign"))
                inst.setColor(it.getInt("color"))

                if (it.hasArray("description")) {
                    inst.setDesc(it.getArray("description").map { e ->
                        e.asString
                    })
                }
                else if (it.hasString("description")) {
                    inst.setDesc(arrayListOf(it.getString("description")))
                }

                if (it.hasArray("conditions")) {
                    inst.addConditions(it.getArray("conditions").map { e ->
                        JsonSerializer.fromJson<Condition, JsonObject>(e.asJsonObject)
                    })
                }
                if (it.hasArray("rules")) {
                    inst.addRules(it.getArray("rules").map { e ->
                        JsonSerializer.fromJson<Rule, JsonObject>(e.asJsonObject)
                    })
                }
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Ability()
                inst.setId(buf.readIdentifier())
                inst.setName(buf.readString())
                inst.setSign(buf.readEnumConstant(Ability.Sign::class.java))
                inst.setColor(buf.readInt())

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
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                val ruleLength = buf.readByte()
                for (i in 0 until ruleLength) {
                    inst.rules.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }
        private fun init_Rule() {
            JsonSerializer.addSerializer<Rule, JsonObject> {
                val obj = JsonObject()

                if (it.decorator != null) {
                    RuleDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!, obj)
                }

                if (it.conditions.size > 0) {
                    obj.add("conditions", it.conditions.map { e ->
                        JsonSerializer.toJson<Condition, JsonObject>(e)
                    }.toJsonArray())
                }
                return@addSerializer obj
            }
            PacketSender.addSender<Rule> { inst, buf ->
                buf.writeBoolean(inst.decorator != null)
                if (inst.decorator != null) {
                    val id = inst.decorator!!.id
                    buf.writeIdentifier(id)
                    RuleDecorators.registry2[id]!!.serializer.applyObj(inst.decorator!!, buf)
                }

                buf.writeByte(inst.conditions.size)
                if (inst.conditions.size > 0) {
                    inst.conditions.forEach { e ->
                        PacketSender.toPacket(e, buf)
                    }
                }
            }
            JsonSerializer.addDeserializer<Rule, JsonObject> {
                val inst = Rule()

                if (it.hasJsonObject("decorator")) {
                    val decObj = it.getObject("decorator")
                    val id = decObj.getIdentifier("id")
                    inst.setDecorator(RuleDecorators.registry[id]!!.deserializer.apply(id, decObj))
                }

                if (it.hasArray("conditions")) {
                    inst.addConditions(it.getArray("conditions").map { e ->
                        JsonSerializer.fromJson<Condition, JsonObject>(e.asJsonObject)
                    })
                }
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = Rule()

                if (buf.readBoolean()) {
                    val id = buf.readIdentifier()
                    inst.setDecorator(RuleDecorators.registry2[id]!!.deserializer.apply(buf))
                }

                val conditionLength = buf.readByte()
                for (i in 0 until conditionLength) {
                    inst.conditions.add(PacketSender.fromPacket(buf))
                }
                return@addReceiver inst
            }
        }

        private fun init_DuringTimeTicksConditionDecorator() {
            JsonSerializer.addSerializer<DuringTimeTicksConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("from", it.from)
                obj.addProperty("to", it.to)
                return@addSerializer obj
            }
            PacketSender.addSender<DuringTimeTicksConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeShort(inst.from)
                buf.writeShort(inst.to)
            }
            JsonSerializer.addDeserializer<DuringTimeTicksConditionDecorator, JsonObject> {
                val inst = DuringTimeTicksConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.from = it.getInt("from")
                inst.to = it.getInt("to")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = DuringTimeTicksConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.from = buf.readShort().toInt()
                inst.to = buf.readShort().toInt()
                return@addReceiver inst
            }
        }
        private fun init_InDimensionConditionDecorator() {
            JsonSerializer.addSerializer<InDimensionConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("world", it.worldId)
                return@addSerializer obj
            }
            PacketSender.addSender<InDimensionConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.worldId)
            }
            JsonSerializer.addDeserializer<InDimensionConditionDecorator, JsonObject> {
                val inst = InDimensionConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.worldId = it.getIdentifier("world")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = InDimensionConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.worldId = buf.readIdentifier()
                return@addReceiver inst
            }
        }

        private fun init_ImmuneDamageTypeRuleDecorator() {
            JsonSerializer.addSerializer<ImmuneDamageTypeRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("damageType", it.damageTypeId)
                return@addSerializer obj
            }
            PacketSender.addSender<ImmuneDamageTypeRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.damageTypeId)
            }
            JsonSerializer.addDeserializer<ImmuneDamageTypeRuleDecorator, JsonObject> {
                val inst = ImmuneDamageTypeRuleDecorator()
                inst.id = it.getIdentifier("id")
                inst.damageTypeId = it.getIdentifier("damageType")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = ImmuneDamageTypeRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.damageTypeId = buf.readIdentifier()
                return@addReceiver inst
            }
        }
        private fun init_StatusEffectRuleDecorator() {
            JsonSerializer.addSerializer<StatusEffectRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("effect", it.statusEffectId)
                return@addSerializer obj
            }
            PacketSender.addSender<StatusEffectRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.statusEffectId)
            }
            JsonSerializer.addDeserializer<StatusEffectRuleDecorator, JsonObject> {
                val inst = StatusEffectRuleDecorator()
                inst.id = it.getIdentifier("id")
                inst.statusEffectId = it.getIdentifier("effect")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = StatusEffectRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.statusEffectId = buf.readIdentifier()
                return@addReceiver inst
            }
        }
    }
}