package com.outlook.oddcommander.dic;

import com.outlook.oddcommander.dic.block.NetherInfestedBlock;
import com.outlook.oddcommander.dic.entity.NetherSilverfishEntity;
import com.outlook.oddcommander.dic.mixin.accessors.DicSpawnRestrictionAccessor;
import com.outlook.oddcommander.dic.mixin.accessors.DimensionTypeAccessor;
import com.outlook.oddcommander.dic.util.SpawnPredicate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

import java.util.function.Predicate;

public final class DimensionConfusionCore implements ModInitializer {
	public static final String MODID = "dic";
	public static final String MC = "minecraft";

	public static final Block INFESTED_BLACKSTONE = new NetherInfestedBlock(Blocks.POLISHED_BLACKSTONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0f,0.75f));
	public static final Block INFESTED_BLACKSTONE_BRICKS = new NetherInfestedBlock(Blocks.POLISHED_BLACKSTONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0f,0.75f));
	public static final Block INFESTED_CRACKED_BLACKSTONE_BRICKS = new NetherInfestedBlock(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0f,0.75f));
	public static final Block INFESTED_CHISELED_BLACKSTONE = new NetherInfestedBlock(Blocks.CHISELED_POLISHED_BLACKSTONE, AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT).strength(0f,0.75f));

	private static final Tag<EntityType<?>> OVERWORLD_ANIMALS = TagRegistry.entityType(new Identifier(MC,"overworld_animals"));
	private static final Tag<EntityType<?>> OVERWORLD_MONSTERS = TagRegistry.entityType(new Identifier(MC,"overworld_monsters"));
	public static final Tag<EntityType<?>> OVERWORLD_MOBS = TagRegistry.entityType(new Identifier(MC,"overworld_mobs"));
	public static final Tag<EntityType<?>> NETHER_MOBS = TagRegistry.entityType(new Identifier(MC,"nether_mobs"));

	public static final EntityType<NetherSilverfishEntity> NETHER_SILVERFISH = Registry.register(Registry.ENTITY_TYPE,new Identifier(MODID,"nether_silverfish"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER,NetherSilverfishEntity::new).dimensions(EntityDimensions.fixed(0.4f,0.3f)).fireImmune().build());

	private static final Predicate<BiomeSelectionContext> NETHER = context -> BiomeSelectors.foundInTheNether().test(context) && !context.getBiomeKey().equals(BiomeKeys.CRIMSON_FOREST);

	@Override
	public void onInitialize(){
		registerBlocks();
		registerItems();
		FabricDefaultAttributeRegistry.register(NETHER_SILVERFISH, SilverfishEntity.createSilverfishAttributes());
		BiomeModifications.create(new Identifier(MODID,"overworld_spawn")).add(ModificationPhase.ADDITIONS,BiomeSelectors.foundInOverworld(),DimensionConfusionCore::spawnNetherMonsters);
		BiomeModifications.create(new Identifier(MODID,"nether_stronghold")).add(ModificationPhase.ADDITIONS,NETHER,DimensionConfusionCore::spawnEndMonsters);
	}

	private void registerBlocks(){
		Registry.register(Registry.BLOCK,new Identifier(MODID,"infested_blackstone"),INFESTED_BLACKSTONE);
		Registry.register(Registry.BLOCK,new Identifier(MODID,"infested_blackstone_bricks"),INFESTED_BLACKSTONE_BRICKS);
		Registry.register(Registry.BLOCK,new Identifier(MODID,"infested_cracked_blackstone_bricks"),INFESTED_CRACKED_BLACKSTONE_BRICKS);
		Registry.register(Registry.BLOCK,new Identifier(MODID,"infested_chiseled_blackstone"),INFESTED_CHISELED_BLACKSTONE);
	}

	private void registerItems(){
		Registry.register(Registry.ITEM,new Identifier(MODID,"infested_blackstone"),new BlockItem(INFESTED_BLACKSTONE,new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM,new Identifier(MODID,"infested_blackstone_bricks"),new BlockItem(INFESTED_BLACKSTONE_BRICKS,new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM,new Identifier(MODID,"infested_cracked_blackstone_bricks"),new BlockItem(INFESTED_CRACKED_BLACKSTONE_BRICKS,new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM,new Identifier(MODID,"infested_chiseled_blackstone"),new BlockItem(INFESTED_CHISELED_BLACKSTONE,new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM,new Identifier(MODID,"nether_silverfish_spawn_egg"),new SpawnEggItem(NETHER_SILVERFISH,0,14423100,new FabricItemSettings().group(ItemGroup.MISC)));
	}

	public static boolean isOverworld(DimensionType type){
		return DimensionTypeAccessor.getOverworld() == type;
	}

	public static boolean isNether(DimensionType type){
		return DimensionTypeAccessor.getNether() == type;
	}

	public static boolean isEnd(DimensionType type){
		return DimensionTypeAccessor.getTheEnd() == type;
	}

	private static void spawnNetherMonsters(BiomeModificationContext context){
		BiomeModificationContext.SpawnSettingsContext spawnSettings = context.getSpawnSettings();
		if(!DicSpawnRestrictionAccessor.getRestrictions().containsKey(EntityType.ZOGLIN)){
			SpawnRestrictionAccessor.callRegister(EntityType.ZOGLIN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, serverWorldAccess, spawnReason, pos, random) -> SpawnPredicate.OVERWORLD.test(serverWorldAccess,pos,spawnReason));
		}
		if(!DicSpawnRestrictionAccessor.getRestrictions().containsKey(EntityType.PIGLIN_BRUTE)){
			SpawnRestrictionAccessor.callRegister(EntityType.PIGLIN_BRUTE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ((type, serverWorldAccess, spawnReason, pos, random) -> SpawnPredicate.OVERWORLD.test(serverWorldAccess,pos,spawnReason)));
		}
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.PIGLIN,120,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN,200,1,4));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.PIGLIN_BRUTE,60,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.HOGLIN,80,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.ZOGLIN,160,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.GHAST,200,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON,200,1,3));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.BLAZE,60,1,1));
		spawnSettings.addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE,210,1,2));
	}

	private static void spawnEndMonsters(BiomeModificationContext context){
		context.getGenerationSettings().addBuiltInStructure(ConfiguredStructureFeatures.STRONGHOLD);
		context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.ENDERMAN,50,1,2));
		context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER,new SpawnSettings.SpawnEntry(EntityType.SHULKER,150,1,3));
	}

	public static boolean isOverworldMob(LivingEntity entity){
		return OVERWORLD_MOBS.contains(entity.getType());
	}

	public static boolean isNetherMob(LivingEntity entity){
		return NETHER_MOBS.contains(entity.getType());
	}

	public static void teleport(Entity entity, ServerWorld world, double x, double y, double z, float yaw, float pitch){
		if(entity.world == world) entity.refreshPositionAndAngles(x, y, z, yaw, pitch);
		else {
			if(entity instanceof ServerPlayerEntity){
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				player.teleport(world,x,y,z,yaw,pitch);
			}
			else {
				Entity after = entity.getType().create(world);
				if(after!=null){
					entity.detach();
					after.copyFrom(entity);
					after.refreshPositionAndAngles(x, y, z, yaw, pitch);
					after.setHeadYaw(yaw);
					world.onDimensionChanged(after);
				}
				entity.remove();
			}
		}
	}

	public static BlockState toNetherState(BlockState old){
		Block block = old.getBlock();
		if(block == Blocks.OAK_FENCE){
			boolean east = old.get(FenceBlock.EAST), west = old.get(FenceBlock.WEST), south = old.get(FenceBlock.SOUTH), north = old.get(FenceBlock.NORTH);
			return Blocks.CRIMSON_FENCE.getDefaultState().with(FenceBlock.NORTH,north).with(FenceBlock.SOUTH,south).with(FenceBlock.EAST,east).with(FenceBlock.WEST,west);
		}
		if(block == Blocks.STONE_BUTTON){
			Direction direction = old.get(WallTorchBlock.FACING);
			return Blocks.POLISHED_BLACKSTONE_BUTTON.getDefaultState().with(WallTorchBlock.FACING,direction);
		}
		if(block == Blocks.OAK_DOOR){
			DoubleBlockHalf half = old.get(DoorBlock.HALF);
			Direction face = old.get(DoorBlock.FACING);
			DoorHinge hinge = old.get(DoorBlock.HINGE);
			return Blocks.CRIMSON_DOOR.getDefaultState().with(DoorBlock.HALF,half).with(DoorBlock.FACING,face).with(DoorBlock.HINGE,hinge);
		}
		if(block == Blocks.SMOOTH_STONE_SLAB){
			SlabType type = old.get(SlabBlock.TYPE);
			return Blocks.BLACKSTONE_SLAB.getDefaultState().with(SlabBlock.TYPE,type);
		}
		if(block == Blocks.STONE_BRICK_SLAB){
			SlabType type = old.get(SlabBlock.TYPE);
			return Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE,type);
		}
		if(block == Blocks.COBBLESTONE_STAIRS){
			Direction direction = old.get(StairsBlock.FACING);
			return Blocks.BLACKSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING,direction);
		}
		if(block == Blocks.STONE_BRICK_STAIRS){
			Direction direction = old.get(StairsBlock.FACING);
			return Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING,direction);
		}
		if(block == Blocks.WALL_TORCH){
			Direction direction = old.get(WallTorchBlock.FACING);
			return Blocks.SOUL_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING,direction);
		}
		if(block == Blocks.WATER) return Blocks.LAVA.getDefaultState();
		if(block == Blocks.TORCH) return Blocks.SOUL_TORCH.getDefaultState();
		if(block == Blocks.OAK_PLANKS) return Blocks.CRIMSON_PLANKS.getDefaultState();
		if(block == Blocks.STONE_BRICKS) return Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState();
		if(block == Blocks.CRACKED_STONE_BRICKS) return Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState();
		if(block == Blocks.MOSSY_STONE_BRICKS) return Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState();
		if(block == Blocks.INFESTED_STONE_BRICKS) return INFESTED_BLACKSTONE_BRICKS.getDefaultState().with(NetherInfestedBlock.IS_SILVERFISH,false);
		if(block == Blocks.COBBLESTONE) return Blocks.BLACKSTONE.getDefaultState();
		return old;
	}
}
