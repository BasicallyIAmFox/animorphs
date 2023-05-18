package basicallyiamfox.ani

import basicallyiamfox.ani.decorator.condition.ConditionDecorators
import basicallyiamfox.ani.decorator.rule.RuleDecorators
import basicallyiamfox.ani.item.AnimorphsItems
import basicallyiamfox.ani.loot.AniConditionTypes
import basicallyiamfox.ani.loot.AniContextParameters
import basicallyiamfox.ani.packet.Networking
import net.fabricmc.api.ModInitializer

object Animorphs : ModInitializer {
    override fun onInitialize() {
        RuleDecorators.init()
        ConditionDecorators.init()

        Networking.init()

        AnimorphsItems.init()

        AniConditionTypes.init()
        AniContextParameters.init()
        AnimorphsLootTables.listen()
    }
}