package basicallyiamfox.ani

import basicallyiamfox.ani.packet.Networking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object AnimorphsClmod : ClientModInitializer {
    override fun onInitializeClient() {
        Networking.clinit()

        AnimorphsKeybindings.init()
    }
}