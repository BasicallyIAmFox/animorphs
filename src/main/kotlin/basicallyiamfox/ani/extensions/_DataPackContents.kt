package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.ServerTransformationLoader
import basicallyiamfox.ani.core.ability.ServerAbilityLoader
import basicallyiamfox.ani.interfaces.IDataPackContents
import net.minecraft.server.DataPackContents

val DataPackContents.transformationLoader: ServerTransformationLoader
    get() = (this as IDataPackContents).serverTransformationLoader
val DataPackContents.abilityLoader: ServerAbilityLoader
    get() = (this as IDataPackContents).serverAbilityLoader