package genesis.genesis.world;

import genesis.genesis.block.trees.BlockGenesisSapling;
import genesis.genesis.block.trees.TreeBlocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ForgeDirection;

public class WorldGenTreeAraucarioxylon extends WorldGenTreeBase
{

    /** Constructor
     * @param minH minimum height of tree trunk
     * @param maxH max possible height above minH the tree trunk could grow
     * @param notify whether or not to notify blocks of the tree being grown.
     *  Generally false for world generation, true for saplings.
     */
    public WorldGenTreeAraucarioxylon(int minH, int maxH, boolean notify)
    {
    	super(3, 3, TreeBlocks.blocksLogs[0].blockID, TreeBlocks.blocksLeaves[0].blockID, notify);
        minHeight = minH;
        maxHeight = maxH;
    }

    public boolean generate(World world, Random random, int locX, int locY, int locZ)
    {
    	this.world = world;
    	this.random = random;
    	

    	//finds top block for the given x,z position (excluding leaves and grass)
        for (boolean var6 = false; (world.getBlockId(locX, locY, locZ) == 0 ||
        		Block.blocksList[world.getBlockId(locX, locY, locZ)].isLeaves(world, locX, locY, locZ) && locY > 0); --locY);
        //locY is now the highest solid terrain block
        
        Block soil = Block.blocksList[world.getBlockId(locX, locY, locZ)];
        if(soil == null || !soil.canSustainPlant(world, locX, locY, locZ, ForgeDirection.UP, 
				(BlockGenesisSapling)TreeBlocks.blocksSaplings[0]))return false;
        if(!isCubeClear(locX, locY+2, locZ, 3, 15))return false;
        
    	//generates the trunk
    	locY++;
    	int treeHeight = minHeight + random.nextInt(maxHeight);
    	
    	//Generate trunk
		for(int i = 0; i < treeHeight; i++){
    		setBlockInWorld(locX, locY + i, locZ, this.woodID, this.woodMeta);
    	}
		
		//Generate leaves
		int currentHeight = locY + treeHeight - 2;
		generateLeafLayerCircleNoise(world, random, 3.5, locX, locZ, currentHeight);
		generateLeafLayerCircleNoise(world, random, 2.5, locX, locZ, currentHeight+1);
		generateLeafLayerCircleNoise(world, random, 1.5, locX, locZ, currentHeight+2);
		setBlockInWorld(locX, currentHeight+3, locZ, this.leavesID, this.leavesMeta);
    	
		//generates branches with leaves
		int firstDir = random.nextInt(4);
		int radius = 12;
    	for(int i = currentHeight; i > locY + 6; i--){
    		int[] xyz = generateStraightBranchDown(world, random, radius/6, locX, i, locZ, (firstDir + i)%4);
    		generateLeafLayerCircleNoise(world, random, 1, xyz[0], xyz[2], xyz[1]-1);
    		generateLeafLayerCircleNoise(world, random, 1.5, xyz[0], xyz[2], xyz[1]);
    		generateLeafLayerCircleNoise(world, random, 1, xyz[0], xyz[2], xyz[1]+1);
    		radius++;
    	}
		
		
    	return true;
    }
}













