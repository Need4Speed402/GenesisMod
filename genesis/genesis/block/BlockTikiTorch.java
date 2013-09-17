package genesis.genesis.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import genesis.genesis.common.Genesis;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockTikiTorch extends Block{

	public static final int TORCH_META = 8;
	public static final int MASK_META = 7;
	
	
	public static Icon tikiTorchLower;
	public static Icon tikiTorchUpper;
	
	protected BlockTikiTorch(int par1) {
		super(par1, Material.circuits);
		this.setTickRandomly(true);
		this.setCreativeTab(Genesis.tabGenesis);
		this.setHardness(0.0F);
		this.setLightValue(0.9375F);
	}
	
	public int getRenderType()
    {
        return BlockTikiTorchRenderer.renderID;
    }
	
	public void registerIcons(IconRegister iconRegister)
    {
		this.blockIcon = iconRegister.registerIcon(Genesis.MOD_ID + ":" + getTextureName());
		this.tikiTorchUpper = iconRegister.registerIcon(Genesis.MOD_ID + ":" + getTextureName() + "_upper");
		this.tikiTorchLower = iconRegister.registerIcon(Genesis.MOD_ID + ":" + getTextureName() + "_lower");
    }
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public int setUpper(int metadata)
	{
		return (metadata & MASK_META) | TORCH_META;
	}
	
	public boolean isUpper(int metadata)
	{
		return (metadata & TORCH_META) != 0;
	}
	
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
		if(isUpper(world.getBlockMetadata(x, y, z)))
		{
	        double xPos = (double)x + 0.5D;
	        double yPos = (double)y + 0.7D;
	        double zPos = (double)z + 0.5D;
	
	        world.spawnParticle("smoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
	        world.spawnParticle("flame", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
		}
        
    }
	
	public void updateTick(World world, int x, int y, int z, Random random)
    {
		this.checkUnderneathBlock(world, x, y, z);
    }
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighbourblockID)
    {
		this.checkUnderneathBlock(world, x, y, z);
    }
	
	public void checkUnderneathBlock(World world, int x, int y, int z)
	{
		if(!isUpper(world.getBlockMetadata(x, y, z)))
		{
			if(!canTorchStay(world, x, y, z))
			{
				this.dropBlockAsItem(world, x, y, z, 0, 0);
				world.setBlock(x, y, z, 0);
			}
		}
		else
		{
			if(world.getBlockId(x, y - 1, z) != this.blockID)
			{
				world.setBlock(x, y, z, 0);
			}
		}
	}
	
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
		if(!canTorchStay(world, x, y, z))
		{
			if(world.isAirBlock(x, y - 1, z))
			{
				return setUpper(metadata);
			}
		}
		return metadata & MASK_META;
    }
	
	public boolean canTorchStay(World world, int x, int y, int z)
	{
		if(world.doesBlockHaveSolidTopSurface(x, y - 1, z))
		{
			return true;
		}
		return false;
	}
	
	public void onBlockAdded(World world, int x, int y, int z)
    {
		int metadata = world.getBlockMetadata(x, y, z);
		boolean flag = false;
		if(!isUpper(metadata))
		{
			int blockID = world.getBlockId(x, y + 1, z);
			if(blockID == 0 || blockID == this.blockID)
			{
				world.setBlock(x, y + 1, z, this.blockID, setUpper(metadata), 2);
				flag = true;
			}
		}
		else
		{
			if(canTorchStay(world, x, y - 1, z))
			{
				if(world.getBlockId(x, y - 1, z) != this.blockID)
				{
					world.setBlock(x, y - 1, z, this.blockID, 0, 2);
				}
				flag = true;
				
			}
			
		}
		if(!flag)
		{
			if(world.getBlockId(x, y, z) == this.blockID)
			{
				world.setBlock(x, y, z, 0);
				this.dropBlockAsItem(world, x, y, z, 0, 0);
			}
		}
		
    }
	
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 par5Vec3, Vec3 par6Vec3)
    {
		if(!isUpper(world.getBlockMetadata(x, y, z)))
		{
			this.setBlockBounds(0.4F, 0.0F, 0.4F, 0.6F, 1F, 0.6F);
		}
		else
		{
			this.setBlockBounds(0.4F, 0.0F, 0.4F, 0.6F, 0.7F, 0.6F);
		}
		return super.collisionRayTrace(world, x, y, z, par5Vec3, par6Vec3);
    }
	
	public void breakBlock(World world, int x, int y, int z, int blockID, int metadata)
    {
		if(!isUpper(metadata))
		{
			if(world.getBlockId(x, y + 1, z) == this.blockID)
			{
				world.setBlockToAir(x, y + 1, z);
			}
		}
		else
		{
			world.setBlockToAir(x, y - 1, z);
		}
    }
	

}
