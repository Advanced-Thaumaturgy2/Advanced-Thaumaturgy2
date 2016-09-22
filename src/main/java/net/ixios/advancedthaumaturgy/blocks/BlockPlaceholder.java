package net.ixios.advancedthaumaturgy.blocks;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPlaceholder extends Block implements ITileEntityProvider
{

	public static int blockID;
	public final int renderID;
	
	public BlockPlaceholder(Material material)
	{
		super(Material.air);
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	public void register()
	{
		GameRegistry.registerBlock(this, "blockPlaceholder");
		LanguageRegistry.addName(this, "Block Placeholder");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world,int metadata)
	{
		return null;
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list,
	        Entity entity)
	{
		// do nothing
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}
	
	@Override
	public boolean renderAsNormalBlock() 
	{
		return false;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		return 6;
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
    	return renderID;
    }
    
    @Override
    public void registerBlockIcons(IIconRegister ir)
    {
	     blockIcon = ir.registerIcon("thaumcraft:assets/thaumcraft/textures/aspects/_unknown.png");
    }
    
}
