package basicallyiamfox.ani

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object AnimorphsKeybindings {
    var beeflyKeyBinding: KeyBinding? = null
        private set
    var magmaJumpKeyBinding: KeyBinding? = null
        private set

    fun init() {
        beeflyKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.animorphs.beefly",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SPACE,
                "category.animorphs.keys"
            )
        )
        magmaJumpKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.animorphs.magma_jump",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.animorphs.keys"
            )
        )
    }
}