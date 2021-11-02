package com.outlook.oddcommander.dic.mixin.monsters;

import com.google.common.collect.ImmutableList;
import com.outlook.oddcommander.dic.task.SeekHigherPlaceTask;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZoglinEntity.class)
public abstract class ZoglinMixin extends HostileEntity {
    private ZoglinMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "method_26928",at = @At("HEAD"),cancellable = true)
    private static void addTask(Brain<ZoglinEntity> brain, CallbackInfo ci){
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new SeekHigherPlaceTask(0.8f)));
        ci.cancel();
    }
}
