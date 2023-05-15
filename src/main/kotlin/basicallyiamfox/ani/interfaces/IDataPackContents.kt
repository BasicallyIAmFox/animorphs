package basicallyiamfox.ani.interfaces

import basicallyiamfox.ani.server.ServerAbilityLoader
import basicallyiamfox.ani.server.ServerTransformationLoader

interface IDataPackContents {
    fun getServerTransformationLoader(): ServerTransformationLoader
    fun getServerAbilityLoader(): ServerAbilityLoader
}