package genesis.genesis.world;

import genesis.genesis.block.trees.BlockGenesisSapling;
import genesis.genesis.block.trees.TreeBlocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ForgeDirection;

public class WorldGenTreePsaronius extends WorldGenTreeBase
{

    /** Constructor
     * @param minH minimum height of tree trunk
     * @param maxH max possible height above minH the tree trunk could grow
     * @param notify whether or not to notify blocks of the tree being grown.
     *  Generally false for world generation, true for saplings.
     */
    public WorldGenTreePsaronius(int minH, int maxH, boolean notify)
    {
    	super(0, 0, TreeBlocks.blocksLogs[1].blockID, TreeBlocks.blocksLeaves[1].blockID, notify);
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
        if(!isCubeClear(locX, locY+2, locZ, 1, 15))return false;
        
    	//generates the trunk
    	locY++;
    	int treeHeight = minHeight + random.nextInt(maxHeight);
    	
    	//Generate trunk
		for(int i = 0; i < treeHeight; i++){
    		setBlockInWorld(locX, locY + i, locZ, this.woodID, this.woodMeta);
    	}
		
		
		//Generate leaves
		int currentHeight = locY + treeHeight-1;
		setBlockInWorld(locX, currentHeight+1, locZ, this.leavesID, this.leavesMeta);
		int[] heights = {0, 1, 1, 0};
		int radius;
		for(radius = 1; radius < heights.length; radius++){
			for(int dir = 0; dir < 8; dir++){
				setBlockInWorld(locX+radius*directions[dir][0], currentHeight+heights[radius],
						locZ+radius*directions[dir][1], this.leavesID, this.leavesMeta);
			}
		}
		for(int dir = 1; dir < 8; dir+=2){
			setBlockInWorld(locX+radius*directions[dir][0], currentHeight,
					locZ+radius*directions[dir][1], this.leavesID, this.leavesMeta);
		}
		
    	
    	return true;
    }
}













