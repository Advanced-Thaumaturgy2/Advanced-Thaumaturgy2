package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.ItemJarFilled;

public class ItemEtherealJar extends ItemJarFilled
{
	public ItemEtherealJar()
	{
		super();
		this.setMaxStackSize(4);
		this.setUnlocalizedName("at.etherealjar");
		setCreativeTab(AdvThaum.tabAdvThaum);
	}

	@Override
	public void registerIcons(IIconRegister ir)
	{
		itemIcon = ir.registerIcon("advthaum:etherealjar");
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return itemIcon;
	}
	
	@Override
	public IIcon getIconFromDamage(int dmg)
	{
		return itemIcon;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list)
	{
		list.add(new ItemStack(item, 1, 0));
	}
	
	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, 
            float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, AdvThaum.EtherealJar, metadata, 3))
            return false;
        if (world.getBlock(x, y, z) == AdvThaum.EtherealJar)
        {
        	TileEntity te = world.getTileEntity(x, y, z);
        	if (te == null)
        		te = new TileEtherealJar();
        	
        	TileEtherealJar ej = (TileEtherealJar)te;
        	
        	AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack); 
        	
        	if (aspects != null && aspects.getAspects() != null)
        	{
        		ej.aspect = aspects.getAspects()[0];
        	   	ej.amount = aspects.getAmount(ej.aspect);
        	}
        	
        	NBTTagCompound tag = stack.stackTagCompound;
        	
        	if (tag != null && tag.hasKey("AspectFilter"))
        		ej.aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));

			AdvThaum.EtherealJar.onBlockPlacedBy(world, x, y, z, player, stack);
			AdvThaum.EtherealJar.onPostBlockPlaced(world, x, y, z, metadata);
        	
        }
        return true;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List list, boolean showdetails)
	{
		AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack);
		
		if (aspects != null)
			list.add(aspects.getAspects()[0].getName() + " x " + aspects.getAmount(aspects.getAspects()[0]));
			
	}
	
}
