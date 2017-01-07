package net.ixios.advancedthaumaturgy.tileentities.microlith;

import java.awt.Color;
import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TileMicrolithHealing extends TileMicrolithEssentiaBase 
{
	private int tickCounter = 0;
	private AxisAlignedBB area;

	public TileMicrolithHealing() 
	{
		super(new Color(128, 0, 0), true, true, false, 
				new AspectList().add(Aspect.HEAL, 64));
	}
	
	@Override
	public void updateEntity()
	{
		if (!getActive())
			return;
		
		if (tickCounter < 40)
		{
			tickCounter++;
			return;
		}
		
		tickCounter = 0;
		
		if (!doesContainerContainAmount(Aspect.HEAL, 1))
			return;
		
		@SuppressWarnings("unchecked")
		List<EntityLivingBase> targets = (List<EntityLivingBase>) worldObj.getEntitiesWithinAABB(EntityPlayer.class, 
				AxisAlignedBB.getBoundingBox(
				xCoord - 5,
				yCoord - 5,
				zCoord - 5,
				xCoord + 6,
				yCoord + 6,
				zCoord + 6));
		
		for (EntityLivingBase target : targets)
		{
			if (target.getHealth() < target.getMaxHealth())
			{
				target.heal(1);
				if (worldObj.isRemote)
				{
					AdvThaum.proxy.createHealBeam(worldObj, 
							Vec3.createVectorHelper(xCoord + 0.5f, yCoord + 1.05f, zCoord + 0.5f), 
							target, 20);
					AdvThaum.proxy.createHealBeam(worldObj, 
							Vec3.createVectorHelper(xCoord + 0.5f, yCoord + 1.05f, zCoord + 0.5f), 
							target, 10);
				}
				takeEssentia(Aspect.HEAL, 1);
				return;
			}
		}
	}
	
	@Override
	public void setActive(boolean active)
	{
		tickCounter = 0;
		super.setActive(active);
	}
	
	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public String getMicrolithType() 
	{
		return "healing";
	}

}
