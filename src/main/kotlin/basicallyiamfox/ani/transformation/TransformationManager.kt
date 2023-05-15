package basicallyiamfox.ani.transformation

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class TransformationManager {
    var map: MutableMap<Identifier, Transformation> = hashMapOf()
    var typeByItemId: MutableMap<Identifier, Transformation> = hashMapOf()

    fun load(map: Map<Identifier, Transformation>) {
        map.forEach { (t, u) ->
            this.map[t] = u
            typeByItemId[u.itemId] = u
        }
    }

    fun get(): Iterable<Transformation> {
        return map.values
    }

    fun get(id: Identifier): Transformation? = map[id]
    fun get(item: Item): Transformation? = typeByItemId[Registries.ITEM.getId(item)]
}