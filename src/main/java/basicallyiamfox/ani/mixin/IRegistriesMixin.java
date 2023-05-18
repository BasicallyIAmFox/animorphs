package basicallyiamfox.ani.mixin;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Registries.class)
public interface IRegistriesMixin {
    @Invoker
    static <T> Registry<T> invokeCreate(RegistryKey<? extends Registry<T>> key, Registries.Initializer<T> initializer) {
        throw new AssertionError();
    }
}
