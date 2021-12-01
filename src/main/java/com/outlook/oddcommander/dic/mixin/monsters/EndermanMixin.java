package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

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

    @Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$ChasePlayerGoal")
    static abstract class ChasePlayerGoalMixin extends Goal {
        @Shadow
        private LivingEntity target;

        @Inject(method = "canStart()Z",at = @At(value = "RETURN",ordinal = 1),cancellable = true)
        private void setAngryAtPlayerInNether(CallbackInfoReturnable<Boolean> cir){
            cir.setReturnValue(cir.getReturnValueZ() || (DimensionConfusionCore.isNether(target.world.getDimension()) && target instanceof PlayerEntity));
        }
    }

    @Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$TeleportTowardsPlayerGoal")
    static abstract class TeleportMixin extends FollowTargetGoal<PlayerEntity>{
        @Final
        @Shadow
        private EndermanEntity enderman;

        private TeleportMixin(MobEntity mob, Class<PlayerEntity> targetClass, boolean checkVisibility) {
            super(mob, targetClass, checkVisibility);
        }

        @ModifyArg(method = "<init>(Lnet/minecraft/entity/mob/EndermanEntity;Ljava/util/function/Predicate;)V",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/TargetPredicate;setPredicate(Ljava/util/function/Predicate;)Lnet/minecraft/entity/ai/TargetPredicate;"),index = 0)
        private Predicate<LivingEntity> setPredicate(Predicate<LivingEntity> old){
            return old.or(entity -> DimensionConfusionCore.isNether(entity.world.getDimension()));
        }

        @Inject(method = "shouldContinue()Z",at = @At(value = "RETURN",ordinal = 0),cancellable = true)
        private void setAngryAtPlayerInNether(CallbackInfoReturnable<Boolean> cir){
            cir.setReturnValue(DimensionConfusionCore.isNether(enderman.world.getDimension()));
        }
    }
}
