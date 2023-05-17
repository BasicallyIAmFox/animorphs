package basicallyiamfox.ani.transformation.rule

import basicallyiamfox.ani.json.JsonFunctions
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.packet.PacketFunctions
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.rule.decorator.*
import basicallyiamfox.ani.util.Action
import basicallyiamfox.ani.util.Func2
import basicallyiamfox.ani.util.Func3
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
        val key: RegistryKey<Registry<JsonFunctions<out RuleDecorator, JsonObject>>> = RegistryKey.ofRegistry(Identifier("animorphs:rule_decorator"))
        val registry: Registry<JsonFunctions<out RuleDecorator, JsonObject>> = Registries.create(key) { null!! }
        val key2: RegistryKey<Registry<PacketFunctions<out RuleDecorator>>> = RegistryKey.ofRegistry(Identifier("animorphs:rule_decorator_packet"))
        val registry2: Registry<PacketFunctions<out RuleDecorator>> = Registries.create(key2) { null!! }

        private inline fun <reified T : RuleDecorator> commonObj(): JsonFunctions<T, JsonObject> = object :
            JsonFunctions<T, JsonObject>(
                Func2 { inst ->
                    return@Func2 JsonSerializer.toJson<T, JsonObject>(inst)
                },
                Func3 { _, obj ->
                    return@Func3 JsonSerializer.fromJson<T, JsonObject>(obj)
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

        private val statusEffect = register(StatusEffectRuleDecorator.ID, commonObj<StatusEffectRuleDecorator>(), commonObj2())
        private val immuneDamageType = register(ImmuneDamageTypeRuleDecorator.ID, commonObj<ImmuneDamageTypeRuleDecorator>(), commonObj2())
        private val beefly = register(BeeflyRuleDecorator.ID, commonObj<BeeflyRuleDecorator>(), commonObj2())
        private val noteTick = register(NoteTickRuleDecorator.ID, commonObj<NoteTickRuleDecorator>(), commonObj2())
        private val magmaticJump = register(MagmaticJumpRuleDecorator.ID, commonObj<MagmaticJumpRuleDecorator>(), commonObj2())

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