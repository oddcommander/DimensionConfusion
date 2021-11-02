package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.mixin.accessors.ChunkGeneratorAccessor;
import com.outlook.oddcommander.dic.mixin.accessors.StructuresConfigAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftServer.class)
public abstract class ServerMixin {
    @Redirect(
        at = @At(value = "INVOKE",target = "Lnet/minecraft/world/dimension/DimensionOptions;getChunkGenerator()Lnet/minecraft/world/gen/chunk/ChunkGenerator;"),
        method = "createWorlds",
        slice = @Slice(from = @At(value = "INVOKE",target = "Lnet/minecraft/util/registry/RegistryKey;of(Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/registry/RegistryKey;"), to = @At(value = "INVOKE",target = "Lnet/minecraft/world/border/WorldBorder;addListener(Lnet/minecraft/world/border/WorldBorderListener;)V"))
    )
    public ChunkGenerator onNetherCreate(DimensionOptions dimensionOptions){
        DimensionType type = dimensionOptions.getDimensionType();
        if(DimensionConfusionCore.isNether(type)){
            ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();
            StructuresConfig config = chunkGenerator.getStructuresConfig();
            StrongholdConfig withStronghold = StructuresConfig.DEFAULT_STRONGHOLD;
            ChunkGeneratorAccessor accessor1 = (ChunkGeneratorAccessor) chunkGenerator;
            StructuresConfigAccessor accessor2 = (StructuresConfigAccessor) config;
            accessor2.setStronghold(withStronghold);
            accessor1.setStructuresConfig(config);
            return chunkGenerator;
        }
        return dimensionOptions.getChunkGenerator();
    }

    @Redirect(
        at = @At(value = "INVOKE",target = "Lnet/minecraft/world/dimension/DimensionOptions;getChunkGenerator()Lnet/minecraft/world/gen/chunk/ChunkGenerator;"),
        method = "createWorlds",
        slice = @Slice(from = @At(value = "HEAD"),to = @At(value = "INVOKE",target = "Lnet/minecraft/world/border/WorldBorder;load(Lnet/minecraft/world/border/WorldBorder$Properties;)V"))
    )
    public ChunkGenerator onOverworldCreate(DimensionOptions dimensionOptions){
        ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();
        if(chunkGenerator.getStructuresConfig()!=null){
            StructuresConfig config = chunkGenerator.getStructuresConfig();
            StrongholdConfig noStronghold = new StrongholdConfig(0,0,0);
            ChunkGeneratorAccessor accessor1 = (ChunkGeneratorAccessor) chunkGenerator;
            StructuresConfigAccessor accessor2 = (StructuresConfigAccessor) config;
            accessor2.setStronghold(noStronghold);
            accessor1.setStructuresConfig(config);
        }
        return chunkGenerator;
    }
}
