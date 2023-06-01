package basicallyiamfox.ani.interfaces

import basicallyiamfox.ani.core.ServerTransformationLoader
import basicallyiamfox.ani.core.ability.ServerAbilityLoader

interface IDataPackContents {
    val serverTransformationLoader: ServerTransformationLoader
    val serverAbilityLoader: ServerAbilityLoader
}