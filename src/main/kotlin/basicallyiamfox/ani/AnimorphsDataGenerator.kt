package basicallyiamfox.ani

import basicallyiamfox.ani.datagen.FabricGenericProvider
import basicallyiamfox.ani.item.AnimorphsItems
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.transformation.Transformation
import basicallyiamfox.ani.transformation.ability.Ability
import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.condition.ConditionDecorators
import basicallyiamfox.ani.transformation.rule.Rule
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.awt.Color
import java.lang.reflect.Modifier
import java.util.function.Consumer

class AnimorphsDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::AbilityGenerator)
        pack.addProvider(::TransformationGenerator)
        pack.addProvider(::LangGenerator)
        pack.addProvider(::ModelGenerator)
    }

    class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
        override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {
        }

        override fun generateItemModels(itemModelGenerator: ItemModelGenerator?) {
            itemModelGenerator!!.register(AnimorphsItems.STINGER_O_POLLEN, Models.GENERATED)
            itemModelGenerator.register(AnimorphsItems.MAGMA_JELLY, Models.GENERATED)
        }
    }
    class LangGenerator(output: FabricDataOutput) : FabricLanguageProvider(output, "en_us") {
        private lateinit var translationBuilder: TranslationBuilder

        override fun generateTranslations(translationBuilder: TranslationBuilder?) {
            this.translationBuilder = translationBuilder!!

            translationBuilder.add(AnimorphsItems.STINGER_O_POLLEN, "Stinger o' Pollen")
            translationBuilder.add(AnimorphsItems.MAGMA_JELLY, "Magma Jelly")

            addAbility("beefly", "Beefly")
            addAbility("magmatic_jump", "Magmatic jump")
            addAbility("soft_wings", "Soft Wings")
            addAbility("wet_obsidian", "Wet Obsidian")

            addAbility("negates_fall_damage", "Negates fall damage")
        }

        private fun addAbility(key: String, text: String) {
            translationBuilder.add("animorphs.ability.animorphs.$key", text)
        }
    }
    class TransformationGenerator(output: FabricDataOutput) : FabricGenericProvider<Transformation>("animorphs/transformations", output) {
        override fun generateTypes(consumer: Consumer<Transformation?>?) {
            Consumer<Consumer<Transformation?>?> { t ->
                t.accept(Transformation()
                    .setId(Identifier("animorphs:bee"))
                    .setSkin(Identifier("animorphs:transformations/bee"))
                    .setSlim(Identifier("animorphs:transformations/bee_slim"))
                    .setItem(AnimorphsItems.STINGER_O_POLLEN)
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:beefly"))
                            .addSelf(Identifier("animorphs:jump_boost"))
                            .addSelf(Identifier("animorphs:negate_fall_damage"))
                            .addSelf(Identifier("animorphs:soft_wings"))
                    )
                    .addConditions(
                        arrayListOf<Condition>()
                            .addSelf(Condition().setDecorator(ConditionDecorators.duringDayTime))
                    )
                )

                t.accept(Transformation()
                    .setId(Identifier("animorphs:magma_cube"))
                    .setSkin(Identifier("animorphs:transformations/magmacube"))
                    .setSlim(Identifier("animorphs:transformations/magmacube"))
                    .setItem(AnimorphsItems.MAGMA_JELLY)
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:magmatic_jump"))
                            .addSelf(Identifier("animorphs:jump_boost"))
                            .addSelf(Identifier("animorphs:negate_fall_damage"))
                            .addSelf(Identifier("animorphs:wet_obsidian"))
                    )
                    .addConditions(
                        arrayListOf<Condition>()
                            .addSelf(Condition().setDecorator(ConditionDecorators.inDimension(World.NETHER)))
                    )
                )

            }.accept(consumer)
        }

        override fun getId(type: Transformation): Identifier = type.id

        override fun toJson(type: Transformation): JsonObject = JsonSerializer.toJson(type)

        override fun getName(): String = "Animorphs/Transformations"
    }
    class AbilityGenerator(output: FabricDataOutput) : FabricGenericProvider<Ability>("animorphs/abilities", output) {
        override fun generateTypes(consumer: Consumer<Ability?>?) {
            Consumer<Consumer<Ability?>?> { t ->
                (DamageTypes::class.java.declaredFields).forEach { e ->
                    if (!Modifier.isStatic(e.modifiers) && e.canAccess(null))
                        return@forEach

                    val damageType = (e.get(null) as RegistryKey<DamageType>)
                    t.accept(Ability()
                        .setId(Identifier("animorphs:immune_to_${damageType.value.path}_damage"))
                        .setName("animorphs.ability.animorphs.${damageType.value.path}")
                        .setSign(Ability.Sign.POSITIVE)
                        .setColor(Color(200, 220, 255))
                        .addRules(
                            arrayListOf<Rule>()
                                .addSelf(Rule().setDecorator(RuleDecorators.immuneDamageTypeDecorator(damageType)))
                        )
                    )
                }

                Registries.STATUS_EFFECT.entrySet.forEach { e ->
                    t.accept(Ability()
                        .setId(Identifier("animorphs:${e.key.value.path}_status_effect"))
                        .setSign(if (e.value.isBeneficial) Ability.Sign.POSITIVE else Ability.Sign.NEGATIVE)
                        .setName(e.value.translationKey)
                        .setColor(e.value.color)
                        .addRules(
                            arrayListOf<Rule>()
                                .addSelf(Rule().setDecorator(RuleDecorators.effectDecorator(StatusEffects.JUMP_BOOST)))
                        )
                    )
                }

            }.accept(consumer)
        }

        override fun getId(type: Ability): Identifier = type.id

        override fun toJson(type: Ability): JsonObject = JsonSerializer.toJson(type)

        override fun getName(): String = "Animorphs/Abilities"
    }
}