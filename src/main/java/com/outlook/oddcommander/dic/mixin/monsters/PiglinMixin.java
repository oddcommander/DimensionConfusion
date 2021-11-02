package com.outlook.oddcommander.dic.mixin.monsters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceTask;
import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PiglinEntity.class)
public abstract class PiglinMixin extends AbstractPiglinEntity {
    @Shadow
    public abstract Brain<PiglinEntity> getBrain();

    private PiglinMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
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

    @Mixin(PiglinSpecificSensor.class)
    static abstract class SensorMixin{
        @Shadow
        private static Optional<BlockPos> findPiglinRepellent(ServerWorld world,LivingEntity entity){
            throw new AssertionError();
        }

        /**
         * @reason Make piglins angry at overworld mobs
         * @author Odd_Commander
         */
        @Overwrite
        public void sense(ServerWorld world, LivingEntity entity) {
            Brain<?> brain = entity.getBrain();
            brain.remember(MemoryModuleType.NEAREST_REPELLENT, findPiglinRepellent(world, entity));
            Optional<MobEntity> optional = Optional.empty();
            Optional<HoglinEntity> optional2 = Optional.empty();
            Optional<HoglinEntity> optional3 = Optional.empty();
            Optional<PiglinEntity> optional4 = Optional.empty();
            Optional<LivingEntity> optional5 = Optional.empty();
            Optional<PlayerEntity> optional6 = Optional.empty();
            Optional<PlayerEntity> optional7 = Optional.empty();
            int i = 0;
            List<AbstractPiglinEntity> list = Lists.newArrayList();
            List<AbstractPiglinEntity> list2 = Lists.newArrayList();
            List<LivingEntity> list3 = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of());
            Iterator<LivingEntity> var15 = list3.iterator();

            while(true) {
                while(true) {
                    while(var15.hasNext()) {
                        LivingEntity livingEntity = var15.next();
                        if (livingEntity instanceof HoglinEntity) {
                            HoglinEntity hoglinEntity = (HoglinEntity)livingEntity;
                            if (hoglinEntity.isBaby() && optional3.isEmpty()) {
                                optional3 = Optional.of(hoglinEntity);
                            } else if (hoglinEntity.isAdult()) {
                                ++i;
                                if (optional2.isEmpty() && hoglinEntity.canBeHunted()) {
                                    optional2 = Optional.of(hoglinEntity);
                                }
                            }
                        } else if (livingEntity instanceof PiglinBruteEntity) {
                            list.add((PiglinBruteEntity)livingEntity);
                        } else if (livingEntity instanceof PiglinEntity) {
                            PiglinEntity piglinEntity = (PiglinEntity)livingEntity;
                            if (piglinEntity.isBaby() && optional4.isEmpty()) {
                                optional4 = Optional.of(piglinEntity);
                            } else if (piglinEntity.isAdult()) {
                                list.add(piglinEntity);
                            }
                        } else if (livingEntity instanceof PlayerEntity) {
                            PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                            if (optional6.isEmpty() && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity) && !PiglinBrain.wearsGoldArmor(playerEntity)) {
                                optional6 = Optional.of(playerEntity);
                            }

                            if (optional7.isEmpty() && !playerEntity.isSpectator() && PiglinBrain.isGoldHoldingPlayer(playerEntity)) {
                                optional7 = Optional.of(playerEntity);
                            }
                        } else if (optional.isPresent() || !(livingEntity instanceof WitherSkeletonEntity) && !(livingEntity instanceof WitherEntity)) {

                            //Added
                            if (optional.isEmpty() && DimensionConfusionCore.isOverworldMob(livingEntity)){
                                optional = Optional.of((MobEntity) livingEntity);
                            }
                            //Added

                            if (optional5.isEmpty() && PiglinBrain.isZombified(livingEntity.getType())) {
                                optional5 = Optional.of(livingEntity);
                            }
                        } else {
                            optional = Optional.of((MobEntity)livingEntity);
                        }
                    }

                    List<LivingEntity> list4 = brain.getOptionalMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of());

                    for (LivingEntity livingEntity2 : list4) {
                        if (livingEntity2 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity) livingEntity2).isAdult()) {
                            list2.add((AbstractPiglinEntity) livingEntity2);
                        }
                    }

                    brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
                    brain.remember(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional2);
                    brain.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional3);
                    brain.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional5);
                    brain.remember(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional6);
                    brain.remember(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional7);
                    brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list2);
                    brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
                    brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
                    brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
                    return;
                }
            }
        }
    }

    @Mixin(PiglinBrain.class)
    static abstract class PiglinBrainMixin{
        @ModifyArg(method = "addCoreActivities",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;ILcom/google/common/collect/ImmutableList;)V"),index = 2)
        private static ImmutableList<? extends Task<?>> addTask(ImmutableList<? extends Task<?>> tasks){
            List<Task<?>> mutableTasks = new ArrayList<>(tasks);
            mutableTasks.add(new SeekHigherPlaceTask(0.8f));
            return ImmutableList.copyOf(mutableTasks);
        }
    }
}
