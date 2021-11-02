package com.outlook.oddcommander.dic.mixin.accessors;

import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorSettings.class)
public interface ChunkGenSettingsAccessor {
    @Accessor @Mutable
    void setStructuresConfig(StructuresConfig config);

    @Accessor @Mutable
    void setBedrockCeilingY(int bedrockCeilingY);
}
