package genesis.block.plants;

import java.util.Random;

import sun.rmi.log.ReliableLog.LogFile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import genesis.common.Genesis;

public class BlockGenesisCrop extends BlockCrops {

	@SideOnly(Side.CLIENT) private IIcon[] iconArray;

	private Item seedItem;
	private Item cropItem;
	private Block soilBlock;
	private int stages;
	private int fullGrownDroppedAmmount;
	private int droppedAmmount;

	/**
	 * 
	 * @param Item Crop
	 * @param Item Seed
	 * @param Block Soil
	 * @param Growth Stages
	 * @param Fully grown maximal seeds dropped amount
	 */
	public BlockGenesisCrop(Item cropItem, Item seedItem, Block soilBlock, int stages, int fullGrownDroppedAmmount) {
		this.cropItem = cropItem;
		this.seedItem = seedItem;
		this.soilBlock = soilBlock;
		this.stages = stages;
		this.fullGrownDroppedAmmount = fullGrownDroppedAmmount;
		
		setCreativeTab((CreativeTabs) null);
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == soilBlock;
	}

	@Override
	// protected int getSeedItem() {
	protected Item func_149866_i() {
		return seedItem;
	}

	@Override
	// protected int getCropItem() {
	protected Item func_149865_P() {
		return cropItem;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return droppedAmmount;
	}
	
	@SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return this.func_149866_i();
    }

	@Override
	public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
		super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
		if (!par1World.isRemote)
		{
			if (par5 >= stages - 1) {
				Random par1Random = new Random();
				droppedAmmount = par1Random.nextInt(fullGrownDroppedAmmount) + 1;
				
				dropBlockAsItem(par1World, par2, par3, par4, new ItemStack(this.cropItem));
				if(this.cropItem != this.seedItem)
				{
					int seeddrops = par1Random.nextInt(3);
					for(int i = 0; i < seeddrops; i++)
					{
						dropBlockAsItem(par1World, par2, par3, par4, new ItemStack(this.seedItem));
					}
				}
			} else
				dropBlockAsItem(par1World, par2, par3, par4, new ItemStack(this.seedItem));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
		if (par2 < 0 || par2 > stages)
			par2 = stages;

		return iconArray[par2];
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconArray = new IIcon[stages];

		for (int i = 0; i < iconArray.length; ++i)
			iconArray[i] = par1IconRegister.registerIcon(Genesis.MOD_ID + ":" + getTextureName() + "_stage_" + i);
	}
}