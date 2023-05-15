package basicallyiamfox.ani.transformation.ability

import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.rule.Rule
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.awt.Color

class Ability {
    enum class Sign(val text: String, val colorHex: Int) {
        POSITIVE("animorphs.ability_sign.animorphs.positive", 0x0FBE19), // 15, 190, 25
        NEUTRAL("animorphs.ability_sign.animorphs.neutral", 0xBEBE28),   // 190, 190, 40
        NEGATIVE("animorphs.ability_sign.animorphs.negative", 0xBE0F19); // 190, 15, 25
    }

    val conditions = arrayListOf<Condition>()
    val rules = arrayListOf<Rule>()

    lateinit var id: Identifier
        private set
    lateinit var name: String
        private set
    var desc: List<String>? = null
        private set
    var sign = Sign.NEUTRAL
        private set
    var colorHex = 0xFFFFFF
        private set

    fun tick(player: ServerPlayerEntity) {
        if (conditions.all { condition -> condition.isActive(player) }) {
            rules.forEach {
                it.tick(player)
            }
        }
    }

    fun addConditions(conditions: Iterable<Condition>): Ability {
        this.conditions.addAll(conditions)
        return this
    }
    fun addRules(rules: Iterable<Rule>): Ability {
        this.rules.addAll(rules)
        return this
    }

    fun setId(id: Identifier): Ability {
        this.id = id
        return this
    }
    fun setName(key: String): Ability {
        name = key
        return this
    }
    fun setDesc(keys: Iterable<String>): Ability {
        desc = keys.toList()
        return this
    }
    fun setSign(sign: Sign): Ability {
        this.sign = sign
        return this
    }
    fun setColor(colorHex: Int): Ability {
        this.colorHex = colorHex
        return this
    }
    fun setColor(color: Color): Ability {
        colorHex = color.rgb
        return this
    }
}