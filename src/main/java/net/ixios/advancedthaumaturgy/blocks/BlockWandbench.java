package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.gui.GuiWandbench;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchPage;

public class BlockWandbench extends BlockContainer
{
	public BlockWandbench()
	{
		super(Material.wood);
		setBlockName("blockWandbench");
	}
	
	public void register()
	{
		GameRegistry.registerBlock(this, "blockWandbench");
		GameRegistry.registerTileEntity(TileWandbench.class, "tileentityWandbench");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		ATResearchItem ri = new ATResearchItem("WANDBENCH", "ADVTHAUM",
				(new AspectList()).add(Aspect.MAGIC, 1).add(Aspect.MECHANISM, 1).add(Aspect.EXCHANGE, 1).add(Aspect.TOOL, 1),
				0, 2, 4,
				new ItemStack(this));
		ri.setTitle("at.research.wandbench.title");
		ri.setInfo("at.research.wandbench.desc");
		ri.setPages(new ResearchPage("at.research.wandbench.pg1"));
		ri.setParents("ARCANECRYSTAL");
		ri.setConcealed();
		
		ri.registerResearchItem();
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileWandbench();
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, 
    		EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		if (!world.isRemote)
			player.openGui(AdvThaum.instance, GuiWandbench.id, world, x, y, z);
		return true;
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
	public int getRenderType()
	{
		return -1;
	}
}
