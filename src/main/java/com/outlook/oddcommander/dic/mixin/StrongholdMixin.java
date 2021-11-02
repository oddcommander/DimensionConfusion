package com.outlook.oddcommander.dic.mixin;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Random;

@Mixin(StrongholdGenerator.class)
public abstract class StrongholdMixin {
    @Mixin(targets = "net.minecraft.structure.StrongholdGenerator$StoneBrickRandomizer")
    static abstract class StoneBrickRandomizerMixin extends StructurePiece.BlockRandomizer {
        @Inject(method = "setBlock(Ljava/util/Random;IIIZ)V",at = @At(value = "RETURN"))
        private void changeBlock(Random random, int x, int y, int z, boolean placeBlock, CallbackInfo ci){
            this.block = DimensionConfusionCore.toNetherState(this.block);
        }
    }

    @Mixin(StrongholdGenerator.PortalRoom.class)
    public static class PortalRoomMixin{
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$PortalRoom;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }

        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/MobSpawnerLogic;setEntityId(Lnet/minecraft/entity/EntityType;)V"),index = 0)
        private EntityType<?> changeSilverfish(EntityType<?> old){
            return DimensionConfusionCore.NETHER_SILVERFISH;
        }
    }

    @Mixin(StrongholdGenerator.FiveWayCrossing.class)
    public static class FiveWayMixin{
        @ModifyArgs(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$FiveWayCrossing;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V"))
        private void changeBlock(Args args){
            BlockState replace = DimensionConfusionCore.toNetherState(args.get(8));
            args.set(8,replace);
            args.set(9,replace);
        }

        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$FiveWayCrossing;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.Library.class)
    public static class LibraryMixin{
        @ModifyArgs(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$Library;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V"))
        private void changeBlock(Args args){
            BlockState replace = DimensionConfusionCore.toNetherState(args.get(8));
            args.set(8,replace);
            args.set(9,replace);
        }

        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$Library;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.SquareRoom.class)
    public static class SquareRoomMixin{
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$SquareRoom;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.Stairs.class)
    public static class StairsMixin {
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$Stairs;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.ChestCorridor.class)
    public static class ChestCorridorMixin{
        @ModifyArgs(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$ChestCorridor;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V"))
        private void changeBlock(Args args){
            BlockState replace = DimensionConfusionCore.toNetherState(args.get(8));
            args.set(8,replace);
            args.set(9,replace);
        }

        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$ChestCorridor;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.Corridor.class)
    public static class CorridorMixin{
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$Corridor;addBlockWithRandomThreshold(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;Ljava/util/Random;FIIILnet/minecraft/block/BlockState;)V"),index = 7)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.SpiralStaircase.class)
    public static class StairCaseMixin{
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$SpiralStaircase;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(StrongholdGenerator.SmallCorridor.class)
    public static class SmallCorridorMixin{
        @ModifyArg(method = "generate",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$SmallCorridor;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }

    @Mixin(targets = "net.minecraft.structure.StrongholdGenerator$Piece")
    static abstract class PieceMixin extends StructurePiece{
        private PieceMixin(StructurePieceType type, int length) {
            super(type, length);
        }

        @ModifyArg(method = "generateEntrance(Lnet/minecraft/world/StructureWorldAccess;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/structure/StrongholdGenerator$Piece$EntranceType;III)V",at = @At(value = "INVOKE",target = "Lnet/minecraft/structure/StrongholdGenerator$Piece;addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"),index = 1)
        private BlockState changeBlock(BlockState old){
            return DimensionConfusionCore.toNetherState(old);
        }
    }
}
