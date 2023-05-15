package basicallyiamfox.ani.transformation.rule

import net.minecraft.util.Identifier

class RuleManager {
    private lateinit var map: MutableMap<Identifier, Rule>

    fun load(map: Map<Identifier, Rule>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(id: Identifier): Rule? {
        return map[id]
    }
}