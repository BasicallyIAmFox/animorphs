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
        override fun generateTranslations(translationBuilder: TranslationBuilder?) {
            translationBuilder!!.add(AnimorphsItems.STINGER_O_POLLEN, "Stinger o' Pollen")
            translationBuilder.add(AnimorphsItems.MAGMA_JELLY, "Magma Jelly")

            translationBuilder.addAbility(Identifier("animorphs:beefly"), "Beefly")
            translationBuilder.addAbilityDesc(Identifier("animorphs:beefly"), "Allows to fly while holding [SPACE] button.")
            translationBuilder.addAbility(Identifier("animorphs:soft_wings"), "Soft Wings")
            translationBuilder.addAbilityDesc(Identifier("animorphs:soft_wings"), "You drown much faster.")
            translationBuilder.addAbility(Identifier("animorphs:magmatic_jump"), "Magmatic Jump")
            translationBuilder.addAbilityDesc(Identifier("animorphs:magmatic_jump"), "Holding [SHIFT] long enough will allow to jump high.")
            translationBuilder.addAbility(Identifier("animorphs:wet_obsidian"), "Wet Obsidian")
            translationBuilder.addAbilityDesc(Identifier("animorphs:wet_obsidian"), "You take damage from touching water.")

            translationBuilder.addAbility(Identifier("animorphs:immune_to_fall_damage"), "Negates fall damage")

            translationBuilder.add("animorphs.tooltip.animorphs.hold_shift_to_show", "Hold [SHIFT] to show abilities.")
            translationBuilder.add("animorphs.tooltip.animorphs.is_visual_active", "Is Visual Active:")
            translationBuilder.add("animorphs.ability_sign.animorphs.positive", "[+]")
            translationBuilder.add("animorphs.ability_sign.animorphs.neutral", "[=]")
            translationBuilder.add("animorphs.ability_sign.animorphs.negative", "[-]")
        }
    }
    class TransformationGenerator(output: FabricDataOutput) : FabricGenericProvider<Transformation>("animorphs/transformations", output) {
        override fun generateTypes(consumer: Consumer<Transformation?>?) {
            Consumer<Consumer<Transformation?>?> { t ->
                t.accept(Transformation()
                    .setColor(Color(238, 196, 65).rgb)
                    .setId(Identifier("animorphs:bee"))
                    .setSkin(Identifier("animorphs:transformations/bee"))
                    .setSlim(Identifier("animorphs:transformations/bee_slim"))
                    .setItem(AnimorphsItems.STINGER_O_POLLEN)
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:beefly"))
                            .addSelf(Identifier("animorphs:jump_boost_status_effect"))
                            .addSelf(Identifier("animorphs:immune_to_fall_damage"))
                            .addSelf(Identifier("animorphs:soft_wings"))
                    )
                    .addConditions(
                        arrayListOf<Condition>()
                            .addSelf(Condition().setDecorator(ConditionDecorators.duringDayTime))
                    )
                )

                t.accept(Transformation()
                    .setColor(Color(197, 93, 24).rgb)
                    .setId(Identifier("animorphs:magma_cube"))
                    .setSkin(Identifier("animorphs:transformations/magmacube"))
                    .setSlim(Identifier("animorphs:transformations/magmacube"))
                    .setItem(AnimorphsItems.MAGMA_JELLY)
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:magmatic_jump"))
                            .addSelf(Identifier("animorphs:fire_protection_status_effect"))
                            .addSelf(Identifier("animorphs:immune_to_fall_damage"))
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
                t.accept(Ability()
                    .setId(Identifier("animorphs:beefly"))
                    .setName("animorphs.ability.animorphs.name.beefly")
                    .setColor(Color(235, 194, 64))
                    .setSign(Ability.Sign.POSITIVE)
                    .setDesc(arrayListOf<String>().addSelf("animorphs.ability.animorphs.desc.beefly"))
                    .addRules(
                        arrayListOf<Rule>()
                    )
                )
                t.accept(Ability()
                    .setId(Identifier("animorphs:soft_wings"))
                    .setName("animorphs.ability.animorphs.name.soft_wings")
                    .setColor(Color(217, 231, 252))
                    .setSign(Ability.Sign.NEGATIVE)
                    .setDesc(arrayListOf<String>().addSelf("animorphs.ability.animorphs.desc.soft_wings"))
                    .addRules(
                        arrayListOf<Rule>()
                    )
                )

                t.accept(Ability()
                    .setId(Identifier("animorphs:magmatic_jump"))
                    .setName("animorphs.ability.animorphs.name.magmatic_jump")
                    .setColor(Color(195, 92, 24))
                    .setSign(Ability.Sign.POSITIVE)
                    .setDesc(arrayListOf<String>().addSelf("animorphs.ability.animorphs.desc.magmatic_jump"))
                    .addRules(
                        arrayListOf<Rule>()
                    )
                )
                t.accept(Ability()
                    .setId(Identifier("animorphs:wet_obsidian"))
                    .setName("animorphs.ability.animorphs.name.wet_obsidian")
                    .setColor(Color(97, 77, 97))
                    .setSign(Ability.Sign.NEGATIVE)
                    .setDesc(arrayListOf<String>().addSelf("animorphs.ability.animorphs.desc.wet_obsidian"))
                    .addRules(
                        arrayListOf<Rule>()
                    )
                )

                (DamageTypes::class.java.declaredFields).forEach { e ->
                    if (!Modifier.isStatic(e.modifiers) && e.canAccess(null))
                        return@forEach

                    val damageType = (e.get(null) as RegistryKey<DamageType>)
                    t.accept(Ability()
                        .setId(Identifier("animorphs:immune_to_${damageType.value.path}_damage"))
                        .setName("animorphs.ability.animorphs.name.immune_to_${damageType.value.path}_damage")
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