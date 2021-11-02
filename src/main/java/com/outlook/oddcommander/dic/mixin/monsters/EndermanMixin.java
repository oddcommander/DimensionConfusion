package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin extends HostileEntity
{
    private EndermanMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("RETURN"),method = "initGoals")
    private void setAngryAtNetherMobs(CallbackInfo ci){
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false, DimensionConfusionCore::isNetherMob));
    }

    @Inject(method = "isPlayerStaring",at = @At("HEAD"),cancellable = true)
    private void setAngryAtPlayerInNether(PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if(DimensionConfusionCore.isNether(player.world.getDimension())) cir.setReturnValue(true);
    }
}
