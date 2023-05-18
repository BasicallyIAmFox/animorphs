package basicallyiamfox.ani.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item

class TransformationItem : Item(FabricItemSettings().maxCount(1)) {
    companion object {
        const val VISUAL_ACTIVE_KEY = "visual_active"
    }
}