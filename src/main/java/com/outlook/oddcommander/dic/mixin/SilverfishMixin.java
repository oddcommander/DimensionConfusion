package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.block.NetherInfestedBlock;
import com.outlook.oddcommander.dic.entity.NetherSilverfishEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.entity.mob.SilverfishEntity$WanderAndInfestGoal")
public abstract class SilverfishMixin extends WanderAroundGoal {
    @Shadow
    private Direction direction;

    private SilverfishMixin(PathAwareEntity mob, double speed) {
        super(mob, speed);
    }

    @Inject(method = "start()V",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"),cancellable = true)
    private void considerNether(CallbackInfo ci){
        WorldAccess worldAccess = this.mob.world;
        BlockPos blockPos = (new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ())).offset(this.direction);
        BlockState blockState = worldAccess.getBlockState(blockPos);
        if(this.mob instanceof NetherSilverfishEntity){
            if(NetherInfestedBlock.isNetherInfestable(blockState)){
                worldAccess.setBlockState(blockPos,NetherInfestedBlock.fromRegularBlock(blockState.getBlock(),false),3);
                this.mob.playSpawnEffects();
                this.mob.remove();
            }
            else super.start();
            ci.cancel();
        }
    }
}
