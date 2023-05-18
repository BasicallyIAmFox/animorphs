package basicallyiamfox.ani.cache.item

import basicallyiamfox.ani.core.Transformation
import basicallyiamfox.ani.extensions.getClientAbilityManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import java.awt.Color

@Environment(EnvType.CLIENT)
class TooltipCache {
    companion object {
        private val emptyWhite = Style.EMPTY.withColor(Color.WHITE.rgb)
        private const val isVisualActiveKey = "animorphs.tooltip.animorphs.is_visual_active"

        @JvmStatic
        val isVisualActiveOn: Text = Text.translatable(isVisualActiveKey).append(" ").append(Text.translatable("options.on").formatted(Formatting.GREEN))
        @JvmStatic
        val isVisualActiveOff: Text = Text.translatable(isVisualActiveKey).append(" ").append(Text.translatable("options.off").formatted(Formatting.RED))

        @JvmStatic
        val desc = hashMapOf<Identifier, List<Text>>()
        @JvmStatic
        val lines = hashMapOf<Identifier, List<Text>>()

        @JvmStatic
        fun loadDescForItem(item: Item, transformation: Transformation): List<Text> {
            var descList = desc[Registries.ITEM.getId(item)]
            if (descList == null) {
                val list = arrayListOf<Text>()

                if (!transformation.desc.isNullOrEmpty()) {
                    transformation.desc!!.forEach {
                        list.add(Text.translatable(it))
                    }
                }

                desc[Registries.ITEM.getId(item)] = list
                descList = list
            }
            return descList
        }
        @JvmStatic
        fun loadForItem(item: Item, transformation: Transformation): List<Text> {
            var linesList = lines[Registries.ITEM.getId(item)]
            if (linesList == null) {
                val list = arrayListOf<Text>()
                for (a in transformation.abilities) {
                    var text = Text.literal("* ").setStyle(emptyWhite)
                    val ability = getClientAbilityManager().get(a)
                    if (ability == null) {
                        list.add(text.append(Text.literal("Unable to load ability: ").append(Text.translatable(a.toTranslationKey()))))
                        continue
                    }

                    if (!ability.visible) continue

                    text = text.append(
                        (Text.translatable(ability.sign.text).append(Text.literal(" ")))
                            .setStyle(Style.EMPTY.withColor(ability.sign.colorHex))
                    )

                    text = text.append(Text.translatable(ability.name).setStyle(Style.EMPTY.withColor(ability.colorHex)))
                    list.add(text)

                    if (ability.desc == null) {
                        continue
                    }

                    ability.desc!!.forEach { e ->
                        list.add(Text.literal("    ").append(Text.translatable(e)))
                    }
                }

                linesList = list
                lines[Registries.ITEM.getId(item)] = list
            }
            return linesList
        }

        fun clear() {
            desc.clear()
            lines.clear()
        }
    }
}