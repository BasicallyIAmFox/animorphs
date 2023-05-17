package basicallyiamfox.ani.item

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


class AnimorphsItems {
    companion object {
        private val ITEMS = LinkedHashMap<Identifier, Item>()

        @JvmField
        val STINGER_O_POLLEN: Item = add("stinger_o_pollen", StingerOPollenItem())
        @JvmField
        val MAGMA_JELLY: Item = add("magma_jelly", StingerOPollenItem())
        @JvmField
        val UNFINISHED_SYMPHONY: Item = add("unfinished_symphony", StingerOPollenItem())

        fun init() {
            for ((key, value) in ITEMS) {
                Registry.register(Registries.ITEM, key, value)
            }
        }

        private fun <I : Item> add(name: String, item: I): I {
            if (!ITEMS.containsKey(Identifier("animorphs", name))) {
                ITEMS[Identifier("animorphs", name)] = item
            } else {
                //AnimorphsMod.LOGGER.error("Some item with same Identifier was registered! ($name)")
            }
            return item
        }
    }
}