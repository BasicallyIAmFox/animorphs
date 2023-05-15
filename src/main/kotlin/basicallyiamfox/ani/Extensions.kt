package basicallyiamfox.ani

import basicallyiamfox.ani.interfaces.IDataPackContents
import basicallyiamfox.ani.packet.Networking
import basicallyiamfox.ani.server.ServerAbilityLoader
import basicallyiamfox.ani.server.ServerTransformationLoader
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.AbilityManager
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder
import net.minecraft.server.DataPackContents
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier

fun TranslationBuilder.addAbility(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.name.${abilityId.path}", value)
}
fun TranslationBuilder.addAbilityDesc(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.desc.${abilityId.path}", value)
}
fun TranslationBuilder.addAbilityDesc(abilityId: Identifier, index: Int, value: String) {
    add("animorphs.ability.animorphs.desc.${abilityId.path}.$index", value)
}

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

fun getTransformationManager(): TransformationManager {
    return Networking.Companion.ThisClassExistsOnClientOnly.instance.transformations!!
}
fun getAbilityManager(): AbilityManager {
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