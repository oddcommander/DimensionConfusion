package com.outlook.oddcommander.dic.task;

import com.google.common.collect.ImmutableMap;
import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class SeekHigherPlaceTask extends Task<LivingEntity> {
    private final float speed;
    private boolean canOut;
    private int distance;

    public SeekHigherPlaceTask(float speed){
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.speed = speed;
        this.canOut = true;
        this.distance = 3;
    }

    @Override
    public boolean shouldRun(ServerWorld world, LivingEntity mob) {
        return mob instanceof MobEntity && !world.isSkyVisible(mob.getBlockPos()) && canOut && DimensionConfusionCore.isOverworld(world.getDimension());
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return distance > 15;
    }

    @Override
    public void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        if(!(entity instanceof MobEntity)) return;
        MobEntity mob = (MobEntity) entity;
        mob.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,60,0));
        if(mob.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)){
            distance = 3;
            return;
        }
        BlockPos test = mob.getBlockPos();
        BlockPos.Mutable center = test.mutableCopy();
        Optional<BlockPos> target = SeekHigherPlaceGoal.surround(center,distance).stream().sorted(Comparator.comparingDouble(pos -> pos.getSquaredDistance(center))).filter(next -> {
            if(SeekHigherPlaceGoal.canEntityStandOn(mob,next)){
                Path path = mob.getNavigation().findPathTo(next.getX() + 0.5,next.getY() + 1,next.getZ() + 0.5,64);
                return path != null;
            }
            return false;
        }).findAny();
        if(target.isPresent()){
            BlockPos pos = target.get();
            distance = 3;
            mob.getBrain().remember(MemoryModuleType.WALK_TARGET,new WalkTarget(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5),speed,64));
        }
        else distance += 2;
    }

    private void retry(){
        this.canOut = true;
    }

    @Override
    public void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        if(!(entity instanceof MobEntity)) return;
        MobEntity mob = (MobEntity) entity;
        this.distance = 3;
        mob.getBrain().forget(MemoryModuleType.WALK_TARGET);
        canOut = false;
        Executors.newSingleThreadScheduledExecutor().schedule(this::retry,30L, TimeUnit.SECONDS);
    }
}
