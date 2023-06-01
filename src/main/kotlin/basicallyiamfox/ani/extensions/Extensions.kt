package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.TransformationManager
import basicallyiamfox.ani.core.ability.AbilityManager
import basicallyiamfox.ani.packet.Networking

val clientTransformationManager: TransformationManager
    get() = Networking.ThisClassExistsOnClientOnly.instance.transformations!!
val clientAbilityManager: AbilityManager
    get() = Networking.ThisClassExistsOnClientOnly.instance.abilities!!