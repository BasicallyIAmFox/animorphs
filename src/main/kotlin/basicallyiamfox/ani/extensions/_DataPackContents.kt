package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.ServerTransformationLoader
import basicallyiamfox.ani.core.ability.ServerAbilityLoader
import basicallyiamfox.ani.interfaces.IDataPackContents
import net.minecraft.server.DataPackContents

fun DataPackContents.getTransformationLoader(): ServerTransformationLoader {
    return (this as IDataPackContents).getServerTransformationLoader()
}
fun DataPackContents.getAbilityLoader(): ServerAbilityLoader {
    return (this as IDataPackContents).getServerAbilityLoader()
}