package basicallyiamfox.ani.transformation.rule

import basicallyiamfox.ani.json.JsonFunctions
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.packet.PacketFunctions
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.rule.decorator.ImmuneDamageTypeRuleDecorator
import basicallyiamfox.ani.transformation.rule.decorator.StatusEffectRuleDecorator
import basicallyiamfox.ani.util.Action
import basicallyiamfox.ani.util.Func
import com.google.gson.JsonObject
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import java.util.function.Function

class RuleDecorators {
    companion object {
        val key = RegistryKey.ofRegistry<JsonFunctions<out RuleDecorator, JsonObject>>(Identifier("animorphs:rule_decorator"))
        val registry = Registries.create(key) { null!! }
        val key2 = RegistryKey.ofRegistry<PacketFunctions<out RuleDecorator>>(Identifier("animorphs:rule_decorator_packet"))
        val registry2 = Registries.create(key2) { null!! }

        private inline fun <reified T : RuleDecorator> commonObj(): JsonFunctions<T, JsonObject> = object :
            JsonFunctions<T, JsonObject>(
                Action { inst, obj ->
                    obj.add("decorator", JsonSerializer.toJson<T, JsonObject>(inst))
                },
                Func { _, obj ->
                    return@Func JsonSerializer.fromJson<T, JsonObject>(obj)
                }
            ) { }
        private inline fun <reified T : RuleDecorator> commonObj2(): PacketFunctions<T> = object :
            PacketFunctions<T>(
                Action { inst, buf ->
                    PacketSender.toPacket(inst, buf)
                },
                Function { buf ->
                    return@Function PacketSender.fromPacket<T>(buf)
                }
            ) { }

        private val statusEffect = register(
            Identifier("animorphs:status_effect"),
            commonObj<StatusEffectRuleDecorator>(),
            commonObj2()
        )
        private val immuneDamageType = register(
            Identifier("animorphs:immune_damage_type"),
            commonObj<ImmuneDamageTypeRuleDecorator>(),
            commonObj2()
        )

        fun effectDecorator(statusEffect: StatusEffect): StatusEffectRuleDecorator {
            return StatusEffectRuleDecorator(statusEffect)
        }

        fun immuneDamageTypeDecorator(damageType: RegistryKey<DamageType>): ImmuneDamageTypeRuleDecorator {
            return ImmuneDamageTypeRuleDecorator(damageType)
        }

        private fun <T : JsonFunctions<A, JsonObject>, A : RuleDecorator> register(id: Identifier, jsonFunctions: T, packetFunctions: PacketFunctions<A>): T {
            Registry.register(registry, id, jsonFunctions)
            Registry.register(registry2, id, packetFunctions)
            return jsonFunctions
        }

        fun init() {
        }
    }
}