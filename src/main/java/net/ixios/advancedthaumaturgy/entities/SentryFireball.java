package net.ixios.advancedthaumaturgy.entities;

import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class SentryFireball extends EntitySmallFireball {
	
	public SentryFireball(World world, double x, double y, double z, double dx, double dy, double dz)
	{
		super(world, x, y, z, dx, dy, dz);
		setSize(0.2F, 0.2F);
	}

	@Override
	protected void onImpact(MovingObjectPosition p_70227_1_) 
	{
        if (!this.worldObj.isRemote)
        {
            if (p_70227_1_.entityHit != null)
            {
                if (!p_70227_1_.entityHit.isImmuneToFire() 
                		&& p_70227_1_.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F))
                {
                    p_70227_1_.entityHit.setFire(5);
                }
            }

            this.setDead();
        }
	}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
        return false;
    }

}
