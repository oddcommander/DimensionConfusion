package com.outlook.oddcommander.dic.entity;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.world.World;

public class NetherSilverfishEntity extends SilverfishEntity {
    public NetherSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(2,new SeekHigherPlaceGoal(this,1.2f));
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class, 10, true, false, DimensionConfusionCore::isOverworldMob));
    }

    @Override
    public boolean tryAttack(Entity target) {
        if(!super.tryAttack(target)){
            return false;
        }
        else{
            target.setOnFireFor(3);
            return true;
        }
    }

    protected NetherSilverfishEntity(World world){
        super(DimensionConfusionCore.NETHER_SILVERFISH,world);
    }
}
