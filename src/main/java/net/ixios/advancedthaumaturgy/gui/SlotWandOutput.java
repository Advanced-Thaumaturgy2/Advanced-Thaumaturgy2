package net.ixios.advancedthaumaturgy.gui;

import java.util.Map;

import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotWandOutput extends Slot
{
	private TileWandbench bench;
	
	public SlotWandOutput(TileWandbench bench, int slot, int x, int y)
	{
		super(bench, slot, x, y);

		this.bench = bench;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false; // Output only
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
	{
		if (bench.canCraft())
		{
			// Clear used ingredients
			Map<Integer, Integer> used = bench.getUsedIngredients();
			for (Integer slot : used.keySet())
			{
				inventory.decrStackSize(slot.intValue(), used.get(slot).intValue());
			}
			super.onPickupFromSlot(player, stack);
		}
	}
}
