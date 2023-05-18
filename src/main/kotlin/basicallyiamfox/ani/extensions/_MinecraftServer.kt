package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.ServerTransformationLoader
import basicallyiamfox.ani.core.ability.ServerAbilityLoader
import basicallyiamfox.ani.mixin.IMinecraftServerMixin
import net.minecraft.server.MinecraftServer

fun MinecraftServer.resourceManagerHolder(): MinecraftServer.ResourceManagerHolder {
    return (this as IMinecraftServerMixin).resourceManagerHolder
}
fun MinecraftServer.getTransformationLoader(): ServerTransformationLoader {
    return resourceManagerHolder().dataPackContents.getTransformationLoader()
}
fun MinecraftServer.getAbilityLoader(): ServerAbilityLoader {
    return resourceManagerHolder().dataPackContents.getAbilityLoader()
}