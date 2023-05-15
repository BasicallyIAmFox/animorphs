package basicallyiamfox.ani.transformation.ability

import net.minecraft.util.Identifier

class AbilityManager {
    lateinit var map: MutableMap<Identifier, Ability>

    fun load(map: Map<Identifier, Ability>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(): Iterable<Ability> {
        return map.values
    }

    fun get(id: Identifier): Ability? {
        return map[id]
    }
}