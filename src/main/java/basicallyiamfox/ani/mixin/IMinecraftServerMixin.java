package basicallyiamfox.ani.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface IMinecraftServerMixin {
    @Accessor
    MinecraftServer.ResourceManagerHolder getResourceManagerHolder();
}
