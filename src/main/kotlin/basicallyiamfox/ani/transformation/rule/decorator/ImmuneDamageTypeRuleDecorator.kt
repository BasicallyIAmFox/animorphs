package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

class ImmuneDamageTypeRuleDecorator() : RuleDecorator() {
    lateinit var damageTypeId: Identifier

    constructor(damageType: RegistryKey<DamageType>) : this() {
        id = Identifier("animorphs:immune_damage_type")
        damageTypeId = damageType.value
    }
}