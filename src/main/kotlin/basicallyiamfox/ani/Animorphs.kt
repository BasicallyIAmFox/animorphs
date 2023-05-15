package basicallyiamfox.ani

import basicallyiamfox.ani.item.AnimorphsItems
import basicallyiamfox.ani.json.Serializers
import basicallyiamfox.ani.packet.Networking
import basicallyiamfox.ani.transformation.condition.ConditionDecorators
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Animorphs : ModInitializer {
    private val logger = LoggerFactory.getLogger("animorphs")

    override fun onInitialize() {
        Serializers.init()

        RuleDecorators.init()
        ConditionDecorators.init()

        Networking.init()

        AnimorphsItems.init()
    }
}