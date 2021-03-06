package genesis.genesis.block.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import genesis.genesis.block.Blocks;
import genesis.genesis.common.Genesis;
import genesis.genesis.common.GenesisGuiHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockCampfire extends BlockContainer {

	public static final int DIR_BITS = 7;
	public static final int FIRE_BIT = 8;

	protected Icon poleIcon;
	protected Icon poleVIcon;
	
	public BlockCampfire(int blockID)
	{
		super(blockID, Material.rock);
		
        setCreativeTab(Genesis.tabGenesis);
		
		setBlockBounds(0, 0, 0, 1, 1, 1);
		setTickRandomly(true);
	}
	
	@Override
	public Block setUnlocalizedName(String unlocName)
	{
		GameRegistry.registerBlock(this, ItemBlock.class, unlocName);
		
		return super.setUnlocalizedName(unlocName);
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		poleIcon = iconRegister.registerIcon(Genesis.MOD_ID + ":campfire_pole");
		poleVIcon = iconRegister.registerIcon(Genesis.MOD_ID + ":campfire_v_pole");
		
		blockIcon = Blocks.granite.getIcon(0, 0);
	}
	
	public Icon getIcon(int part, int metadata)
	{
		switch (part)
		{
		case 1:
			return poleIcon;
		case 2:
			return poleVIcon;
		default:
			return blockIcon;
		}
	}
	
	@Override
	public String getItemIconName()
	{
		return Genesis.MOD_ID + ":" + getTextureName();
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return world.doesBlockHaveSolidTopSurface(x, y - 1, z);
	}
	
	@Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return canBlockStay(world, x, y, z);
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if (!canBlockStay(world, x, y, z))
		{
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		if (rand.nextInt(24) == 0)
		{
			world.playSound(x + 0.5, y + 0.5, y + 0.5, "fire.fire", 1 + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}
		
		TileEntityCampfire campfire = getTileEntityAt(world, x, y, z);
		
		if (campfire != null && campfire.isBurning())
		{
			double randAmtH = 0.375;
			double randAmtV = 0.375;
			
			double mX = x + 0.5;
			double mY = y + 0.0625;
			double mZ = z + 0.5;
			
			for (int i = 0; i < rand.nextInt(2); i++)
			{
				double pX = mX - (randAmtH / 2) + rand.nextFloat() * randAmtH;
				double pY = mY + rand.nextFloat() * randAmtV;
				double pZ = mZ - (randAmtH / 2) + rand.nextFloat() * randAmtH;

				world.spawnParticle("largesmoke", pX, pY, pZ, 0, 0, 0);
			}
			
			if (campfire.getStackInSlot(0) != null)
			{
				randAmtH = 0.25;
				randAmtV = 0.25;
				mY = y + 1;
				
				for (int i = 0; i < rand.nextInt(3); i++)
				{
					double offX = rand.nextFloat() * randAmtH - (randAmtH / 2);
					double offZ = rand.nextFloat() * randAmtH - (randAmtH / 2);
					
					double pX = mX + offX;
					double pY = mY + rand.nextFloat() * randAmtV;
					double pZ = mZ + offZ;
					
					world.spawnParticle("smoke", pX, pY, pZ, offX * 0.1, 0, offZ * 0.1);
				}
			}
		}
	}
	
	protected AxisAlignedBB makeBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	protected void addIfIntersects(AxisAlignedBB aabb, AxisAlignedBB mask, List list)
	{
		if (aabb != null && aabb.intersectsWith(mask))
		{
			list.add(aabb);
		}
	}
	
	protected AxisAlignedBB reverseBB(int x, int y, int z, AxisAlignedBB bb)
	{
		return makeBB(bb.minZ - z + x, bb.minY, -(bb.minX - x - 0.5) + z + 0.5,
				bb.maxZ - z + x, bb.maxY, -(bb.maxX - x - 0.5) + z + 0.5);
	}
		
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity)
	{
		int direction = getDirection(world.getBlockMetadata(x, y, z)) % 4;
		
		addIfIntersects(makeBB(x, y, z, x + 1, y + 0.25, z + 1), mask, list);
		
		double stickSize = 0.125;
		double stickSize2 = stickSize / 2;
		
		double cos45 = 0.70710678118654752440084436210485;
		double dist45 = cos45 * 0.5;

		AxisAlignedBB stick0 = null;
		AxisAlignedBB stick1 = null;
		
		switch (direction % 2)
		{
		case 0:
			stick0 = makeBB(x + 0.5 - stickSize2, y, z,
					x + 0.5 + stickSize2, y + 1, z + stickSize);
			stick1 = makeBB(x + 0.5 - stickSize2, y, z + 1 - stickSize,
					x + 0.5 + stickSize2, y + 1, z + 1);
			break;
		case 1:
			stick0 = makeBB(x + 0.5 + dist45 - stickSize, y, z + 0.5 + dist45 - stickSize,
					x + 0.5 + dist45 + stickSize2 * cos45, y + 1, z + 0.5 + dist45 + stickSize2 * cos45);
			stick1 = makeBB(x + 0.5 - dist45 + stickSize, y, z + 0.5 - dist45 + stickSize,
					x + 0.5 - dist45 - stickSize2 * cos45, y + 1, z + 0.5 - dist45 - stickSize2 * cos45);
			break;
		}
		
		switch (direction)
		{
		case 2:
		case 3:
			stick0 = reverseBB(x, y, z, stick0);
			stick1 = reverseBB(x, y, z, stick1);
		}

		addIfIntersects(stick0, mask, list);
		addIfIntersects(stick1, mask, list);
	}
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote)
		{
			TileEntityCampfire campfire = getTileEntityAt(world, x, y, z);
			
			if (campfire.isBurning())
			{
				double posX = entity.posX;
				double posY = entity.posY + entity.ySize - entity.yOffset;
				double posZ = entity.posZ;
				
				double dist = 0.25 + 0.1;
				AxisAlignedBB fireBB = makeBB(x + 0.5, y, z + 0.5,
						x + 0.5, y + 0.3, z + 0.5);
				fireBB = fireBB.expand(dist, 0, dist);
				
				Vec3 vec = world.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
				
				if (fireBB.isVecInside(vec))
				{
					entity.setFire(8);
					
					if (!entity.isImmuneToFire())
					{
						entity.attackEntityFrom(DamageSource.inFire, 1);
					}
				}
			}
		}
	}
	
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return makeBB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public void breakBlock(World world, int x, int y, int z, int blockID, int metadata)
	{
		Random rand = world.rand;
		TileEntityCampfire campfire = getTileEntityAt(world, x, y, z);

		if (campfire != null)
		{
			for (int slot = 0; slot < campfire.getSizeInventory(); ++slot)
			{
				ItemStack stack = campfire.getStackInSlot(slot);

				if (stack != null)
				{
					float offX = rand.nextFloat() * 0.8F + 0.1F;
					float offY = rand.nextFloat() * 0.8F + 0.1F;
					float offZ = rand.nextFloat() * 0.8F + 0.1F;

					while (stack.stackSize > 0)
					{
						int subSize = rand.nextInt(21) + 10;

						if (subSize > stack.stackSize)
						{
							subSize = stack.stackSize;
						}

						stack.stackSize -= subSize;
						
						EntityItem dropItem = new EntityItem(world, x + offX, y + offY, z + offZ,
								new ItemStack(stack.itemID, subSize, stack.getItemDamage()));

						if (stack.hasTagCompound())
						{
							dropItem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
						}
						
						float mul = 0.05F;
						dropItem.motionX = rand.nextGaussian() * mul;
						dropItem.motionY = rand.nextGaussian() * mul + 0.2;
						dropItem.motionZ = rand.nextGaussian() * mul;
						
						world.spawnEntityInWorld(dropItem);
					}
				}
			}

			world.func_96440_m(x, y, z, blockID);
		}

		super.breakBlock(world, x, y, z, blockID, metadata);
	}
	
	public int getDirection(int metadata)
	{
		return metadata & DIR_BITS;
	}
	
	private int setDirection(int metadata, int direction)
	{
		return (metadata & ~DIR_BITS) | (direction & DIR_BITS);
	}
	
	public float getBlockRotationAt(IBlockAccess world, int x, int y, int z)
	{
		float out = getDirection(world.getBlockMetadata(x, y, z)) * 45;
		
		return out;
	}
	
	public boolean isFireLit(int metadata)
	{
		return (metadata & FIRE_BIT) != 0;
	}
	
	public int setFireLit(int metadata, boolean lit)
	{
		return (metadata & ~FIRE_BIT) | (lit ? FIRE_BIT : 0);
	}
	
	@Override
	public int getRenderType()
	{
		return BlockCampfireRenderer.renderID;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
			int side, float hitX, float hitY, float hitZ)
	{
		ItemStack held = player.getHeldItem();
		
		if (held != null && held.itemID == Item.bucketWater.itemID)
		{
			TileEntityCampfire campfire = getTileEntityAt(world, x, y, z);
			campfire.setWet();
			
			return true;
		}
		else
		{
			player.openGui(Genesis.instance, GenesisGuiHandler.GUI_CAMPFIRE, world, x, y, z);
			
			return true;
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityCampfire();
	}
	
	public TileEntityCampfire getTileEntityAt(IBlockAccess world, int x, int y, int z)
	{
		TileEntity tileEnt = world.getBlockTileEntity(x, y, z);
		
		if (tileEnt instanceof TileEntityCampfire)
		{
			return (TileEntityCampfire)tileEnt;
		}
		
		return null;
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		int direction = Math.round(-entity.rotationYaw / 45) + 2;
		
		world.setBlockMetadataWithNotify(x, y, z, setDirection(world.getBlockMetadata(x, y, z), direction), 2);
	}

	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		int lightVal = super.getLightValue(world, x, y, z);
		
		if (world.getBlockId(x, y, z) == blockID && isFireLit(world.getBlockMetadata(x, y, z)))
		{
			return Math.max(lightVal, 15);
		}
		
		return lightVal;
	}
	
	public Random getRandomAt(IBlockAccess world, int x, int y, int z)
	{
		return new Random(x * 30000L + y * 300000000L + z * 3000000000000L);
	}
	
}
