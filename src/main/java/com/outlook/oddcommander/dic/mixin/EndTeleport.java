package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class EndTeleport {
    @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/Entity;moveToWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"),method = "onEntityCollision",cancellable = true)
    private void onEndTeleport(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci){
        if(world.getServer()==null || DimensionConfusionCore.isEnd(entity.world.getDimension())) return;
        ServerWorld end = world.getServer().getWorld(World.END);
        BlockPos spawn = ServerWorld.END_SPAWN_POS;
        BlockPos.Mutable mutable = new BlockPos.Mutable(spawn.getX(),0,spawn.getZ());
        if(end!=null){
            ServerWorld.createEndSpawnPlatform(end);
            for(int y = 255;y >= 50;y--){
                mutable.setY(y);
                if(!end.getBlockState(mutable).isAir()){
                    break;
                }
            }
            mutable.setY(mutable.getY() + 30);
            DimensionConfusionCore.teleport(entity,end,spawn.getX(),mutable.getY(),spawn.getZ(),entity.yaw,entity.pitch);
            ci.cancel();
        }
    }
}
