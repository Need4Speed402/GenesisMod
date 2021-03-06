package genesis.genesis.block.plants;

import genesis.genesis.block.BlockAndMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class BlockGenesisFlowerPot extends BlockFlowerPot {

	public static final HashSet<BlockFlowerPot> potBlocks = new HashSet();
	private final HashMap<ItemStack, Integer> metadataMap = new HashMap();
	private final HashMap<Integer, BlockAndMeta> plantBlockCache = new HashMap();
	private int currentID = 1;
	
	public BlockGenesisFlowerPot(int blockID) {
		super(blockID);
		
		if (potBlocks.isEmpty())
			potBlocks.add((BlockFlowerPot)Block.flowerPot);
		
		potBlocks.add(this);
	}
	
    public int getRenderType()
    {
        return BlockGenesisFlowerPotRenderer.renderID;
    }
    
    public boolean registerPlant(ItemStack plantStack)
    {
		if (metadataMap.size() < 14)
		{
			metadataMap.put(plantStack, currentID);
			currentID++;
			return true;
		}
		
		return false;
    }
    
    public static boolean tryRegisterPlant(ItemStack plantStack)
    {
    	if (Block.blocksList[plantStack.itemID] instanceof IPlantInFlowerPot)
    	{
	    	for (BlockFlowerPot potBlock : potBlocks)
	    	{
	    		if (potBlock instanceof BlockGenesisFlowerPot)
	    		{
		    		if (((BlockGenesisFlowerPot)potBlock).registerPlant(plantStack))
		    		{
		    			return true;
		    		}
	    		}
	    	}
    	}
    	
    	return false;
    }
    
    public int getPlantMetadata(ItemStack stack)
    {
    	for (Entry<ItemStack, Integer> entry : metadataMap.entrySet())
    	{
    		ItemStack keyStack = entry.getKey();
    		
    		if (keyStack.itemID == stack.itemID && keyStack.getItemDamage() == stack.getItemDamage())
    		{
    			return entry.getValue();
    		}
    	}
    	
    	return -1;
    }
    
    public ItemStack getPlantStack(int metadata)
    {
    	for (Entry<ItemStack, Integer> entry : metadataMap.entrySet())
    	{
    		if (entry.getValue().intValue() == metadata)
    		{
    			return entry.getKey();
    		}
    	}
    	
    	return null;
    }
    
    public BlockAndMeta getPlantBlock(int metadata)
    {
    	BlockAndMeta blockAndMeta = plantBlockCache.get(metadata);
    	
    	if (blockAndMeta == null)
    	{
    		ItemStack stack = getPlantStack(metadata);
    		
    		if (stack != null)
    		{
	    		ItemBlock itemBlock = (ItemBlock)stack.getItem();
	    		
	    		int plantMetadata = itemBlock.getMetadata(stack.getItemDamage());
	    		
	    		blockAndMeta = new BlockAndMeta(stack.itemID, plantMetadata);
	    		plantBlockCache.put(metadata, blockAndMeta);
    		}
    	}
    	
    	return blockAndMeta;
    }
    
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
    		int side, float hitX, float hitY, float hitZ)
    {
    	if (world.getBlockMetadata(x, y, z) == 0)
        {
	        ItemStack stack = player.getHeldItem();
	        
	        if (stack != null)
	        {
        		int metadata = getPlantMetadata(stack);
        		
        		if (metadata > 0)
        		{
	                world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	                
	                if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0)
	                {
	                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
	                }
	                
	                return true;
        		}
	        }
	        
	        int oldMetadata = world.getBlockMetadata(x, y, z);
	        
	        int worldBlockID = world.getBlockId(x, y, z);
	        
	        for (BlockFlowerPot pot : potBlocks)
	        {
		        if (pot != this && pot.blockID != worldBlockID && pot.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
		        {
		        	world.setBlock(x, y, z, pot.blockID, world.getBlockMetadata(x, y, z), 2);
		        	return true;
		        }
	        }
	        
	        world.setBlock(x, y, z, blockID, oldMetadata, 2);
        }
    	
    	return false;
    }
    
    public int idPicked(World world, int x, int y, int z)
    {
        ItemStack stack = getPlantStack(world.getBlockMetadata(x, y, z));
        
        return stack == null ? Item.flowerPot.itemID : stack.itemID;
    }
    
    public int getDamageValue(World world, int x, int y, int z)
    {
        ItemStack stack = getPlantStack(world.getBlockMetadata(x, y, z));
        
        return stack == null ? 0 : stack.getItemDamage();
    }
    
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune)
    {
        if (!world.isRemote)
        {
            ArrayList<ItemStack> items = getBlockDropped(world, x, y, z, metadata, fortune);
            chance = ForgeEventFactory.fireBlockHarvesting(items, world, this, x, y, z, metadata, fortune, chance, false, null);
            
            for (ItemStack item : items)
            {
                if (world.rand.nextFloat() <= chance)
                {
                    dropBlockAsItem_do(world, x, y, z, item);
                }
            }
            
            ItemStack stack = getPlantStack(metadata);
            
            if (stack != null)
            {
                dropBlockAsItem_do(world, x, y, z, stack);
            }
        }
    }
	
}
