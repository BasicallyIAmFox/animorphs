package basicallyiamfox.ani

import basicallyiamfox.ani.datagen.FabricGenericProvider
import basicallyiamfox.ani.item.AnimorphsItems
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.loot.AniLootTableIds
import basicallyiamfox.ani.loot.MagmaJellyCondition
import basicallyiamfox.ani.loot.StingerOPollenCondition
import basicallyiamfox.ani.transformation.Transformation
import basicallyiamfox.ani.transformation.ability.Ability
import basicallyiamfox.ani.transformation.condition.Condition
import basicallyiamfox.ani.transformation.condition.ConditionDecorators
import basicallyiamfox.ani.transformation.condition.decorator.AndConditionDecorator
import basicallyiamfox.ani.transformation.condition.decorator.BiomeTemperatureConditionDecorator
import basicallyiamfox.ani.transformation.condition.decorator.LightLevelConditionDecorator
import basicallyiamfox.ani.transformation.condition.decorator.OrConditionDecorator
import basicallyiamfox.ani.transformation.rule.Rule
import basicallyiamfox.ani.transformation.rule.RuleDecorators
import basicallyiamfox.ani.transformation.rule.decorator.BeeflyRuleDecorator
import basicallyiamfox.ani.transformation.rule.decorator.MagmaticJumpRuleDecorator
import basicallyiamfox.ani.transformation.rule.decorator.NoteTickRuleDecorator
import basicallyiamfox.ani.util.ComparisonOperator
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.awt.Color
import java.lang.reflect.Modifier
import java.util.function.BiConsumer
import java.util.function.Consumer

class AnimorphsDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::AbilityGenerator)
        pack.addProvider(::TransformationGenerator)
        pack.addProvider(::LangGenerator)
        pack.addProvider(::ModelGenerator)
        pack.addProvider(::RecipeGenerator)
        pack.addProvider(::LootTableGenerator)
    }

    class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
        override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {
        }

        override fun generateItemModels(itemModelGenerator: ItemModelGenerator?) {
            itemModelGenerator!!.register(AnimorphsItems.STINGER_O_POLLEN, Models.GENERATED)
            itemModelGenerator.register(AnimorphsItems.MAGMA_JELLY, Models.GENERATED)
            itemModelGenerator.register(AnimorphsItems.UNFINISHED_SYMPHONY, Models.GENERATED)
        }
    }
    class LangGenerator(output: FabricDataOutput) : FabricLanguageProvider(output, "en_us") {
        override fun generateTranslations(translationBuilder: TranslationBuilder?) {
            translationBuilder!!.add(AnimorphsItems.STINGER_O_POLLEN, "Stinger o' Pollen")
            translationBuilder.addTransDesc(
                Identifier("animorphs:bee"),
                arrayListOf<String>()
                    .addSelf("Turns you into a werebee when in light.")
                    .addSelf("Grants ability to slowly fly up.")
                    .addSelf("Also increases your stats when werebee.")
            )
            translationBuilder.add(AnimorphsItems.MAGMA_JELLY, "Magma Jelly")
            translationBuilder.addTransDesc(
                Identifier("animorphs:magma_cube"),
                arrayListOf<String>()
                    .addSelf("Turns you into a magma cube when in Nether.")
                    .addSelf("Holding [SHIFT] allows to make really high jumps.")
                    .addSelf("Also increases fire resistance.")
            )
            translationBuilder.add(AnimorphsItems.UNFINISHED_SYMPHONY, "Unfinished Symphony")
            translationBuilder.addTransDesc(
                Identifier("animorphs:note_block"),
                arrayListOf<String>()
                    .addSelf("'Flapping my arms I began to cluck,")
                    .addSelf("look at me, I'm the disco duck.'")
                    .addSelf("")
                    .addSelf("Turns you into a note block.")
                    .addSelf("Movements will sometimes play sounds.")
            )

            translationBuilder.addAbility(Identifier("animorphs:beefly"), "Beefly")
            translationBuilder.addAbilityDesc(Identifier("animorphs:beefly"), "Allows to fly while holding [SPACE] button.")

            translationBuilder.addAbility(Identifier("animorphs:soft_wings"), "Soft Wings")
            translationBuilder.addAbilityDesc(Identifier("animorphs:soft_wings"), "You drown much faster.")

            translationBuilder.addAbility(Identifier("animorphs:magmatic_jump"), "Magmatic Jump")
            translationBuilder.addAbilityDesc(Identifier("animorphs:magmatic_jump"), "Holding [SHIFT] long enough will allow to jump high.")

            translationBuilder.addAbility(Identifier("animorphs:wet_obsidian"), "Wet Obsidian")
            translationBuilder.addAbilityDesc(Identifier("animorphs:wet_obsidian"), "You take damage from touching water.")

            translationBuilder.addAbility(Identifier("animorphs:note_tick"), "Note Tick")
            translationBuilder.addAbilityDesc(Identifier("animorphs:note_tick"), "Moving will occasionally play notes.")

            (DamageTypes::class.java.declaredFields).forEach { e ->
                if (!Modifier.isStatic(e.modifiers) && e.canAccess(null))
                    return@forEach

                val damageType = (e.get(null) as RegistryKey<DamageType>)
                translationBuilder.addAbility(
                    Identifier("animorphs:immune_to_${damageType.value.path}_damage"),
                    "Negates ${damageType.value.path.replace('_', ' ')} damage"
                )
            }

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
                    .setSkin(Identifier("animorphs:textures/transformations/bee.png"))
                    .setSlim(Identifier("animorphs:textures/transformations/bee_slim.png"))
                    .setItem(AnimorphsItems.STINGER_O_POLLEN)
                    .setDesc(
                        arrayListOf<String>()
                            .addSelf("animorphs.transformation.animorphs.desc.bee.0")
                            .addSelf("animorphs.transformation.animorphs.desc.bee.1")
                            .addSelf("animorphs.transformation.animorphs.desc.bee.2")
                    )
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:beefly"))
                            .addSelf(Identifier("animorphs:jump_boost_status_effect"))
                            .addSelf(Identifier("animorphs:immune_to_fall_damage"))
                            .addSelf(Identifier("animorphs:soft_wings"))
                    )
                    .addConditions(
                        arrayListOf<Condition>()
                            .addSelf(Condition().setDecorator(
                                OrConditionDecorator(
                                    LightLevelConditionDecorator(ComparisonOperator.greater, 7),
                                    AndConditionDecorator(
                                        ConditionDecorators.isDay,
                                        ConditionDecorators.isSkyVisible
                                    )
                                )
                            ))
                            .addSelf(Condition().setDecorator(ConditionDecorators.inDimension(World.OVERWORLD)))
                    )
                )

                t.accept(Transformation()
                    .setColor(Color(197, 93, 24).rgb)
                    .setId(Identifier("animorphs:magma_cube"))
                    .setSkin(Identifier("animorphs:textures/transformations/magmacube.png"))
                    .setSlim(Identifier("animorphs:textures/transformations/magmacube.png"))
                    .setItem(AnimorphsItems.MAGMA_JELLY)
                    .setDesc(
                        arrayListOf<String>()
                            .addSelf("animorphs.transformation.animorphs.desc.magma_cube.0")
                            .addSelf("animorphs.transformation.animorphs.desc.magma_cube.1")
                            .addSelf("animorphs.transformation.animorphs.desc.magma_cube.2")
                    )
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:magmatic_jump"))
                            .addSelf(Identifier("animorphs:fire_resistance_status_effect"))
                            .addSelf(Identifier("animorphs:immune_to_fall_damage"))
                            .addSelf(Identifier("animorphs:wet_obsidian"))
                    )
                    .addConditions(
                        arrayListOf<Condition>()
                            .addSelf(Condition().setDecorator(BiomeTemperatureConditionDecorator(ComparisonOperator.greaterOrEqual, 1.5f)))
                    )
                )

                t.accept(Transformation()
                    .setColor(Color(119, 215, 0).rgb)
                    .setId(Identifier("animorphs:note_block"))
                    .setSkin(Identifier("animorphs:textures/transformations/noteman.png"))
                    .setSlim(Identifier("animorphs:textures/transformations/noteman_slim.png"))
                    .setItem(AnimorphsItems.UNFINISHED_SYMPHONY)
                    .setDesc(
                        arrayListOf<String>()
                            .addSelf("animorphs.transformation.animorphs.desc.note_block.0")
                            .addSelf("animorphs.transformation.animorphs.desc.note_block.1")
                            .addSelf("animorphs.transformation.animorphs.desc.note_block.2")
                            .addSelf("animorphs.transformation.animorphs.desc.note_block.3")
                            .addSelf("animorphs.transformation.animorphs.desc.note_block.4")
                    )
                    .addAbilities(
                        arrayListOf<Identifier>()
                            .addSelf(Identifier("animorphs:note_tick"))
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
                            .addSelf(Rule().setDecorator(BeeflyRuleDecorator(0.077f, 0.0796f, 30)))
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
                            .addSelf(Rule().setDecorator(MagmaticJumpRuleDecorator(100, 175)))
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

                t.accept(Ability()
                    .setId(Identifier("animorphs:note_tick"))
                    .setName("animorphs.ability.animorphs.name.note_tick")
                    .setColor(Color(235, 194, 64))
                    .setSign(Ability.Sign.POSITIVE)
                    .setDesc(arrayListOf<String>().addSelf("animorphs.ability.animorphs.desc.note_tick"))
                    .addRules(
                        arrayListOf<Rule>()
                            .addSelf(Rule().setDecorator(NoteTickRuleDecorator()))
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
                                .addSelf(Rule().setDecorator(RuleDecorators.effectDecorator(e.value)))
                        )
                    )
                }

            }.accept(consumer)
        }

        override fun getId(type: Ability): Identifier = type.id

        override fun toJson(type: Ability): JsonObject = JsonSerializer.toJson(type)

        override fun getName(): String = "Animorphs/Abilities"
    }
    class RecipeGenerator(output: FabricDataOutput) : FabricRecipeProvider(output) {
        override fun generate(exporter: Consumer<RecipeJsonProvider>?) {
            ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, AnimorphsItems.UNFINISHED_SYMPHONY)
                .input('#', ItemTags.TERRACOTTA)
                .input('N', Items.NOTE_BLOCK)
                .pattern("###")
                .pattern("#N#")
                .pattern("###")
                .criterion("has_note_block", RecipeProvider.conditionsFromItem(Items.NOTE_BLOCK))
                .offerTo(exporter)
        }
    }
    class LootTableGenerator(output: FabricDataOutput) : SimpleFabricLootTableProvider(output, LootContextTypes.BLOCK) {
        override fun accept(t: BiConsumer<Identifier, LootTable.Builder>?) {
            t!!.accept(AniLootTableIds.STINGER_O_POLLEN, LootTable.builder()
                .pool(
                    LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .conditionally(
                            StingerOPollenCondition.Builder()
                                .setDivideByXEveryYTicks(72000)
                                .setDefaultDivider(1E+09)
                                .setDivider(10)
                                .setUndividedChance(635.0F)
                                .setMinDivider(100.0)
                                .setCapChance(0.035)
                                .setCompareMult(1E-04)
                        )
                        .with(ItemEntry.builder(AnimorphsItems.STINGER_O_POLLEN))
                )
            )
            t.accept(AniLootTableIds.MAGMA_JELLY, LootTable.builder()
                .pool(
                    LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .conditionally(
                            MagmaJellyCondition.Builder()
                                .setDivider(1E+05)
                                .setChance(8184.0)
                        )
                        .with(ItemEntry.builder(AnimorphsItems.MAGMA_JELLY))
                )
            )
        }
    }
}