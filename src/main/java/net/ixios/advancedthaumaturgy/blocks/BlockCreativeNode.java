package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemCreativeNode;
import net.ixios.advancedthaumaturgy.tileentities.TileCreativeNode;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class BlockCreativeNode extends BlockAiry
{
	public static int renderID;
	
	public BlockCreativeNode()
	{
		super();
		this.setBlockName("at.creativenode");
		this.setCreativeTab(AdvThaum.tabAdvThaum);
		this.setBlockUnbreakable();
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:node");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) 
	{
		return blockIcon;
	}

	@Override
	public boolean renderAsNormalBlock() 
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileCreativeNode();
	}
	
	public void register()
	{
		GameRegistry.registerBlock(this, ItemCreativeNode.class, getUnlocalizedName());
		GameRegistry.registerTileEntity(TileCreativeNode.class, "tileentityCreativeNode");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		// prevent BlockAiry.onBlockPlacedBy() from creating a random node
	}
}
