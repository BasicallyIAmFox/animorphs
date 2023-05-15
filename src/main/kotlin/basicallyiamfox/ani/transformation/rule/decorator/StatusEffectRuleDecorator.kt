package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class StatusEffectRuleDecorator() : RuleDecorator() {
    lateinit var statusEffectId: Identifier

    constructor(statusEffect: StatusEffect) : this() {
        id = Identifier("animorphs:status_effect")
        statusEffectId = Registries.STATUS_EFFECT.getId(statusEffect)!!
    }
}