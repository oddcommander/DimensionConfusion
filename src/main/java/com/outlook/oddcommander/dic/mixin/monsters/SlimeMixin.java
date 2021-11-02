package com.outlook.oddcommander.dic.mixin.monsters;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntity.class)
public abstract class SlimeMixin extends MobEntity {
    private SlimeMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract void damage(LivingEntity target);

    @Shadow
    protected abstract boolean canAttack();

    @Inject(method = "pushAwayFrom",at = @At("HEAD"),cancellable = true)
    public void enableDamage(Entity entity, CallbackInfo ci){
        super.pushAwayFrom(entity);
        if(entity instanceof LivingEntity && !(entity instanceof PlayerEntity) && this.canAttack()){
            if((MobEntity)this instanceof MagmaCubeEntity){
                LivingEntity livingEntity = (LivingEntity) entity;
                if(DimensionConfusionCore.isOverworldMob(livingEntity)){
                    this.damage(livingEntity);
                }
            }
        }
        ci.cancel();
    }
}
