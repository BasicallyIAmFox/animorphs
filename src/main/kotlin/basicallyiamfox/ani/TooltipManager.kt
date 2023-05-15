package basicallyiamfox.ani

import net.minecraft.text.Text
import net.minecraft.util.Formatting

class TooltipManager {
    companion object {
        @JvmStatic
        val IS_VISUAL_ACTIVE_ON: Text = Text.translatable("tooltip.animorphs.is_visual_active").append(" ").append(Text.translatable("options.on").formatted(Formatting.GREEN))
        @JvmStatic
        val IS_VISUAL_ACTIVE_OFF: Text = Text.translatable("tooltip.animorphs.is_visual_active").append(" ").append(Text.translatable("options.off").formatted(Formatting.RED))
    }
}