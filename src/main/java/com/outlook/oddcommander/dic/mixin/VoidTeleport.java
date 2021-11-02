package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class VoidTeleport{
    @Shadow
    public World world;

    @Shadow public float pitch;

    @Shadow public float yaw;

    @Shadow @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/Entity;tickInVoid()V"),method = "baseTick",cancellable = true)
    private void onVoidDamage(CallbackInfo ci){
        if(DimensionConfusionCore.isOverworld(this.world.getDimension()) && !this.world.isClient){
            if(this.getServer()!=null) {
                ServerWorld nether = this.getServer().getWorld(World.NETHER);
                BlockPos.Mutable mutable = new BlockPos.Mutable(this.getX(),0,this.getZ());
                if(nether!=null){
                    for(int y = 255;y >= 128;y--){
                        mutable.setY(y);
                        if(!nether.getBlockState(mutable).isAir()){
                            break;
                        }
                    }
                    mutable.setY(mutable.getY() + 30);
                    DimensionConfusionCore.teleport((Entity) (Object) this,nether,this.getX(),mutable.getY(),this.getZ(),this.yaw,this.pitch);
                    ci.cancel();
                }
            }
        }
    }
}
