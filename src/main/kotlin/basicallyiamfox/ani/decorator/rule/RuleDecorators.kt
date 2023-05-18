package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

object RuleDecorators {
    private val statusEffectFunctions = register(StatusEffectRuleDecorator.ID, StatusEffectRuleDecorator.Serializer)
    private val immuneDamageTypeFunctions = register(ImmuneDamageTypeRuleDecorator.ID, ImmuneDamageTypeRuleDecorator.Serializer)
    private val beeflyFunctions = register(BeeflyRuleDecorator.ID, BeeflyRuleDecorator.Serializer)
    private val noteTickFunctions = register(NoteTickRuleDecorator.ID, NoteTickRuleDecorator.Serializer)
    private val magmaticJumpFunctions = register(MagmaticJumpRuleDecorator.ID, MagmaticJumpRuleDecorator.Serializer)
    private val modifyAirGenerationFunctions = register(ModifyAirGenerationRuleDecorator.ID, ModifyAirGenerationRuleDecorator.Serializer)
    private val modifyDamageReceivedFunctions = register(ModifyDamageReceivedRuleDecorator.ID, ModifyDamageReceivedRuleDecorator.Serializer)
    private val playSoundFunctions = register(PlaySoundRuleDecorator.ID, PlaySoundRuleDecorator.Serializer)

    @JvmStatic
    fun effectDecorator(statusEffect: StatusEffect): StatusEffectRuleDecorator =
        StatusEffectRuleDecorator(statusEffect)

    @JvmStatic
    fun immuneDamageTypeDecorator(damageType: RegistryKey<DamageType>): ImmuneDamageTypeRuleDecorator =
        ImmuneDamageTypeRuleDecorator(damageType)

    private fun <T : ISerializer<A>, A : RuleDecorator> register(id: Identifier, serializer: T): T {
        TypeSerializers.register(id, serializer)
        return serializer
    }

    fun init() {
    }
}