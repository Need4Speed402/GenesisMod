package genesis.genesis.block.trees;

import java.util.ArrayList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import genesis.genesis.block.BlockAndMeta;
import genesis.genesis.block.plants.BlockGenesisFlowerPot;
import genesis.genesis.common.Genesis;
import genesis.genesis.item.ItemGenesisAxe;
import genesis.genesis.item.ItemGenesisHoe;
import genesis.genesis.item.ItemGenesisPickaxe;
import genesis.genesis.item.ItemGenesisSpade;
import genesis.genesis.item.ItemGenesisSword;
import genesis.genesis.item.itemblock.ItemBlockGenesisTree;
import genesis.genesis.lib.IDs;
import genesis.genesis.lib.Names;
import genesis.genesis.world.WorldGenTreeAraucarioxylon;
import genesis.genesis.world.WorldGenTreeBase;
import genesis.genesis.world.WorldGenTreeCordaites;
import genesis.genesis.world.WorldGenTreeLepidodendron;
import genesis.genesis.world.WorldGenTreePsaronius;
import genesis.genesis.world.WorldGenTreeSigillaria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class TreeBlocks {

	public static final String SIGIL_NAME = "sigillaria";
	public static final String LEPID_NAME = "lepidodendron";
	public static final String CORD_NAME = "cordaites";
	public static final String ARAU_NAME = "araucarioxylon";
	public static final String PSAR_NAME = "psaronius";
	
	public static final ArrayList<String> woodTypes = new ArrayList() {{
		add(SIGIL_NAME);
		add(LEPID_NAME);
		add(CORD_NAME);
		add(ARAU_NAME);
		add(PSAR_NAME);
	}};
	public static final int woodTypeCount = woodTypes.size();
	private static ArrayList<WorldGenTreeBase> treeGenerators = new ArrayList(woodTypeCount);
	
	public static final int setSize = 4;
	
	public static Block[] blocksLogs = new Block[IDs.TREE_BLOCK_COUNT];
	public static Block[] blocksSaplings = new Block[IDs.TREE_BLOCK_COUNT];
	public static Block[] blocksLeaves = new Block[IDs.TREE_BLOCK_COUNT];
	public static Block[] blocksWoods = new Block[IDs.TREE_BLOCK_COUNT];
	public static Block[] blocksStairs = new Block[IDs.TREE_BLOCK_COUNT];
	public static Block[] blocksRottenLogs = new Block[IDs.TREE_BLOCK_COUNT];
	
	public static BlockStairsSet woodStairs;
	
	public static void init()
	{
		for (int set = 0; set < IDs.TREE_BLOCK_COUNT; set++)
		{
			blocksLogs[set] = new BlockGenesisLog(IDs.blockLogID.getID(set), set)
					.setUnlocalizedName(Names.blockLogGenesis);
			
			blocksSaplings[set] = new BlockGenesisSapling(IDs.blockSaplingID.getID(set), set)
					.setUnlocalizedName(Names.blockSaplingGenesis);
			
			blocksLeaves[set] = new BlockGenesisLeaves(IDs.blockLeavesID.getID(set), set)
					.setUnlocalizedName(Names.blockLeavesGenesis);
			
			blocksWoods[set] = new BlockGenesisWood(IDs.blockWoodID.getID(set), set)
					.setUnlocalizedName(Names.blockWoodGenesis);
			
			blocksRottenLogs[set] = new BlockRottenLog(IDs.blockRottenLogID.getID(set), set)
				.setUnlocalizedName(Names.blockRottenLogGenesis);
		}
		
		woodStairs = new BlockStairsSet(IDs.blockStairsStartID, blocksWoods[0]);
		blocksStairs = (Block[])woodStairs.stairs;
	}
	
	public static void registerBlocks()
	{
		for (int set = 0; set < IDs.TREE_BLOCK_COUNT; set++)
		{
			GameRegistry.registerBlock(blocksLogs[set], ItemBlockGenesisTree.class, Genesis.MOD_ID + "." + Names.blockLogGenesis + set);
			
			GameRegistry.registerBlock(blocksSaplings[set], ItemBlockGenesisTree.class, Genesis.MOD_ID + "." + Names.blockSaplingGenesis + set);
			
			int start = set * TreeBlocks.setSize;
			int end = Math.min((set + 1) * TreeBlocks.setSize, TreeBlocks.woodTypeCount);
			
			for (int i = start; i < end; i++)
			{
				BlockGenesisFlowerPot.tryRegisterPlant(new ItemStack(blocksSaplings[set].blockID, 1, i - start));
			}
			
			GameRegistry.registerBlock(blocksLeaves[set], ItemBlockGenesisTree.class, Genesis.MOD_ID + "." + Names.blockLeavesGenesis + set);
			
			GameRegistry.registerBlock(blocksWoods[set], ItemBlockGenesisTree.class, Genesis.MOD_ID + "." + Names.blockWoodGenesis + set);
			
			GameRegistry.registerBlock(blocksRottenLogs[set], ItemBlockGenesisTree.class, Genesis.MOD_ID + "." + Names.blockRottenLogGenesis + set);
			
			OreDictionary.registerOre("logWood", new ItemStack(blocksLogs[set], 1, OreDictionary.WILDCARD_VALUE));
			OreDictionary.registerOre("plankWood", new ItemStack(blocksWoods[set], 1, OreDictionary.WILDCARD_VALUE));
			GameRegistry.addSmelting(blocksLogs[set].blockID, new ItemStack(Item.coal, 1, 1), 0.15F);
		}
		
		for (int type = 0; type < woodTypeCount; type++)
		{
			GameRegistry.registerBlock(blocksStairs[type], Genesis.MOD_ID + "." + Names.blockStairsGenesis + type);
			
			GameRegistry.addShapelessRecipe(new ItemStack(blocksWoods[type/setSize], 4, type%setSize), 
					new ItemStack(blocksLogs[type/setSize], 1, type%setSize));
			
			GameRegistry.addRecipe(new ItemStack(blocksStairs[type], 1),  
					"P  ",
					"PP ",
					"PPP",
					'P', new ItemStack(blocksWoods[type/setSize], 1, type%setSize));
			GameRegistry.addRecipe(new ItemStack(blocksStairs[type], 1),  
					"  P",
					" PP",
					"PPP",
					'P', new ItemStack(blocksWoods[type/setSize], 1, type%setSize));
		}
		
		treeGenerators.add(new WorldGenTreeSigillaria(8, 3, true));
		treeGenerators.add(new WorldGenTreeLepidodendron(10, 5, true));
		treeGenerators.add(new WorldGenTreeCordaites(15, 5, true));
		treeGenerators.add(new WorldGenTreeAraucarioxylon(20, 7, true));
		treeGenerators.add(new WorldGenTreePsaronius(5, 4, true));
	}

	public static enum TreeBlockType {
		LOG,
		LEAVES,
		SAPLING,
		WOOD,
		STAIRS,
		ROTTEN_LOG;
	}
	
	public static BlockAndMeta getBlockForType(TreeBlockType type, String name)
	{
		int index = woodTypes.indexOf(name);
		int set = index / setSize;
		int metadata = index % setSize;
		Block block = null;
		
		switch (type)
		{
		case LOG:
			block = blocksLogs[set];
			break;
		case LEAVES:
			block = blocksLeaves[set];
			break;
		case SAPLING:
			block = blocksSaplings[set];
			break;
		case WOOD:
			block = blocksWoods[set];
			break;
		case STAIRS:
			block = blocksWoods[set];
			break;
		case ROTTEN_LOG:
			block = blocksRottenLogs[set];
			break;
		}
		
		if (block != null)
			return new BlockAndMeta(block.blockID, metadata);
		
		return null;
	}
	
	public static int getLogMetadataForDirection(int logMetadata, ForgeDirection direction)
	{
		int directionBits = 0;
		
		switch (direction)
		{
		case NORTH:
		case SOUTH:
			directionBits = 4;
			break;
		case EAST:
		case WEST:
			directionBits = 8;
			break;
		case UNKNOWN:
			directionBits = 12;
			break;
		default:
			directionBits = 0;
			break;
		}
		
		return logMetadata + directionBits;
	}
	
	public static WorldGenTreeBase getTreeGenerator(String treeName)
	{
		return treeGenerators.get(woodTypes.indexOf(treeName));
	}
	
	public static class BlockStairsSet {
		public BlockGenesisStairs[] stairs;
		
		public BlockStairsSet(int startID, Block modelBlock)
		{
			stairs = new BlockGenesisStairs[woodTypeCount];
			
			for(int set = 0; set < woodTypeCount; set++){
					stairs[set] = new BlockGenesisStairs(startID + set, modelBlock, set);
					stairs[set].setUnlocalizedName("genesis.stairs." + woodTypes.get(set));
			}
		}
	}
	
}
