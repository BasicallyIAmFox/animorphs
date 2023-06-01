package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.core.ServerTransformationLoader
import basicallyiamfox.ani.core.ability.ServerAbilityLoader
import basicallyiamfox.ani.mixin.IMinecraftServerMixin
import net.minecraft.server.MinecraftServer

fun MinecraftServer.resourceManagerHolder(): MinecraftServer.ResourceManagerHolder {
    return (this as IMinecraftServerMixin).resourceManagerHolder
}
val MinecraftServer.transformationLoader: ServerTransformationLoader
    get() = resourceManagerHolder().dataPackContents.transformationLoader
val MinecraftServer.abilityLoader: ServerAbilityLoader
    get() = resourceManagerHolder().dataPackContents.abilityLoader