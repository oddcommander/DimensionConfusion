package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.mixin.accessors.ChunkGenSettingsAccessor;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(GeneratorOptions.class)
public abstract class GenOptionsMixin {
    @Inject(method = "createOverworldGenerator",at = @At("RETURN"),cancellable = true)
    private static void noStronghold(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed, CallbackInfoReturnable<NoiseChunkGenerator> cir){
        ChunkGeneratorSettings settings = chunkGeneratorSettingsRegistry.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
        StrongholdConfig strongholdConfig = settings.getStructuresConfig().getStronghold();
        if(strongholdConfig!=null){
            StructuresConfig noStronghold = new StructuresConfig(Optional.empty(),settings.getStructuresConfig().getStructures());
            ChunkGenSettingsAccessor accessor = (ChunkGenSettingsAccessor) (Object) settings;
            accessor.setStructuresConfig(noStronghold);
        }
        cir.setReturnValue(new NoiseChunkGenerator(new VanillaLayeredBiomeSource(seed, false, false, biomeRegistry), seed, () -> settings));
        cir.cancel();
    }
}
