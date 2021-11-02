package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceGoal;
import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Random;

@Mixin(GhastEntity.class)
public abstract class AdjustMonstersSpawn extends FlyingEntity{
    protected AdjustMonstersSpawn(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"),method = "canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z",cancellable = true)
    private static void setOverworldSpawnCondition(EntityType<HoglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
        boolean canSpawn = SpawnPredicate.OVERWORLD.test(world,pos,spawnReason);
        if(!canSpawn){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("RETURN"),method = "initGoals")
    private void setAngryAtOverworldMobs(CallbackInfo ci){
        this.targetSelector.add(5,new SeekHigherPlaceGoal(this,0.8f));
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false,DimensionConfusionCore::isOverworldMob));
    }

    @Mixin(HostileEntity.class)
    static abstract class IgnoreLights{
        @Inject(at = @At("HEAD"),method = "canSpawnIgnoreLightLevel",cancellable = true)
        private static void setOverworldSpawnConditions(EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
            boolean canSpawn = SpawnPredicate.OVERWORLD.test(world,pos,spawnReason);
            if(!canSpawn){
                cir.setReturnValue(false);
                cir.cancel();
            }
        }

        @Inject(at = @At("HEAD"),method = "canSpawnInDark",cancellable = true)
        private static void setOverworldSpawnConditions(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
            if(type == EntityType.BLAZE || type == EntityType.WITHER_SKELETON){
                boolean canSpawn = SpawnPredicate.OVERWORLD.test(world,pos,spawnReason);
                if(!canSpawn){
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
            if(type == EntityType.ENDERMAN){
                boolean canSpawn = SpawnPredicate.NETHER.test(world,pos,spawnReason);
                Biome biome = world.getBiome(pos);
                Optional<RegistryKey<Biome>> registryKey = BuiltinRegistries.BIOME.getKey(biome);
                if(registryKey.isPresent() && !BiomeKeys.CRIMSON_FOREST.equals(registryKey.get()) && !canSpawn){
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    @Mixin(MagmaCubeEntity.class)
    static abstract class MagmaSpawn extends SlimeEntity{
        public MagmaSpawn(EntityType<? extends SlimeEntity> entityType, World world) {
            super(entityType, world);
        }

        @Inject(at = @At("HEAD"),method = "canMagmaCubeSpawn",cancellable = true)
        private static void setOverworldSpawnConditions(EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
            boolean canSpawn = SpawnPredicate.OVERWORLD.test(world,pos,spawnReason);
            if(!canSpawn){
                cir.setReturnValue(false);
                cir.cancel();
            }
        }

        @Inject(at = @At("RETURN"),method = "<init>")
        private void setAngryAtOverworldMobs(EntityType<? extends MagmaCubeEntity> entityType, World world, CallbackInfo ci){
            this.targetSelector.add(5,new SeekHigherPlaceGoal(this,0.8f));
            this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false,DimensionConfusionCore::isOverworldMob));
        }
    }
}
