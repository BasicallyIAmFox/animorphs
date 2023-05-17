package basicallyiamfox.ani.mixin.bee_transformation;


import basicallyiamfox.ani.item.AnimorphsItems;
import basicallyiamfox.ani.loot.AniLootTableIds;
import basicallyiamfox.ani.loot.ContextParameters;
import basicallyiamfox.ani.transformation.rule.decorator.BeeflyRuleDecorator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BlockWithEntity {
    protected BeehiveBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BeehiveBlock;dropHoneycomb(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void animorphs$onUseDropHoneycomb(World what,
                                              BlockPos what2,
                                              Operation<Void> original,
                                              BlockState state,
                                              World world,
                                              BlockPos pos,
                                              PlayerEntity player,
                                              Hand hand,
                                              BlockHitResult hit) {
        var duck = (BeeflyRuleDecorator.BeeflyPlayerEntity)player;
        if (player.getServer() != null) {
            var table = player.getServer().getLootManager().getTable(AniLootTableIds.STINGER_O_POLLEN)
                    .generateLoot(
                            new LootContext.Builder((ServerWorld) world)
                                    .parameter(ContextParameters.THIS_PLAYER, player)
                                    .build(LootContextTypes.EMPTY)
                    );
            if (table.size() > 0) {
                BeehiveBlock.dropStack(world, pos, AnimorphsItems.STINGER_O_POLLEN.getDefaultStack());
                duck.setStingerTick(0);
                return;
            }
            original.call(world, pos);
        }
    }
}
