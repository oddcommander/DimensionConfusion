package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.block.NetherInfestedBlock;
import com.outlook.oddcommander.dic.mixin.accessors.InfestedBlockAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    private final Map<BlockPos,Boolean> netherInfestedBlocks = new HashMap<>();

    @Redirect(method = "affectWorld",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState isNetherInfestedBlock(World world, BlockPos pos){
        BlockState blockState = world.getBlockState(pos);
        if(blockState.getBlock() instanceof NetherInfestedBlock){
            netherInfestedBlocks.put(pos,blockState.get(NetherInfestedBlock.IS_SILVERFISH));
        }
        return blockState;
    }

    @Redirect(method = "affectWorld",at = @At(value = "INVOKE",target = "Lnet/minecraft/block/Block;onDestroyedByExplosion(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;)V"))
    private void onExplodingNetherInfestedBlock(Block block, World world, BlockPos pos, Explosion explosion){
        if(netherInfestedBlocks.containsKey(pos) && world instanceof ServerWorld){
            boolean isSilverfish = netherInfestedBlocks.remove(pos);
            NetherInfestedBlock netherInfestedBlock = (NetherInfestedBlock) block;
            InfestedBlockAccessor accessor = (InfestedBlockAccessor) block;
            if(!isSilverfish) netherInfestedBlock.spawnNetherSilverfish((ServerWorld) world,pos);
            else accessor.invokeSpawnSilverfish((ServerWorld) world,pos);
        }
    }
}
