package com.outlook.oddcommander.dic.mixin.accessors;

import net.minecraft.block.InfestedBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InfestedBlock.class)
public interface InfestedBlockAccessor {
    @Invoker
    void invokeSpawnSilverfish(ServerWorld world, BlockPos pos);
}
