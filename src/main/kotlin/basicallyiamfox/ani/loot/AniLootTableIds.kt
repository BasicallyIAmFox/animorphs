package basicallyiamfox.ani.loot

import basicallyiamfox.ani.item.AnimorphsItems
import net.minecraft.registry.Registries

class AniLootTableIds {
    companion object {
        @JvmField
        val STINGER_O_POLLEN = Registries.ITEM.getId(AnimorphsItems.STINGER_O_POLLEN)
        @JvmField
        val MAGMA_JELLY = Registries.ITEM.getId(AnimorphsItems.MAGMA_JELLY)
    }
}