package basicallyiamfox.ani.datagen

import basicallyiamfox.ani.core.ability.Ability
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.util.Identifier
import java.util.function.Consumer

abstract class AnimorphsAbilityProvider(output: FabricDataOutput) : FabricGenericProvider<Ability>("animorphs/abilities", output) {
    abstract fun generateAbilities(consumer: Consumer<Ability?>)

    override fun generateTypes(consumer: Consumer<Ability?>?) = generateAbilities(consumer!!)
    override fun getId(type: Ability): Identifier = type.id
    override fun toJson(type: Ability): JsonObject {
        val obj = JsonObject()
        Ability.Serializer.toJson(obj, type)
        return obj
    }
    override fun getName(): String? = "Animorphs/Abilities"
}