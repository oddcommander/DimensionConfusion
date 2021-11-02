package com.outlook.oddcommander.dic.mixin.monsters;

import com.google.common.collect.ImmutableList;
import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceTask;
import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(HoglinEntity.class)
public abstract class HoglinMixin extends AnimalEntity {
    private HoglinMixin(EntityType<? extends AnimalEntity> entityType, World world) {
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

    @Mixin(HoglinBrain.class)
    static class BrainMixin{
        @Inject(method = "getNearestVisibleTargetablePlayer",at = @At("RETURN"), cancellable = true)
        private static void addTarget(HoglinEntity hoglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir){
            if(cir.getReturnValue().isEmpty()){
                List<LivingEntity> entities = hoglin.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Collections.emptyList());
                if(!entities.isEmpty()) cir.setReturnValue(entities.stream().filter(DimensionConfusionCore::isOverworldMob).findAny());
            }
        }

        @ModifyArg(method = "addIdleTasks",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;ILcom/google/common/collect/ImmutableList;)V"),index = 2)
        private static ImmutableList<? extends Task<?>> addIdleTask(ImmutableList<? extends Task<?>> tasks){
            List<Task<?>> mutableTasks = new ArrayList<>(tasks);
            mutableTasks.add(new SeekHigherPlaceTask(0.8f));
            return ImmutableList.copyOf(mutableTasks);
        }
    }
}
