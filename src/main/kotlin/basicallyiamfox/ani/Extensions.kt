package basicallyiamfox.ani

import basicallyiamfox.ani.interfaces.IDataPackContents
import basicallyiamfox.ani.packet.Networking
import basicallyiamfox.ani.server.ServerAbilityLoader
import basicallyiamfox.ani.server.ServerTransformationLoader
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.AbilityManager
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.client.MinecraftClient
import net.minecraft.server.DataPackContents
import net.minecraft.server.MinecraftServer

fun DataPackContents.getTransformationLoader(): ServerTransformationLoader {
    return (this as IDataPackContents).getServerTransformationLoader()
}
fun MinecraftServer.getTransformationLoader(): ServerTransformationLoader {
    return this.resourceManagerHolder.dataPackContents.getTransformationLoader()
}
fun DataPackContents.getAbilityLoader(): ServerAbilityLoader {
    return (this as IDataPackContents).getServerAbilityLoader()
}
fun MinecraftServer.getAbilityLoader(): ServerAbilityLoader {
    return this.resourceManagerHolder.dataPackContents.getAbilityLoader()
}

fun MinecraftClient.getTransformationManager(): TransformationManager {
    return Networking.Companion.ThisClassExistsOnClientOnly.instance.transformations!!
}
fun MinecraftClient.getAbilityManager(): AbilityManager {
    return Networking.Companion.ThisClassExistsOnClientOnly.instance.abilities!!
}

fun Iterable<JsonElement>.toJsonArray(): JsonArray {
    val array = JsonArray()
    forEach {
        array.add(it)
    }
    return array
}
fun <TType : MutableCollection<TValue>, TValue> TType.addSelf(value: TValue): TType {
    this.add(value)
    return this
}