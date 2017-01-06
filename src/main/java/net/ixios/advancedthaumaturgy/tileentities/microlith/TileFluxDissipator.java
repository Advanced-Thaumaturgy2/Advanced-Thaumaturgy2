package net.ixios.advancedthaumaturgy.tileentities.microlith;

import java.awt.Color;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.block.Block;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.config.ConfigBlocks;

public class TileFluxDissipator extends TileMicrolithEssentiaBase implements IAspectContainer
{
	private int tickcount = 0;
	
	public TileFluxDissipator()
	{
		super(new Color(255, 0, 255), false, false, true, new AspectList().add(Aspect.TAINT, 64));
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	
		tickcount++;
		
		if (tickcount % 20 != 0)
			return;
		
		for (int cx = xCoord - 12; cx < xCoord + 12; cx++)
		{
			for (int cy = yCoord - 12; cy < yCoord + 12; cy++)
			{
				for (int cz = zCoord - 12; cz < zCoord + 12; cz++)
				{
					Block blockid = worldObj.getBlock(cx, cy, cz);
				
					if (blockid == ConfigBlocks.blockFluxGoo)
					{
						worldObj.playSoundEffect(cx, cy, cz, "liquid.swim", 0.3F, 1.0F);
						worldObj.setBlock(cx,  cy,  cz, ConfigBlocks.blockFluxGas);
						return;
					}
					else if (blockid == ConfigBlocks.blockFluxGas && tickcount % 60 == 0)
					{
						Vector3F src = new Vector3F(xCoord + 0.5F, yCoord + 1F, zCoord + 0.5F);
						
						Vector3F dst = new Vector3F(cx + 0.2F, cy, cz + 0.2F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						dst = new Vector3F(cx + 0.7F, cy, cz + 0.2F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);

						dst = new Vector3F(cx + 0.2F, cy, cz + 0.7F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						dst = new Vector3F(cx + 0.7F, cy, cz + 0.7F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						worldObj.setBlockToAir(cx,  cy,  cz);
						
						worldObj.playSoundEffect(cx, cy, cz, "random.fizz", 0.3F, 1.0F);
						
						if (!worldObj.isRemote && worldObj.rand.nextInt(100) <= 15)
						{
							addEssentia(Aspect.TAINT, 1);
						}
						return;
					}
				}	
			}
		}
	}
	
	@Override
	public String getMicrolithType() {
		return "fluxDissapator";
	}
	
}
