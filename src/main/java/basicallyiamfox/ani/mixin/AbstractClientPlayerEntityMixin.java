package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.extensions.ExtensionsKt;
import basicallyiamfox.ani.interfaces.IPlayerEntity;
import basicallyiamfox.ani.item.TransformationItem;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getSkinTexture", at = @At("RETURN"), cancellable = true)
    private void animorphs$changeSkinDependingOnTransformation(CallbackInfoReturnable<Identifier> cir) {
        if (isSpectator())
            return;

        var duck = (IPlayerEntity)this;
        if (duck.getActiveTransformation() == null) {
            return;
        }

        if (!duck.getTransformationItem().getOrCreateNbt().getBoolean(TransformationItem.VISUAL_ACTIVE_KEY)) {
            return;
        }

        var transformation = ExtensionsKt.getClientTransformationManager().get(duck.getActiveTransformation());
        if (transformation == null || !transformation.isActive(world, this))
            return;

        Identifier path;
        if (DefaultSkinHelper.getModel(getUuid()).equals("slim")) {
            path = transformation.getSkinSlim();
        }
        else {
            path = transformation.getSkin();
        }

        cir.setReturnValue(path);
    }
}
