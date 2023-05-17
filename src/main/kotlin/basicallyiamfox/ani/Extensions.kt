package basicallyiamfox.ani

import basicallyiamfox.ani.interfaces.IDataPackContents
import basicallyiamfox.ani.interfaces.IPlayerEntity
import basicallyiamfox.ani.item.TransformationItem
import basicallyiamfox.ani.packet.Networking
import basicallyiamfox.ani.server.ServerAbilityLoader
import basicallyiamfox.ani.server.ServerTransformationLoader
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.AbilityManager
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.DataPackContents
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import kotlin.math.abs


fun TranslationBuilder.addAbility(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.name.${abilityId.path}", value)
}
fun TranslationBuilder.addAbilityDesc(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.desc.${abilityId.path}", value)
}
fun TranslationBuilder.addAbilityDesc(abilityId: Identifier, values: Iterable<String>) {
    values.withIndex().forEach {
        add("animorphs.transformation.animorphs.desc.${abilityId.path}.${it.index}", it.value)
    }
}
fun TranslationBuilder.addTransDesc(transId: Identifier, value: String) {
    add("animorphs.transformation.animorphs.desc.${transId.path}", value)
}
fun TranslationBuilder.addTransDesc(transId: Identifier, values: Iterable<String>) {
    values.withIndex().forEach {
        add("animorphs.transformation.animorphs.desc.${transId.path}.${it.index}", it.value)
    }
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

fun LivingEntity.getTransformationManager(): TransformationManager? {
    if (world == null)
        return null

    if (world.isClient) {
        return getClientTransformationManager()
    }
    else if (server != null) {
        return server!!.getTransformationLoader().manager
    }
    return null
}
fun LivingEntity.getAbilityManager(): AbilityManager? {
    if (world == null)
        return null

    if (world.isClient) {
        return getClientAbilityManager()
    }
    else if (server != null) {
        return server!!.getAbilityLoader().manager
    }
    return null
}
fun getClientTransformationManager(): TransformationManager {
    return Networking.Companion.ThisClassExistsOnClientOnly.instance.transformations!!
}
fun getClientAbilityManager(): AbilityManager {
    return Networking.Companion.ThisClassExistsOnClientOnly.instance.abilities!!
}
fun PlayerEntity.isVisualActive(): Boolean {
    val duck = this as IPlayerEntity
    val item: ItemStack = duck.transformationItem ?: return false
    return item.orCreateNbt.getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)
}
fun LivingEntity.isMoving(): Boolean {
    val absX = abs(velocity.x)
    val absY = abs(velocity.y)
    val absZ = abs(velocity.z)
    return absX + absY + absZ >= getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
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