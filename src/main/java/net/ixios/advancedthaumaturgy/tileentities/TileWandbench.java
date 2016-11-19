package net.ixios.advancedthaumaturgy.tileentities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.items.wands.ItemWandCap;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.ItemWandRod;

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
	}
	
	/**
	 * @return A map <Slot Number> -> <Amount to be removed>
	 */
	public Map<Integer, Integer> getUsedIngredients()
	{
		return used;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public boolean canCraft()
	{
		return true;
	}
	
	/**
	 * @param in WandRod to apply
	 * @param out Wand ItemStack that is to be modified, must be valid
	 * @return whether this rod can be applied
	 */
	private boolean swapRod(ItemStack in, ItemStack out)
	{
		ItemWandCasting w = (ItemWandCasting) out.getItem();
		
		// Find the actual rod
		WandRod rod = null;
		for (WandRod r : WandRod.rods.values())
		{
			if (r.getItem().getItem().equals(in.getItem()) &&
					r.getItem().getItemDamage() == in.getItemDamage())
			{
				rod = r;
				break;
			}
		}
		
		if (rod != null)
		{
			if (!w.getRod(out).equals(rod))
			{
				w.setRod(out, rod);
				
				// Transform into MercurialWand
				if (rod instanceof ItemMercurialRod)
				{
					ItemStack mercWand = new ItemStack(AdvThaum.MercurialWand);
					mercWand.setTagCompound(slots[0].getTagCompound());
					out = mercWand;
					w = (ItemWandCasting) mercWand.getItem();
				}
				
				for (Aspect a : Aspect.getPrimalAspects())
				{
					int amount = w.getVis(slots[0], a);
					w.storeVis(slots[0], a, Math.min(amount, rod.getCapacity() * 100));
				}
				
				used.put(3, 1);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @param in WandCap to apply
	 * @param out Wand ItemStack that is to be modified, must be valid
	 * @return whether caps can be applied
	 */
	public boolean swapCaps(ItemStack in, ItemStack out)
	{
		ItemWandCasting w = (ItemWandCasting) out.getItem();
		
		// Find the actual cap
		WandCap cap = null;
		for (WandCap c : WandCap.caps.values())
		{
			if (c.getItem().getItem().equals(in.getItem()) &&
					c.getItem().getItemDamage() == in.getItemDamage())
			{
				cap = c;
				break;
			}
		}
		
		if (cap != null)
		{
			if (!w.getCap(out).equals(cap))
			{
				w.setCap(out, cap);
				used.put(4, 2);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @param in Crystal upgrade to apply
	 * @param out Wand ItemStack that is to be modified, must be valid
	 * @return whether upgrade can be applied
	 */
	public boolean applyUpgrades(ItemStack in, ItemStack out)
	{
		ItemArcaneCrystal.Upgrades upgrade = ((ItemArcaneCrystal) slots[2].getItem()).getUpgradeFromStack(slots[2]);
		if (!AdvThaum.MercurialWand.hasUpgrade(slots[0], upgrade))
		{
			AdvThaum.MercurialWand.addUpgrade(slots[0], upgrade);
			used.put(2, 1);
		}
		
		return true;
	}
	
	public void buildWand()
	{
		used.clear();
		slots[0] = null;
		
		if (slots[1] != null && slots[1].getItem() instanceof ItemWandCasting)
		{
			slots[0] = slots[1].copy();
			used.put(1, 1);
			
			if (slots[3] != null)
			{
				if (!swapRod(slots[3], slots[0]))
				{
					slots[0] = null;
					return;
				}
			}
			
			if (slots[4] != null && slots[4].stackSize >= 2)
			{
				if (!swapCaps(slots[4], slots[0]))
				{
					slots[0] = null;
					return;
				}
			}
			
			if (slots[0].getItem() instanceof ItemMercurialWand
					&& slots[2] != null && slots[2].getItem() instanceof ItemArcaneCrystal
					&& ((ItemArcaneCrystal) slots[2].getItem()).isWandUpgrade(slots[2]))
			{
				applyUpgrades(slots[2], slots[0]);
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
					return stack.getItem() instanceof ItemWandRod;
				case 4:
					return stack.getItem() instanceof ItemWandCap;
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
