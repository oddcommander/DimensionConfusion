package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.mixin.accessors.ChunkGenSettingsAccessor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseGenMixin {
    @Shadow @Final
    protected Supplier<ChunkGeneratorSettings> settings;

    @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/world/gen/chunk/NoiseChunkGenerator;buildBedrock(Lnet/minecraft/world/chunk/Chunk;Ljava/util/Random;)V"),method = "buildSurface",cancellable = true)
    private void removeBedrock(ChunkRegion region, Chunk chunk, CallbackInfo ci){
        ServerWorld world = region.toServerWorld();
        if(DimensionConfusionCore.isOverworld(world.getDimension())) ci.cancel();
        if(DimensionConfusionCore.isNether(world.getDimension())){
            ChunkGeneratorSettings settings = this.settings.get();
            ChunkGenSettingsAccessor accessor = (ChunkGenSettingsAccessor) (Object) settings;
            accessor.setBedrockCeilingY(-16);
        }
    }
}
