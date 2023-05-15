package basicallyiamfox.ani.transformation.rule

import basicallyiamfox.ani.transformation.condition.Condition
import net.minecraft.server.network.ServerPlayerEntity

class Rule {
    val conditions = arrayListOf<Condition>()

    var decorator: RuleDecorator? = null
        private set

    fun tick(player: ServerPlayerEntity) {
        if (conditions.all { condition -> condition.isActive(player) }) {
            decorator?.update(player)
        }
    }

    fun addConditions(conditions: Iterable<Condition>): Rule {
        this.conditions.addAll(conditions)
        return this
    }

    fun setDecorator(decorator: RuleDecorator): Rule {
        this.decorator = decorator
        return this
    }
}