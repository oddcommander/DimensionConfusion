package com.outlook.oddcommander.dic.mixin.accessors;

import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructuresConfig.class)
public interface StructuresConfigAccessor {
    @Accessor @Mutable
    void setStronghold(StrongholdConfig stronghold);
}
