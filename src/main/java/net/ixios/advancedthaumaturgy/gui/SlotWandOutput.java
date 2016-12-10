package net.ixios.advancedthaumaturgy.gui;

import java.util.Map;

import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.ItemWandCasting;

public class SlotWandOutput extends Slot
{
	private TileWandbench bench;
	private Container cont;
	
	public SlotWandOutput(Container cont, TileWandbench bench, int slot, int x, int y)
	{
		super(bench, slot, x, y);

		this.bench = bench;
		this.cont = cont;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false; // Output only
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer p)
	{
		return bench.canCraft(p);
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
	{
		// Crafting cost
		if (bench.getCost().size() > 0 && inventory.getStackInSlot(5) != null)
		{
			ItemWandCasting w = (ItemWandCasting) inventory.getStackInSlot(5).getItem();
			w.consumeAllVisCrafting(inventory.getStackInSlot(5), player, bench.getCost(), true);
			cont.detectAndSendChanges();
		}
		
		// Clear used ingredients
		Map<Integer, Integer> used = bench.getUsedIngredients();
		for (Integer slot : used.keySet())
		{
			inventory.decrStackSize(slot.intValue(), used.get(slot).intValue());
		}
		
		super.onPickupFromSlot(player, stack);
	}
}
