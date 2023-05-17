package basicallyiamfox.ani.loot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.loot.context.LootContextParameter
import net.minecraft.util.Identifier

class ContextParameters {
    companion object {
        @JvmField
        val THIS_PLAYER: LootContextParameter<PlayerEntity?> = register("animorphs:this_player")

        private fun <T> register(name: String): LootContextParameter<T?> = LootContextParameter(Identifier(name))
    }
}