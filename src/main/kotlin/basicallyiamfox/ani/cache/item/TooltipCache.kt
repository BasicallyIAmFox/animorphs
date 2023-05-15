package basicallyiamfox.ani.cache.item

import basicallyiamfox.ani.getAbilityManager
import basicallyiamfox.ani.transformation.Transformation
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
        val lines = hashMapOf<Identifier, List<Text>>()

        @JvmStatic
        fun loadForItem(item: Item, transformation: Transformation): List<Text> {
            var linesList = lines[Registries.ITEM.getId(item)]
            if (linesList == null) {
                val list = arrayListOf<Text>()
                if (transformation.desc != null && transformation.desc!!.isNotEmpty()) {
                    transformation.desc!!.forEach {
                        list.add(Text.translatable(it))
                    }
                    list.add(Text.empty())
                }

                for (a in transformation.abilities) {
                    var text = Text.literal("* ").setStyle(emptyWhite)
                    val ability = getAbilityManager().get(a)
                    if (ability == null) {
                        list.add(text.append(Text.literal("Unable to load ability: ").append(Text.translatable(a.toTranslationKey()))))
                        continue
                    }

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
            lines.clear()
        }
    }
}