package basicallyiamfox.ani.mixin.bee_transformation;

import basicallyiamfox.ani.transformation.rule.decorator.BeeflyRuleDecorator;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataOutput;
import java.io.IOException;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements BeeflyRuleDecorator.BeeflyPlayerEntity {
    @Unique
    private int stingerFly;
    @Unique
    private int stingerTick;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Override
    public int getStingerFly() {
        return stingerFly;
    }
    @Unique
    @Override
    public void setStingerFly(int i) {
        stingerFly = i;
    }

    @Unique
    @Override
    public int getStingerTick() {
        return stingerTick;
    }
    @Unique
    @Override
    public void setStingerTick(int i) {
        stingerTick = i;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void animorphs$writeStingerTickToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound element = nbt.contains("animorphs_data") ? nbt.getCompound("animorphs_data") : new NbtCompound();
        element.putInt("stinger_tick", stingerTick);
        nbt.put("animorphs_data", element);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void animorphs$readStingerTickFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("animorphs_data") && nbt.getCompound("animorphs_data").contains("stinger_tick")) {
            stingerTick = nbt.getCompound("animorphs_data").getInt("stinger_tick");
        }
    }
}
