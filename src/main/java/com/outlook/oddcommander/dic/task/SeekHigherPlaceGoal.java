package com.outlook.oddcommander.dic.task;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class SeekHigherPlaceGoal extends Goal {
    private final MobEntity mob;
    private final float speed;
    private int distance;
    private boolean canOut;

    public SeekHigherPlaceGoal(MobEntity mob,float speed){
        this.mob = mob;
        this.speed = speed;
        this.distance = 3;
        this.canOut = true;
    }

    @Override
    public boolean canStart() {
        return !mob.world.isSkyVisible(mob.getBlockPos()) && canOut && DimensionConfusionCore.isOverworld(mob.world.getDimension());
    }

    @Override
    public void tick() {
        if(mob.getNavigation().isFollowingPath()){
            distance = 3;
            return;
        }
        if(distance > 15) this.stop();
        BlockPos test = mob.getBlockPos();
        BlockPos.Mutable center = test.mutableCopy();
        Optional<BlockPos> target = surround(center,distance).stream().sorted(Comparator.comparingDouble(pos -> pos.getSquaredDistance(center))).filter(next -> {
            if(canEntityStandOn(mob,next)){
                Path path = mob.getNavigation().findPathTo(next.getX() + 0.5,next.getY() + 1,next.getZ() + 0.5,64);
                return path != null;
            }
            return false;
        }).findAny();
        if(target.isPresent()){
            BlockPos pos = target.get();
            distance = 3;
            mob.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,speed);
        }
        else distance += 2;
    }

    private void retry(){
        canOut = true;
    }

    @Override
    public void stop() {
        this.distance = 3;
        mob.getNavigation().stop();
        canOut = false;
        Executors.newSingleThreadScheduledExecutor().schedule(this::retry,30L, TimeUnit.SECONDS);
    }

    public static boolean canEntityStandOn(Entity entity, BlockPos pos){
        if(!entity.world.getBlockState(pos).getMaterial().isSolid()) return false;
        Box box = entity.getBoundingBox();
        BlockPos start = new BlockPos(pos.getX() + 0.5 - box.getXLength()/2,pos.getY() + 1,pos.getZ() + 0.5 - box.getZLength()/2);
        BlockPos end = new BlockPos(pos.getX() + 0.5 + box.getXLength()/2,pos.getY() + 1 + box.getYLength(),pos.getZ() + 0.5 + box.getZLength()/2);
        for (BlockPos next : BlockPos.iterate(start, end)) if (entity.world.getBlockState(next).getMaterial().isSolid()) return false;
        return true;
    }

    public static Set<BlockPos> surround(BlockPos center,int distance){
        Set<BlockPos> pos = new HashSet<>();
        int i = (distance - 1)/2;
        BlockPos ne = center.north(i).east(i),se = center.south(i).east(i),nw = center.north(i).west(i),sw = center.south(i).west(i);
        for(int n = 0;n < distance;n++){
            pos.add(ne.south(n));
            pos.add(se.west(n));
            pos.add(nw.east(n));
            pos.add(sw.north(n));
        }
        return pos;
    }
}
