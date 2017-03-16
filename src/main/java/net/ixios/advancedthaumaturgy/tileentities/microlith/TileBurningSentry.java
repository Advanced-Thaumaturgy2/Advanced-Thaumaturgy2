package net.ixios.advancedthaumaturgy.tileentities.microlith;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.ixios.advancedthaumaturgy.entities.SentryFireball;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TileBurningSentry extends TileMicrolithEssentiaBase
{
	public static final int INTERVAL_IDLE = 20;
	public static final int INTERVAL_TARGET = 10;
	public static final int AMMO_PER_IGNIS = 10;
	public static final int TIMEOUT = 200;
	
	public static final double MAX_DISTANCE = 20.0;
	public static final int AABB_X = 8;
	public static final int AABB_Y = 4;
	public static final int AABB_Z = 8;
	
	private int tickCounter = 0;
	private int timeoutCounter = 0;
	
	private int ammo = 0;
	private EntityLivingBase targetEntity = null;
	
	public TileBurningSentry()
    {
	    super(new Color(255, 127, 0, 128), true, true, false, new AspectList().add(Aspect.FIRE, 64));
    }
	
	public boolean isLoaded()
	{
		return ammo > 0;
	}
	
	private boolean loadUp()
	{
		if (ammo >= 0 && doesContainerContainAmount(Aspect.FIRE, 1))
		{
			takeEssentia(Aspect.FIRE, 1);
			ammo = AMMO_PER_IGNIS;

			if (!worldObj.isRemote)
				worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, xCoord, yCoord, zCoord, 0);
            
            return true;
		}
		return false;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	    if (!getActive())
	    	return;
	    
	    tickCounter++;
	    
	    Vec3 src = Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.9, zCoord + 0.5);
	    
	    if (targetEntity == null)
	    {
	    	if (tickCounter < INTERVAL_IDLE)
	    		return;
	    	tickCounter = 0;
	    	
	    	AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord - AABB_X, yCoord - AABB_Y, zCoord - AABB_Z, xCoord + AABB_X,  yCoord + AABB_Y, zCoord + AABB_Z);
	    	@SuppressWarnings("unchecked")
			List<EntityMob> mobs = (List<EntityMob>) worldObj.getEntitiesWithinAABB(EntityMob.class, bb);
	    	
	    	for (EntityMob m : mobs)
	    	{
	    		if (m.isDead || m.getHealth() <= 0)
	    			continue;
	    		
	    		// check unobstructed
	    	    Vec3 target = m.getPosition(1.f).addVector(0, m.height / 2.0, 0);
	    		if (Utilities.raytraceUnobstructed(worldObj, target, src))
	    		{
	    			targetEntity = m;
	    			
	    			if (!isLoaded())
	    				loadUp();
	    			timeoutCounter = 0;
	    			tickCounter = INTERVAL_TARGET / 2;
	    			return;
	    		}
	    	}
	    	
	    	if (isLoaded())
	    	{
		    	timeoutCounter += INTERVAL_IDLE;
		    	if (timeoutCounter > TIMEOUT)
		    	{
		    		ammo = 0;
		    	}
	    	}
	    	return;
	    }
	    
	    if (tickCounter < INTERVAL_TARGET)
	    	return;
	    tickCounter = 0;

	    Vec3 target = targetEntity.getPosition(1.f).addVector(0, targetEntity.height / 2.0, 0);
	    Vec3 d = src.subtract(target);
	    double distance = d.lengthVector();
	    
	    if (targetEntity.getHealth() <= 0 || distance > MAX_DISTANCE || !Utilities.raytraceUnobstructed(worldObj, target, src))
	    {
	    	targetEntity = null;
	    	tickCounter = INTERVAL_IDLE;
	    	return;
	    }
	    
	    if (!isLoaded() && !loadUp())
	    	return;
	    
	    // FIRE ZE... FIRE
        
	    if (!worldObj.isRemote)
	    {
		    d = d.normalize();
		    float epsilon = 0.8f;
		    src = src.addVector(d.xCoord * epsilon, d.yCoord * epsilon, d.zCoord * epsilon);
		    SentryFireball fireball = new SentryFireball(worldObj, src.xCoord, src.yCoord, src.zCoord, d.xCoord, d.yCoord, d.zCoord);
	
	        worldObj.spawnEntityInWorld(fireball);
	        worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, xCoord, yCoord, zCoord, 0);
	    }
        
        ammo--;
	}
	
	@Override
	public void setActive(boolean active)
	{
		tickCounter = 0;
		ammo = 0;
		targetEntity = null;
		super.setActive(active);
	}

	@Override
	public String getMicrolithType() 
	{
		return "fire";
	}
}
