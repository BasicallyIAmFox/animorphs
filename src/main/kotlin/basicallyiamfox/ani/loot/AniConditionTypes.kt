package basicallyiamfox.ani.loot

import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.JsonSerializer

object AniConditionTypes {
    @JvmField
    val MAGMA_JELLY = register("magma_jelly", MagmaJellyCondition.Serializer())
    @JvmField
    val STINGER_O_POLLEN = register("stinger_o_pollen", StingerOPollenCondition.Serializer())

    fun init() {
    }

    private fun register(id: String, serializer: JsonSerializer<out LootCondition?>): LootConditionType {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, Identifier("animorphs", id), LootConditionType(serializer))
    }
}