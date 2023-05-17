package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.interfaces.IPlayerEntity
import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

class ImmuneDamageTypeRuleDecorator() : RuleDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:immune_damage_type")
    }

    lateinit var damageTypeId: Identifier

    constructor(damageType: RegistryKey<DamageType>) : this() {
        id = ID
        damageTypeId = damageType.value
    }

    override fun update(world: World, player: PlayerEntity) {
        (player as IPlayerEntity).damageTypesImmunities.add(damageTypeId)
    }
}