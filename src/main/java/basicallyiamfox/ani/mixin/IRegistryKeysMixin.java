package basicallyiamfox.ani.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegistryKeys.class)
public interface IRegistryKeysMixin {
    @Invoker
    static <T> RegistryKey<Registry<T>> invokeOf(String id) {
        throw new AssertionError();
    }
}
