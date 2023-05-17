package basicallyiamfox.ani.transformation.rule

import basicallyiamfox.ani.transformation.condition.Condition
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World

class Rule {
    val conditions = arrayListOf<Condition>()

    var decorator: RuleDecorator? = null
        private set

    fun tick(world: World, player: PlayerEntity) {
        if (conditions.all { condition -> condition.isActive(world, player) }) {
            decorator?.update(world, player)
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