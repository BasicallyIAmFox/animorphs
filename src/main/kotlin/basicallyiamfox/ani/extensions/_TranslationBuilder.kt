package basicallyiamfox.ani.extensions

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.util.Identifier

fun FabricLanguageProvider.TranslationBuilder.addTransDesc(transId: Identifier, value: String) {
    add("animorphs.transformation.animorphs.desc.${transId.path}", value)
}
fun FabricLanguageProvider.TranslationBuilder.addTransDesc(transId: Identifier, values: Iterable<String>) {
    values.withIndex().forEach {
        add("animorphs.transformation.animorphs.desc.${transId.path}.${it.index}", it.value)
    }
}

fun FabricLanguageProvider.TranslationBuilder.addAbilityName(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.name.${abilityId.path}", value)
}
fun FabricLanguageProvider.TranslationBuilder.addAbilityDesc(abilityId: Identifier, value: String) {
    add("animorphs.ability.animorphs.desc.${abilityId.path}", value)
}
fun FabricLanguageProvider.TranslationBuilder.addAbilityDesc(abilityId: Identifier, values: Iterable<String>) {
    values.withIndex().forEach {
        add("animorphs.transformation.animorphs.desc.${abilityId.path}.${it.index}", it.value)
    }
}