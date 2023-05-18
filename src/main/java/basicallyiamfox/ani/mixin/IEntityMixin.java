package basicallyiamfox.ani.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IEntityMixin {
    @Invoker
    void invokeCalculateDimensions();

    @Invoker
    boolean invokeIsBeingRainedOn();

    @Invoker
    static Vec3d invokeMovementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        throw new AssertionError();
    }
}
