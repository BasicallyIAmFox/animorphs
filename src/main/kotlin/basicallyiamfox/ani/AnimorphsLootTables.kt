package basicallyiamfox.ani

import basicallyiamfox.ani.loot.AniLootTableIds
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.entity.EntityType
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition
import net.minecraft.loot.condition.LootCondition
import net.minecraft.predicate.entity.DamageSourcePredicate
import net.minecraft.predicate.entity.EntityPredicate


object AnimorphsLootTables {
    private val MAGMA_CUBE_LOOT_TABLE_ID = EntityType.MAGMA_CUBE.lootTableId

    private fun killedByFrog(): LootCondition.Builder? {
        return DamageSourcePropertiesLootCondition.builder(
            DamageSourcePredicate.Builder.create()
                .sourceEntity(EntityPredicate.Builder.create().type(EntityType.FROG))
        )
    }

    fun listen() {
        LootTableEvents.MODIFY.register { _, lootManager, id, tableBuilder, source ->
            if (!source.isBuiltin)
                return@register;

            if (MAGMA_CUBE_LOOT_TABLE_ID.equals(id)) {
                lootManager.getLootTable(AniLootTableIds.MAGMA_JELLY).pools.forEach {
                    tableBuilder.pool(it).modifyPools { e ->
                        e.conditionally(killedByFrog())
                    }
                }
            }
        }
    }
}