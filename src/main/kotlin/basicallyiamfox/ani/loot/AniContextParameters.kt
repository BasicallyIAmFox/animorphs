package basicallyiamfox.ani.loot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.loot.context.LootContextParameter
import net.minecraft.util.Identifier

object AniContextParameters {
    @JvmField
    val THIS_PLAYER: LootContextParameter<PlayerEntity?> = register("animorphs:this_player")

    fun init() {
    }

    private fun <T> register(name: String): LootContextParameter<T?> = LootContextParameter(Identifier(name))
}