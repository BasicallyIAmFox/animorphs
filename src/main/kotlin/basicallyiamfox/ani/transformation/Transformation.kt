package basicallyiamfox.ani.transformation

import basicallyiamfox.ani.getAbilityManager
import basicallyiamfox.ani.transformation.condition.Condition
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class Transformation {
    val abilities = arrayListOf<Identifier>()
    val conditions = arrayListOf<Condition>()

    lateinit var id: Identifier
        private set
    lateinit var skin: Identifier
        private set
    lateinit var skinSlim: Identifier
        private set
    lateinit var itemId: Identifier
        private set
    var desc: List<String>? = null
        private set
    var color: Int? = null
        private set

    fun tick(player: ServerPlayerEntity) {
        if (conditions.all { condition -> condition.isActive(player) }) {
            abilities.map { getAbilityManager().get(it) }.forEach {
                it!!.tick(player)
            }
        }
    }

    fun addAbilities(abilities: Iterable<Identifier>): Transformation {
        this.abilities.addAll(abilities)
        return this
    }
    fun addConditions(conditions: Iterable<Condition>): Transformation {
        this.conditions.addAll(conditions)
        return this
    }

    fun setId(id: Identifier): Transformation {
        this.id = id
        return this
    }
    fun setSkin(skin: Identifier): Transformation {
        this.skin = skin
        return this
    }
    fun setSlim(skinSlim: Identifier): Transformation {
        this.skinSlim = skinSlim
        return this
    }
    fun setItem(item: Item): Transformation {
        itemId = Registries.ITEM.getId(item)
        return this
    }
    fun setColor(color: Int): Transformation {
        this.color = color
        return this
    }
    fun setDesc(keys: Iterable<String>): Transformation {
        desc = keys.toList()
        return this
    }
}