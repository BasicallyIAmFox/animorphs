package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.ExtensionsKt;
import basicallyiamfox.ani.interfaces.IPlayerEntity;
import basicallyiamfox.ani.interfaces.IPlayerInventory;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow @Final public MinecraftServer server;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    @Inject(method = "tick", at = @At("RETURN"))
    private void animorphs$tick(CallbackInfo ci) {
        if (isSpectator())
            return;

        PlayerEntity entity = this;
        IPlayerEntity duck = (IPlayerEntity)entity;
        duck.setActiveTransformation(null);
        duck.setTransformationItem(null);

        DefaultedList<ItemStack> list = ((IPlayerInventory)getInventory())
                .findItems((stack -> ExtensionsKt.getTransformationLoader(server).getManager().get(stack.getItem()) != null));

        if (list == null || list.isEmpty())
            return;

        duck.setTransformationItem(list.get(0));

        var transformation = ExtensionsKt.getTransformationLoader(server).getManager().get(list.get(0).getItem());
        duck.setActiveTransformation(transformation.getId());
        transformation.tick((ServerPlayerEntity)(Object)this);
    }
}
