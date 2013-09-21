package genesis.genesis.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockTikiTorchRenderer implements ISimpleBlockRenderingHandler{

	public static int renderID = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer)
	{
		BlockTikiTorch tikiTorch = (BlockTikiTorch)block;
		int metadata = world.getBlockMetadata(x, y, z);
		
		Tessellator tess = Tessellator.instance;
		tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		tess.setColorOpaque_F(1, 1, 1);
		
		double delta = 0;
        double off = 0;
        
		double offX = 0;
		double offZ = 0;
		double dX = 0;
		double dZ = 0;
		
		if (!tikiTorch.isUpper(metadata))
		{
			delta = 0.25;
			off = 0.5 - delta;
		}
		else
		{
			delta = 0.25;
			off = 0;
		}
		
		switch (tikiTorch.getDirection(metadata))
		{
		case 1:
			offX = -off;
			dX = -delta;
			break;
		case 2:
			offX = off;
			dX = delta;
			break;
		case 3:
			offZ = -off;
			dZ = -delta;
			break;
		case 4:
			offZ = off;
			dZ = delta;
			break;
		}
		
		renderer.renderTorchAtAngle(block, x + offX, y, z + offZ, dX, dZ, metadata);
		
		return true;
		
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}
