package com.outlook.oddcommander.dic.util;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface SpawnPredicate {
    SpawnPredicate OVERWORLD = (world, pos, reason) ->{
        if(DimensionConfusionCore.isOverworld(world.getDimension())){
            if(reason == SpawnReason.NATURAL) return pos.getY() < 64 && !world.isSkyVisible(pos) && world.getBlockState(pos).isOf(Blocks.CAVE_AIR);
        }
        return true;
    };

    SpawnPredicate NETHER = (world, pos, reason) ->{
        if(DimensionConfusionCore.isNether(world.getDimension())){
            if(reason == SpawnReason.NATURAL) return pos.getY() < 24;
        }
        return true;
    };

    boolean test(WorldAccess world, BlockPos pos, SpawnReason reason);
}
