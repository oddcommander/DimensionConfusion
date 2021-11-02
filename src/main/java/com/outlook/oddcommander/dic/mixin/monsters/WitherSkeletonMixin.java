package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkeletonEntity.class)
public abstract class WitherSkeletonMixin extends AbstractSkeletonEntity {
    private WitherSkeletonMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"),method = "initGoals")
    private void setOverworldMobAsTarget(CallbackInfo ci){
        this.targetSelector.add(5,new SeekHigherPlaceGoal(this,0.8f));
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false, DimensionConfusionCore::isOverworldMob));
    }
}
