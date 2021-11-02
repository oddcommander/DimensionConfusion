package com.outlook.oddcommander.dic.mixin.monsters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.PiglinBruteSpecificSensor;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PiglinBruteBrain.class)
public abstract class PiglinBruteMixin{
    @ModifyArg(method = "method_30260",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;ILcom/google/common/collect/ImmutableList;)V"),index = 2)
    private static ImmutableList<? extends Task<?>> addTask(ImmutableList<? extends Task<?>> tasks){
        List<Task<?>> mutableTasks = new ArrayList<>(tasks);
        mutableTasks.add(new SeekHigherPlaceTask(0.8f));
        return ImmutableList.copyOf(mutableTasks);
    }

    @Inject(method = "method_30247",at = @At(value = "RETURN",ordinal = 1),cancellable = true)
    private static void addTarget(AbstractPiglinEntity abstractPiglinEntity, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir){
        if(cir.getReturnValue().isEmpty()){
            List<LivingEntity> entities = abstractPiglinEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Collections.emptyList());
            if(!entities.isEmpty()) cir.setReturnValue(entities.stream().filter(DimensionConfusionCore::isOverworldMob).findAny());
        }
    }

    @Mixin(PiglinBruteSpecificSensor.class)
    static abstract class SensorMixin{

        /**
         * @reason Make piglin brutes angry at overworld mobs
         * @author Odd_Commander
         */
        @Overwrite
        public void sense(ServerWorld world, LivingEntity entity) {
            Brain<?> brain = entity.getBrain();
            Optional<MobEntity> optional = Optional.empty();
            List<AbstractPiglinEntity> list = Lists.newArrayList();
            List<LivingEntity> list2 = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of());

            for (LivingEntity livingEntity : list2) {
                if (livingEntity instanceof WitherSkeletonEntity || livingEntity instanceof WitherEntity) {
                    optional = Optional.of((MobEntity) livingEntity);
                    break;
                }

                //Added
                if(DimensionConfusionCore.isOverworldMob(livingEntity)){
                    optional = Optional.of((MobEntity) livingEntity);
                    break;
                }
                //Added
            }

            List<LivingEntity> list3 = brain.getOptionalMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of());

            for (LivingEntity livingEntity2 : list3) {
                if (livingEntity2 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity) livingEntity2).isAdult()) {
                    list.add((AbstractPiglinEntity) livingEntity2);
                }
            }

            brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
            brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
        }
    }
}
