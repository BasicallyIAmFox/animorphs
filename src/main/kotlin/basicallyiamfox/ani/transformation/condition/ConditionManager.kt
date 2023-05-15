package basicallyiamfox.ani.transformation.condition

import net.minecraft.util.Identifier

class ConditionManager {
    private lateinit var map: MutableMap<Identifier, Condition>

    fun load(map: Map<Identifier, Condition>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(id: Identifier): Condition? {
        return map[id]
    }
}