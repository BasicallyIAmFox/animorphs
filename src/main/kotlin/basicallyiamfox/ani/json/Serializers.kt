package basicallyiamfox.ani.json

import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.toJsonArray
import basicallyiamfox.ani.transformation.Transformation
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.Ability
import basicallyiamfox.ani.transformation.ability.AbilityManager
import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.condition.ConditionDecorators
import basicallyiamfox.ani.transformation.condition.decorator.*
import basicallyiamfox.ani.transformation.rule.Rule
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import basicallyiamfox.ani.transformation.rule.decorator.*
import basicallyiamfox.ani.util.ComparisonOperator
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

            init_AndConditionDecorator()
            init_OrConditionDecorator()
            init_DuringTimeTicksConditionDecorator()
            init_InDimensionConditionDecorator()
            init_IsDayConditionDecorator()
            init_IsNightConditionDecorator()
            init_IsSkyVisibleConditionDecorator()
            init_LightLevelConditionDecorator()
            init_BiomeTemperatureConditionDecorator()

            init_ImmuneDamageTypeRuleDecorator()
            init_StatusEffectRuleDecorator()
            init_BeeflyRuleDecorator()
            init_NoteTickRuleDecorator()
            init_MagmaticJumpRuleDecorator()
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
                    obj.addProperty("color", it.color!!.and(0xFFFFFF))
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
                    inst.setColor(it.getInt("color").or(0xFF000000.toInt()))
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
                    obj.add("decorator", ConditionDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!))
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
                obj.addProperty("color", it.colorHex.and(0xFFFFFF))

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
                inst.setColor(it.getInt("color").or(0xFF000000.toInt()))

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
                    obj.add("decorator", RuleDecorators.registry[it.decorator!!.id]!!.serializer.applyObj(it.decorator!!))
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

        private fun init_AndConditionDecorator() {
            JsonSerializer.addSerializer<AndConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.add("elements", it.decorators.map { e ->
                    ConditionDecorators.registry[e.id]!!.serializer.applyObj(e)
                }.toJsonArray())
                return@addSerializer obj
            }
            PacketSender.addSender<AndConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeByte(inst.decorators.size)
                inst.decorators.forEach { e ->
                    buf.writeIdentifier(e.id)
                    ConditionDecorators.registry2[e.id]!!.serializer.applyObj(e, buf)
                }
            }
            JsonSerializer.addDeserializer<AndConditionDecorator, JsonObject> {
                val inst = AndConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.decorators = it.getArray("elements").map { e ->
                    val id = e.asJsonObject.getIdentifier("id")
                    ConditionDecorators.registry[id]!!.deserializer.apply(id, e.asJsonObject)
                }.toTypedArray()
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = AndConditionDecorator()
                inst.id = buf.readIdentifier()
                val eLen = buf.readByte().toInt()
                inst.decorators = Array(eLen) {
                    val id = buf.readIdentifier()
                    return@Array ConditionDecorators.registry2[id]!!.deserializer.apply(buf)
                }

                return@addReceiver inst
            }
        }
        private fun init_OrConditionDecorator() {
            JsonSerializer.addSerializer<OrConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.add("elements", it.decorators.map { e ->
                    ConditionDecorators.registry[e.id]!!.serializer.applyObj(e)
                }.toJsonArray())
                return@addSerializer obj
            }
            PacketSender.addSender<OrConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeByte(inst.decorators.size)
                inst.decorators.forEach { e ->
                    buf.writeIdentifier(e.id)
                    ConditionDecorators.registry2[e.id]!!.serializer.applyObj(e, buf)
                }
            }
            JsonSerializer.addDeserializer<OrConditionDecorator, JsonObject> {
                val inst = OrConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.decorators = it.getArray("elements").map { e ->
                    val id = e.asJsonObject.getIdentifier("id")
                    ConditionDecorators.registry[id]!!.deserializer.apply(id, e.asJsonObject)
                }.toTypedArray()
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = OrConditionDecorator()
                inst.id = buf.readIdentifier()
                val eLen = buf.readByte().toInt()
                inst.decorators = Array(eLen) {
                    val id = buf.readIdentifier()
                    return@Array ConditionDecorators.registry2[id]!!.deserializer.apply(buf)
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
        private fun init_IsDayConditionDecorator() {
            JsonSerializer.addSerializer<IsDayConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                return@addSerializer obj
            }
            PacketSender.addSender<IsDayConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
            }
            JsonSerializer.addDeserializer<IsDayConditionDecorator, JsonObject> {
                val inst = IsDayConditionDecorator()
                inst.id = it.getIdentifier("id")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = IsDayConditionDecorator()
                inst.id = buf.readIdentifier()
                return@addReceiver inst
            }
        }
        private fun init_IsNightConditionDecorator() {
            JsonSerializer.addSerializer<IsNightConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                return@addSerializer obj
            }
            PacketSender.addSender<IsNightConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
            }
            JsonSerializer.addDeserializer<IsNightConditionDecorator, JsonObject> {
                val inst = IsNightConditionDecorator()
                inst.id = it.getIdentifier("id")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = IsNightConditionDecorator()
                inst.id = buf.readIdentifier()
                return@addReceiver inst
            }
        }
        private fun init_IsSkyVisibleConditionDecorator() {
            JsonSerializer.addSerializer<IsSkyVisibleConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                return@addSerializer obj
            }
            PacketSender.addSender<IsSkyVisibleConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
            }
            JsonSerializer.addDeserializer<IsSkyVisibleConditionDecorator, JsonObject> {
                val inst = IsSkyVisibleConditionDecorator()
                inst.id = it.getIdentifier("id")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = IsSkyVisibleConditionDecorator()
                inst.id = buf.readIdentifier()
                return@addReceiver inst
            }
        }
        private fun init_LightLevelConditionDecorator() {
            JsonSerializer.addSerializer<LightLevelConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("operator", it.operator)
                obj.addProperty("compared", it.value)
                return@addSerializer obj
            }
            PacketSender.addSender<LightLevelConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeEnumConstant(inst.operator)
                buf.writeByte(inst.value)
            }
            JsonSerializer.addDeserializer<LightLevelConditionDecorator, JsonObject> {
                val inst = LightLevelConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.operator = it.asEnum("operator")
                inst.value = it.getInt("compared")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = LightLevelConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.operator = buf.readEnumConstant(ComparisonOperator::class.java)
                inst.value = buf.readByte().toInt()
                return@addReceiver inst
            }
        }
        private fun init_BiomeTemperatureConditionDecorator() {
            JsonSerializer.addSerializer<BiomeTemperatureConditionDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("operator", it.operator)
                obj.addProperty("compared", it.value)
                return@addSerializer obj
            }
            PacketSender.addSender<BiomeTemperatureConditionDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeEnumConstant(inst.operator)
                buf.writeFloat(inst.value)
            }
            JsonSerializer.addDeserializer<BiomeTemperatureConditionDecorator, JsonObject> {
                val inst = BiomeTemperatureConditionDecorator()
                inst.id = it.getIdentifier("id")
                inst.operator = it.asEnum("operator")
                inst.value = it.getFloat("compared")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = BiomeTemperatureConditionDecorator()
                inst.id = buf.readIdentifier()
                inst.operator = buf.readEnumConstant(ComparisonOperator::class.java)
                inst.value = buf.readFloat()
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
                obj.addProperty("amplifier", it.amplifier)
                return@addSerializer obj
            }
            PacketSender.addSender<StatusEffectRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeIdentifier(inst.statusEffectId)
                buf.writeByte(inst.amplifier)
            }
            JsonSerializer.addDeserializer<StatusEffectRuleDecorator, JsonObject> {
                val inst = StatusEffectRuleDecorator()
                inst.id = it.getIdentifier("id")
                inst.statusEffectId = it.getIdentifier("effect")
                inst.amplifier = it.getByte("amplifier").toInt()
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = StatusEffectRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.statusEffectId = buf.readIdentifier()
                inst.amplifier = buf.readByte().toInt()
                return@addReceiver inst
            }
        }
        private fun init_BeeflyRuleDecorator() {
            JsonSerializer.addSerializer<BeeflyRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("minSpeed", it.minSpeed)
                obj.addProperty("maxSpeed", it.maxSpeed)
                obj.addProperty("startAt", it.flyStart)
                return@addSerializer obj
            }
            PacketSender.addSender<BeeflyRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeFloat(inst.minSpeed)
                buf.writeFloat(inst.maxSpeed)
                buf.writeShort(inst.flyStart)
            }
            JsonSerializer.addDeserializer<BeeflyRuleDecorator, JsonObject> {
                val inst = BeeflyRuleDecorator()
                inst.id = it.getIdentifier("id")
                inst.minSpeed = it.getFloat("minSpeed")
                inst.maxSpeed = it.getFloat("maxSpeed")
                inst.flyStart = it.getInt("startAt")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = BeeflyRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.minSpeed = buf.readFloat()
                inst.maxSpeed = buf.readFloat()
                inst.flyStart = buf.readShort().toInt()
                return@addReceiver inst
            }
        }
        private fun init_NoteTickRuleDecorator() {
            JsonSerializer.addSerializer<NoteTickRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                return@addSerializer obj
            }
            PacketSender.addSender<NoteTickRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
            }
            JsonSerializer.addDeserializer<NoteTickRuleDecorator, JsonObject> {
                val inst = NoteTickRuleDecorator()
                inst.id = it.getIdentifier("id")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = NoteTickRuleDecorator()
                inst.id = buf.readIdentifier()
                return@addReceiver inst
            }
        }
        private fun init_MagmaticJumpRuleDecorator() {
            JsonSerializer.addSerializer<MagmaticJumpRuleDecorator, JsonObject> {
                val obj = JsonObject()
                obj.addProperty("id", it.id)
                obj.addProperty("ready", it.ready)
                obj.addProperty("max", it.max)
                return@addSerializer obj
            }
            PacketSender.addSender<MagmaticJumpRuleDecorator> { inst, buf ->
                buf.writeIdentifier(inst.id)
                buf.writeInt(inst.ready)
                buf.writeInt(inst.max)
            }
            JsonSerializer.addDeserializer<MagmaticJumpRuleDecorator, JsonObject> {
                val inst = MagmaticJumpRuleDecorator()
                inst.id = it.getIdentifier("id")
                inst.ready = it.getInt("ready")
                inst.max = it.getInt("max")
                return@addDeserializer inst
            }
            PacketSender.addReceiver { buf ->
                val inst = MagmaticJumpRuleDecorator()
                inst.id = buf.readIdentifier()
                inst.ready = buf.readInt()
                inst.max = buf.readInt()
                return@addReceiver inst
            }
        }
    }
}