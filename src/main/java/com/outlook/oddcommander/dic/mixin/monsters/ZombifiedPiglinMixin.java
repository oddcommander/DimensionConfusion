package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceGoal;
import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Predicate;

@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinMixin extends ZombieEntity {
    private ZombifiedPiglinMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/goal/FollowTargetGoal;<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V"),method = "initCustomGoals",index = 5)
    private Predicate<LivingEntity> setAngryAtPlayer(Predicate<LivingEntity> old){
        return old.or(entity -> entity instanceof PlayerEntity && DimensionConfusionCore.isOverworld(entity.world.getDimension()));
    }

    @Inject(at = @At("RETURN"),method = "initCustomGoals")
    private void setAngryAtOverworldMobs(CallbackInfo ci){
        this.targetSelector.add(5,new SeekHigherPlaceGoal(this,0.8f));
        this.targetSelector.add(2,new FollowTargetGoal<>(this, MobEntity.class,10,true,false,DimensionConfusionCore::isOverworldMob));
    }

    @Inject(at = @At("HEAD"),method = "canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z",cancellable = true)
    private static void setOverworldSpawnCondition(EntityType<HoglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
        boolean canSpawn = SpawnPredicate.OVERWORLD.test(world,pos,spawnReason);
        if(!canSpawn){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
