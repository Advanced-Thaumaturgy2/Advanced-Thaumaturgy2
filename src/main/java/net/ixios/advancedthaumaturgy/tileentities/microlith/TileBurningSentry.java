package net.ixios.advancedthaumaturgy.tileentities.microlith;

import java.awt.Color;
import java.util.List;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TileBurningSentry extends TileMicrolithEssentiaBase
{
	private int tickCounter;
	private EntityLivingBase m_target = null;
	
	public TileBurningSentry()
    {
	    super(new Color(255, 127, 0, 128), true, true, false, new AspectList().add(Aspect.FIRE, 64));
    }
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	    if (!getActive())
	    	return;
	    
	    tickCounter++;
	    
	    if (m_target == null)
    	{
	    	if (tickCounter > 20)
	    	{
	    		tickCounter = 0;
		    	AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord - 12, yCoord - 4, zCoord - 12, xCoord + 12,  yCoord + 8, zCoord + 12);
		    	@SuppressWarnings("unchecked")
				List<EntityMob> mobs = (List<EntityMob>) worldObj.getEntitiesWithinAABB(EntityMob.class, bb);
		    	
		    	if (mobs.size() == 0)
		    		return;
		    	
		    	m_target = mobs.get(0);
	    	}
	    	else
	    		return;
	    }
	    
	    if (m_target != null && m_target.isDead)
	    {
	    	m_target = null;
	    	return;
	    }
	    
	    // FIRE ZE... FIRE
	    
	    Vec3 target = Vec3.createVectorHelper(m_target.posX, m_target.posY,  m_target.posZ);
	    Vec3 src = Vec3.createVectorHelper(xCoord,  yCoord, zCoord);
	    Vec3 vector = src.subtract(target);
	    
        if (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 10 == 0)
            worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "thaumcraft:fireloop", 0.25F, 2.0F);

        if (worldObj.isRemote)
        	AdvThaum.proxy.shootFireInDirection(worldObj, src, vector);
        
	}
	
	@Override
	public void setActive(boolean active)
	{
		tickCounter = 0;
		super.setActive(active);
	}

	@Override
	public String getMicrolithType() 
	{
		return "fire";
	}
}
