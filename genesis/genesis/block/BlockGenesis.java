package genesis.genesis.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import genesis.genesis.common.Genesis;

public class BlockGenesis extends Block {

	public BlockGenesis(int blockID, Material material) {
		super(blockID, material);
        setCreativeTab(Genesis.tabGenesis);
	}
	
	public void registerBlock(String name) {
		GameRegistry.registerBlock(this, ItemBlock.class, name);
	}
	
	@Override
	public Block setUnlocalizedName(String unlocName) {
		registerBlock(unlocName);
		return super.setUnlocalizedName(unlocName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon(Genesis.MOD_ID + ":" + getTextureName());
    }
	
}
