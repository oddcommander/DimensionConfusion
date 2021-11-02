package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlazeEntity.class)
public abstract class BlazeMixin extends HostileEntity {
    private BlazeMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("RETURN"),method = "initGoals")
    private void setAngryAtOverworldMobs(CallbackInfo ci){
        this.targetSelector.add(5,new SeekHigherPlaceGoal(this,0.8f));
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false, DimensionConfusionCore::isOverworldMob));
    }
}
