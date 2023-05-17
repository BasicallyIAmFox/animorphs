package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.World

class StatusEffectRuleDecorator() : RuleDecorator() {
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:status_effect")
    }

    lateinit var statusEffectId: Identifier
    var amplifier: Int = 0

    constructor(statusEffect: StatusEffect) : this() {
        id = ID
        statusEffectId = Registries.STATUS_EFFECT.getId(statusEffect)!!
    }

    fun setAmplifier(amplifier: Int): StatusEffectRuleDecorator {
        this.amplifier = amplifier
        return this
    }

    override fun update(world: World, player: PlayerEntity) {
        if (!world.isClient) {
            player.addStatusEffect(StatusEffectInstance(Registries.STATUS_EFFECT.get(statusEffectId), 20, amplifier, true, false, true))
        }
    }
}
