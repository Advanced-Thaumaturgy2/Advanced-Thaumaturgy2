package net.ixios.advancedthaumaturgy.tileentities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileWandbench extends TileEntity implements IInventory
{
	private ItemStack[] slots;
	
	private Map<Integer, Integer> used;
	private int cost;
	
	public TileWandbench()
	{
		slots = new ItemStack[6];
		Arrays.fill(slots, null);
		used = new HashMap<Integer, Integer>();
		cost = 0;
	}
	
	/**
	 * @return A map <Slot Number> -> <Amount to be removed>
	 */
	public Map<Integer, Integer> getUsedIngredients()
	{
		return used;
	}
	
	public AspectList getCost()
	{
		AspectList rtn = new AspectList();
		if (cost > 0)
			for (Aspect a : Aspect.getPrimalAspects())
				rtn.add(a, cost);
		return rtn;
	}
	
	public Map<Aspect, Float> getRealCost(EntityPlayer player)
	{
		AspectList base = getCost();
		Map<Aspect, Float> rtn = new HashMap<Aspect, Float>();
		
		if (cost > 0)
		{
			if (slots[5] != null)
			{
				ItemWandCasting w = (ItemWandCasting) slots[5].getItem();
			    for (Aspect a : base.getAspects())
			    {
			    	rtn.put(a, base.getAmount(a) * w.getConsumptionModifier(slots[5], player, a, true));
			    }
			}
			else
			{
				for (Aspect a : base.getAspects())
					rtn.put(a, (float) base.getAmount(a));
			}
		}
		
		return rtn;
	}
	
	public boolean canCraft(EntityPlayer player)
	{
		if (slots[0] == null)
			return false;
		
		if (cost > 0)
		{
			if (slots[5] == null || !(slots[5].getItem() instanceof ItemWandCasting))
				return false;
			
			ItemWandCasting w = (ItemWandCasting) slots[5].getItem();
			if (!w.consumeAllVisCrafting(slots[5], player, getCost(), false))
				return false;
		}
		
		return true;
	}
	
	private boolean swapRod()
	{
		ItemWandCasting w = (ItemWandCasting) slots[0].getItem();
		WandRod rod = TCItems.getRod(slots[3]);
		
		if (rod != null)
		{
			if (!w.getRod(slots[0]).equals(rod))
			{
				w.setRod(slots[0], rod);
				
				// Transform into MercurialWand
				if (rod instanceof ItemMercurialRod)
				{
					ItemStack mercWand = new ItemStack(AdvThaum.MercurialWand);
					mercWand.setTagCompound(slots[0].getTagCompound());
					slots[0] = mercWand;
					w = (ItemWandCasting) mercWand.getItem();
				}
				// Downgrade
				else if (w instanceof ItemMercurialWand)
				{
					ItemStack wand = new ItemStack(ConfigItems.itemWandCasting);
					wand.setTagCompound(slots[0].getTagCompound());
					slots[0] = wand;
					w = (ItemWandCasting) wand.getItem();
				}
				
				for (Aspect a : Aspect.getPrimalAspects())
				{
					int amount = w.getVis(slots[0], a);
					w.storeVis(slots[0], a, Math.min(amount, rod.getCapacity() * 100));
				}
				
				used.put(3, 1);
				
				if (w.getCap(slots[0]) != null)
				{
					cost = rod.getCraftCost() * w.getCap(slots[0]).getCraftCost();
				}
				else
				{
					cost = rod.getCraftCost();
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean swapCaps()
	{
		ItemWandCasting w = (ItemWandCasting) slots[0].getItem();
		WandCap cap = TCItems.getCap(slots[4]);
		
		if (cap != null)
		{
			if (!w.getCap(slots[0]).equals(cap))
			{
				w.setCap(slots[0], cap);
				used.put(4, 2);
				
				if (w.getRod(slots[0]) != null)
				{
					cost = cap.getCraftCost() * w.getRod(slots[0]).getCraftCost();
				}
				else
				{
					cost = cap.getCraftCost();
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean applyUpgrades()
	{
		ItemArcaneCrystal.Upgrades upgrade = ((ItemArcaneCrystal) slots[2].getItem()).getUpgradeFromStack(slots[2]);
		if (!AdvThaum.MercurialWand.hasUpgrade(slots[0], upgrade))
		{
			AdvThaum.MercurialWand.addUpgrade(slots[0], upgrade);
			used.put(2, 1);
			cost += 42;
		}
		
		return true;
	}
	
	public void buildWand()
	{
		used.clear();
		slots[0] = null;
		cost = 0;
		
		if (slots[1] != null && slots[1].getItem() instanceof ItemWandCasting)
		{
			slots[0] = slots[1].copy();
			used.put(1, 1);
			
			if (slots[3] != null)
			{
				if (!swapRod())
				{
					used.clear();
					slots[0] = null;
					cost = 0;
					return;
				}
			}
			
			if (slots[4] != null && slots[4].stackSize >= 2)
			{
				if (!swapCaps())
				{
					used.clear();
					slots[0] = null;
					cost = 0;
					return;
				}
			}
			
			if (slots[0].getItem() instanceof ItemMercurialWand
					&& slots[2] != null && slots[2].getItem() instanceof ItemArcaneCrystal
					&& ((ItemArcaneCrystal) slots[2].getItem()).isWandUpgrade(slots[2]))
			{
				applyUpgrades();
			}
		}
	}
	
	@Override
	public void markDirty()
	{
		buildWand();
		
		super.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	    super.readFromNBT(nbt);
	    
	    Arrays.fill(slots, null);
	    NBTTagList list = nbt.getTagList("items", 10);
	    for (int i = 0; i < list.tagCount(); i++)
    	{
	    	NBTTagCompound slot = list.getCompoundTagAt(i);
	    	Byte index = slot.getByte("index");
	    	
	    	if (index >= 0 && index <= slots.length)
	    		slots[index] = ItemStack.loadItemStackFromNBT(slot);
    	}
	}
	
	@Override 
	public void writeToNBT(NBTTagCompound nbt)
	{
	    super.writeToNBT(nbt);
	    
	    NBTTagList list = new NBTTagList();
	    for (Byte i = 0; i < slots.length; i++)
	    {
	    	if (slots[i] != null)
	    	{
		    	NBTTagCompound slot = new NBTTagCompound();
		    	slot.setInteger("index", i);
		    	slots[i].writeToNBT(slot);
		    	list.appendTag(slot);
	    	}
	    }
	    nbt.setTag("items", list);
	}

	@Override
	public int getSizeInventory()
	{
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return i < slots.length ? slots[i] : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int amount)
	{
		if (i >= slots.length || slots[i] == null)
			return null;
		
		if (slots[i].stackSize <= amount)
		{
			ItemStack rtn = slots[i];
			slots[i] = null;
			return rtn;
		}

		return slots[i].splitStack(amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
        slots[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
    	}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
	{
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false 
        		: p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if (i < getSizeInventory())
		{
			if (slots[i] == null || slots[i].stackSize + stack.stackSize <= getInventoryStackLimit())
			{
				switch (i)
				{
				case 0:
					return false;
				case 1:
				case 5:
					return stack.getItem() instanceof ItemWandCasting;
				case 2:
					return stack.getItem() instanceof ItemArcaneCrystal;
				case 3:
					return TCItems.getRod(stack) != null;
				case 4:
					return TCItems.getCap(stack) != null;
				}
			}
		}
		return false;
	}

	@Override
	public void openInventory() { }
	@Override
	public void closeInventory() { }
	@Override
	public String getInventoryName()
	{
		return null;
	}
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

}
