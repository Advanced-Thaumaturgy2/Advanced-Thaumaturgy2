package net.ixios.advancedthaumaturgy.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

public class ItemFocusVoidCage extends ItemFocusBasic
{

	private AspectList cost = null;
	
	public ItemFocusVoidCage()
    {
	    super();
	    cost = new AspectList().add(Aspect.ORDER, 5000).add(Aspect.ENTROPY, 5000);
	    setUnlocalizedName("at.voidcage");
    }

	public void register()
	{
		GameRegistry.registerItem(this, "focusvoidcage");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		// research
		// if tt parent to dislocation and place off
		//else
		// put where dislocation is
		
		// bestia, vacuos, praecantatio
		
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
        super.icon = ir.registerIcon("advthaum:voidcage");
    }
	
	@Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition mop)
    {
		NBTTagCompound tag = itemstack.getTagCompound();
    	
    	if (tag == null || !tag.hasKey("classname"))
    	{

	 		Entity pointedEntity = EntityUtils.getPointedEntity(world, player, 1D, 32D, 0f);
	 		ItemWandCasting wand = ((ItemWandCasting)itemstack.getItem());
			if (pointedEntity != null && wand.consumeAllVis(itemstack, player, cost, true,false))
			{
				if (!world.isRemote)
	    		{
					pointedEntity.writeToNBT(tag);
					tag.setString("classname", pointedEntity.getClass().getCanonicalName());
					world.removeEntity(pointedEntity);
					NBTTagCompound pos = tag.getCompoundTag("Pos");
					float x = (float)pos.getDouble("x");
					float y = (float)pos.getDouble("y");
					float z = (float)pos.getDouble("z");
					AdvThaum.proxy.createSparkleBurst(world, x + 0.5F, y + 1, z + 0.5F, 15, 0xFFFF00FF);
	    		}
				else
					player.swingItem();
			}
    			
    	}
		else if (tag.hasKey("classname") && mop != null)
		{
			EntityLivingBase entity = null;
			String classname = tag.getString("classname");
			Class c;
			
			EnumFacing fd = EnumFacing.values()[mop.sideHit];
			
            try
            {
                c = Class.forName(classname);
            } catch (Exception e)
            {
            	return itemstack;
            }
            
			try
            {
				Constructor<? extends EntityLivingBase> constructor = c.getConstructor(World.class);
				entity = constructor.newInstance(world);

				NBTTagCompound newpos=new NBTTagCompound();
				newpos.setDouble("x", mop.blockX + fd.getFrontOffsetX());
				newpos.setDouble("y",mop.blockY + fd.getFrontOffsetY());
				newpos.setDouble("z",mop.blockZ + fd.getFrontOffsetZ());

				NBTTagCompound motion = new NBTTagCompound();
				motion.setDouble("x", 0D);
				motion.setDouble("y", 0D);
				motion.setDouble("z", 0D);
				
				tag.removeTag("Pos");
				tag.removeTag("Motion");
				
				tag.setTag("Pos", newpos);
				tag.setTag("Motion", motion);
				
				entity.readFromNBT(tag);
				tag.removeTag("classname");
			    
            } catch (Exception e) 
            { 
            	return itemstack;
            }
			if (entity != null)
			{
				
				AdvThaum.proxy.createSparkleBurst(world, mop.blockX + 0.5F, mop.blockY + 1F, mop.blockZ + 0.5F, 15, 0xFFFF00FF);
				if (!world.isRemote)
					world.spawnEntityInWorld(entity);
				else
					player.swingItem();
			}
		}
    	
        return itemstack; 
    }

    public boolean isVisCostPerTick()
    {
        return false;
    }

	@Override
	public WandFocusAnimation getAnimation(ItemStack focusstack) {
		return WandFocusAnimation.WAVE;
	}

	@Override
	public AspectList getVisCost(ItemStack focusstack) {
		return cost;
	}

    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int i, EntityPlayer entityplayer)
    {
        return false;
    }

	@Override
	public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank) {
		return new FocusUpgradeType[]{FocusUpgradeType.frugal};
	}

    
}
