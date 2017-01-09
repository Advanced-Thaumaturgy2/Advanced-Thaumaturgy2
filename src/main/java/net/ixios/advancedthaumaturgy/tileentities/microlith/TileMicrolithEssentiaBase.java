package net.ixios.advancedthaumaturgy.tileentities.microlith;

import java.awt.Color;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEssence;

public abstract class TileMicrolithEssentiaBase extends TileMicrolithBase implements IAspectContainer, IEssentiaTransport
{
	private boolean essentiaInput;
	private boolean essentiaOutput;
	private AspectList capacity;
	
	private AspectList content;
	private Aspect suction;
	private int suctionCycle;
	
	public TileMicrolithEssentiaBase(Color color, boolean canToggleActive, boolean essentiaInput, boolean essentiaOutput, AspectList capacity)
	{
		super(color, canToggleActive);
		this.essentiaInput = essentiaInput;
		this.essentiaOutput = essentiaOutput;
		this.capacity = capacity;
		content = new AspectList();
		suction = null;
		suctionCycle = 0;
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
    {
		if (super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
			return true;
		
		if (player.getHeldItem() != null && player.getHeldItem().getItem().equals(ConfigItems.itemEssence))
		{
			ItemStack stack = player.getHeldItem();
			ItemEssence essence = (ItemEssence) ConfigItems.itemEssence;
			AspectList aspects = essence.getAspects(stack);
			
			if (aspects == null && essentiaOutput)
			{
				for (Aspect a : content.getAspects())
				{
					if (doesContainerContainAmount(a, 8))
					{
						if (world.isRemote)
						{
							player.swingItem();
							return false;
						}
						
						takeFromContainer(a, 8);
						stack.stackSize--;
						ItemStack filled = new ItemStack(ConfigItems.itemEssence, 1, 1);
						essence.setAspects(filled, (new AspectList()).add(a, 8));
						if (!player.inventory.addItemStackToInventory(filled)) 
						{
							world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), 
						 		  (double)((float)y + 0.5F), (double)((float)z + 0.5F), filled));
						}
						
						world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
						player.inventoryContainer.detectAndSendChanges();
						return true;
					}
				}
			}
			else if (aspects != null && essentiaInput)
			{
				Aspect a = aspects.getAspects()[0];
				if (capacity.getAmount(a) - content.getAmount(a) >= 8)
				{
					if (world.isRemote)
					{
						player.swingItem();
						return false;
					}
					
					addToContainer(a, 8);
					stack.stackSize--;
					ItemStack empty = new ItemStack(ConfigItems.itemEssence, 1, 0);
					if (!player.inventory.addItemStackToInventory(empty)) 
					{
						world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), 
					 		  (double)((float)y + 0.5F), (double)((float)z + 0.5F), empty));
					}
					
					world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
					player.inventoryContainer.detectAndSendChanges();
					return true;
				}
			}
		}
		
		return false;
    }
	
	@Override
	public void updateEntity()
	{
		if (essentiaInput)
		{
			boolean essentiaApplied = false;
			
			for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) 
			{
				if (isConnectable(orientation)) 
				{
					IEssentiaTransport connectedTube = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, orientation);
					if ((connectedTube != null) 
					 && (connectedTube.getEssentiaAmount(orientation.getOpposite()) > 0))
					{
						Aspect tubeAspect = connectedTube.getEssentiaType(orientation.getOpposite());
						if (capacity.getAmount(tubeAspect) > 0 && content.getAmount(tubeAspect) < capacity.getAmount(tubeAspect))
						{
							int taken = connectedTube.takeEssentia(tubeAspect, 1, orientation.getOpposite());
							if (taken == 1)
							{
								suction = tubeAspect;
								addEssentia(tubeAspect, 1, orientation);
								essentiaApplied = true;
								break;
							}
						}
					}
				}
			}
			
			// cycle suction through accepted aspects
			if (!essentiaApplied)
			{
				if (worldObj.getWorldTime() % 100 == 0)
				{
					for (int i = 0; i < capacity.size(); i++)
					{
						suctionCycle++;
						if (suctionCycle >= capacity.size())
							suctionCycle = 0;
						Aspect newSuction = capacity.getAspects()[suctionCycle];
						if (content.getAmount(newSuction) < capacity.getAmount(newSuction))
						{
							suction = newSuction;
							break;
						}
					}	
				}
			}
		}
	}
	
	@Override
	public boolean canUpdate()
	{
		return true;
	}
	
	@Override
	public void readExtraNBT(NBTTagCompound tag)
	{
		super.readExtraNBT(tag);
		content.readFromNBT(tag, "content");
	}
	
	@Override
	public void writeExtraNBT(NBTTagCompound tag)
	{
		super.writeExtraNBT(tag);
		content.writeToNBT(tag, "content");
	}
	
	/**
	 * For use by child classes when essentia is used up by the block
	 * @param a
	 * @param amount
	 * @return
	 */
	protected boolean takeEssentia(Aspect a, int amount)
	{
		if (content.getAmount(a) >= amount)
		{
			content.reduce(a, amount);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return true;
		}
		return false;
	}
	
	/**
	 * For use by child classes when essentia is generated by the block
	 * @param a
	 * @param amount
	 * @return
	 */
	protected int addEssentia(Aspect a, int amount)
	{
		int rtn = Math.max(0, amount - (capacity.getAmount(a) - content.getAmount(a)));
		content.add(a, amount - rtn);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return rtn;
	}

	/*
	 * IAspectContainer
	 */

	@Override
	public boolean takeFromContainer(Aspect a, int amount) 
	{
		if (essentiaOutput)
		{
			return takeEssentia(a, amount);
		}
		return false;
	}

	@Override
	public int addToContainer(Aspect a, int amount) 
	{
		if (essentiaInput)
		{
			return addEssentia(a, amount);
		}
		
		return amount;
	}

	@Override
	public int containerContains(Aspect a) 
	{
		return content.getAmount(a);
	}

	@Override
	public boolean doesContainerAccept(Aspect a) 
	{
		return essentiaOutput && capacity.getAmount(a) > 0;
	}

	@Override
	public boolean doesContainerContain(AspectList al) 
	{
		for (Aspect a : al.getAspects())
		{
			if (content.getAmount(a) < al.getAmount(a))
				return false;
		}
		
		return true;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect a, int amount) 
	{
		return content.getAmount(a) >= amount;
	}

	@Override
	public AspectList getAspects() 
	{
		return content;
	}

	@Override
	public void setAspects(AspectList arg0) 
	{
	}

	@Override
	public boolean takeFromContainer(AspectList al) 
	{
		if (essentiaOutput && doesContainerContain(al))
		{
			for (Aspect a : al.getAspects())
			{
				takeFromContainer(a, al.getAmount(a));
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return true;
		}
		return false;
	}
	
	/*
	 * IEssentiaTransport
	 */

	@Override
	public boolean isConnectable(ForgeDirection face) 
	{
		switch (face) 
		{
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
		case DOWN:
			return essentiaInput || essentiaOutput;
		default:
			return false;
		}
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) 
	{
		return essentiaInput && isConnectable(face);
	}
	
	@Override
	public boolean canOutputTo(ForgeDirection face) 
	{
		return essentiaOutput && isConnectable(face);
	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) 
	{
		return suction;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) 
	{
		return 128;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		return canInputFrom(face) ? addToContainer(aspect, amount) : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		if (canOutputTo(face))
		{
			if (takeFromContainer(aspect, amount))
			{
				return amount;
			}
		}
		return 0;
	}
	
	@Override
	public int getEssentiaAmount(ForgeDirection face) 
	{
		return content.getAmount(suction);
	}
	
	@Override
	public Aspect getEssentiaType(ForgeDirection face) 
	{
		return suction;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public int getMinimumSuction() {return 128;}

	@Override
	public boolean renderExtendedTube() {return true;}

}
