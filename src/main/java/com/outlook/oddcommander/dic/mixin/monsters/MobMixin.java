package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MobEntity.class)
public abstract class MobMixin extends LivingEntity{
    private MobMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"),method = "canMobSpawn",cancellable = true)
    private static void setShulkerSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
        if(type == EntityType.SHULKER){
            boolean canSpawn = SpawnPredicate.NETHER.test(world,pos,spawnReason);
            if(!canSpawn){
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
