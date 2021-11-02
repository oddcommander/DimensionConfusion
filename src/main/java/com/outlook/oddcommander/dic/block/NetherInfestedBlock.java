package com.outlook.oddcommander.dic.block;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.entity.NetherSilverfishEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

import java.util.HashMap;
import java.util.Map;

public class NetherInfestedBlock extends InfestedBlock {
    public static final BooleanProperty IS_SILVERFISH = BooleanProperty.of("is_silverfish");
    private static final Map<Block,Block> NETHER_INFESTABLE = new HashMap<>();

    public NetherInfestedBlock(Block regularBlock, Settings settings) {
        super(regularBlock, settings);
        NETHER_INFESTABLE.put(regularBlock,this);
        this.setDefaultState(this.getStateManager().getDefaultState().with(IS_SILVERFISH,true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_SILVERFISH);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if(state.get(IS_SILVERFISH)) super.onBroken(world, pos, state);
        else if(world instanceof ServerWorld) spawnNetherSilverfish((ServerWorld) world,pos);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        if(state.get(IS_SILVERFISH)) super.onStacksDropped(state, world, pos, stack);
        else spawnNetherSilverfish(world,pos);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
    }

    public void spawnNetherSilverfish(ServerWorld world, BlockPos pos){
        NetherSilverfishEntity netherSilverfish = DimensionConfusionCore.NETHER_SILVERFISH.create(world);
        if(netherSilverfish==null) return;
        netherSilverfish.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        world.spawnEntity(netherSilverfish);
        netherSilverfish.playSpawnEffects();
    }

    public static boolean isNetherInfestable(BlockState blockState){
        return NETHER_INFESTABLE.containsKey(blockState.getBlock());
    }

    public static BlockState fromRegularBlock(Block block,boolean isSilverfish){
        return NETHER_INFESTABLE.get(block).getDefaultState().with(IS_SILVERFISH,isSilverfish);
    }
}
