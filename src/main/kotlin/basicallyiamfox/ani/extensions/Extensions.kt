package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.TransformationManager
import basicallyiamfox.ani.core.ability.AbilityManager
import basicallyiamfox.ani.packet.Networking

fun getClientTransformationManager(): TransformationManager {
    return Networking.ThisClassExistsOnClientOnly.instance.transformations!!
}
fun getClientAbilityManager(): AbilityManager {
    return Networking.ThisClassExistsOnClientOnly.instance.abilities!!
}