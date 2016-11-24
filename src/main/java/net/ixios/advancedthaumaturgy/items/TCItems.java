package net.ixios.advancedthaumaturgy.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class TCItems 
{
	public static Map<Item, Map<Integer, WandRod>> rodItems;
	public static Map<Item, Map<Integer, WandCap>> capItems;
	
	public static void registerRodItems()
	{
		rodItems = new HashMap<Item, Map<Integer, WandRod>>();
		for (WandRod r : WandRod.rods.values())
		{
			ItemStack i = r.getItem();
			if (i != null && i.getItem() != null)
			{
				Map<Integer, WandRod> mapping = rodItems.get(r.getItem().getItem());
				if (mapping == null)
					mapping = new HashMap<Integer, WandRod>();
				mapping.put(i.getItemDamage(), r);
				rodItems.put(r.getItem().getItem(), mapping);
			}
		}
	}
	
	public static void registerCapItems()
	{
		capItems = new HashMap<Item, Map<Integer, WandCap>>();
		for (WandCap r : WandCap.caps.values())
		{
			ItemStack i = r.getItem();
			if (i != null && i.getItem() != null)
			{
				Map<Integer, WandCap> mapping = capItems.get(r.getItem().getItem());
				if (mapping == null)
					mapping = new HashMap<Integer, WandCap>();
				mapping.put(i.getItemDamage(), r);
				capItems.put(r.getItem().getItem(), mapping);
			}
		}
	}
	
	public static WandRod getRod(ItemStack stack)
	{
		if (stack != null && stack.getItem() != null)
		{
			Map<Integer, WandRod> mapping = rodItems.get(stack.getItem());
			if (mapping != null)
			{
				return mapping.get(stack.getItemDamage());
			}
		}
		
		return null;
	}
	
	public static WandCap getCap(ItemStack stack)
	{
		if (stack != null && stack.getItem() != null)
		{
			Map<Integer, WandCap> mapping = capItems.get(stack.getItem());
			if (mapping != null)
			{
				return mapping.get(stack.getItemDamage());
			}
		}
		
		return null;
	}
	
	public static ItemStack airshard = new ItemStack(ConfigItems.itemShard, 1, 0);
	public static ItemStack watershard = new ItemStack(ConfigItems.itemShard, 1, 2);
	public static ItemStack fireshard = new ItemStack(ConfigItems.itemShard, 1, 1);
	public static ItemStack earthshard = new ItemStack(ConfigItems.itemShard, 1, 3);
	public static ItemStack ordoshard = new ItemStack(ConfigItems.itemShard, 1, 5);
	public static ItemStack entropyshard = new ItemStack(ConfigItems.itemShard, 1, 4);
	public static ItemStack anyshard = new ItemStack(ConfigItems.itemShard, 1, 32767);
	
	public static ItemStack aircluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 0);
	public static ItemStack watercluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 1);
	public static ItemStack firecluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 2);
	public static ItemStack earthcluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 3);
	public static ItemStack ordocluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 4);
	public static ItemStack entropycluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 5);
	public static ItemStack anycluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 32767);
	
	public static ItemStack alumentum = new ItemStack(ConfigItems.itemResource, 1, 0);
	public static ItemStack nitor = new ItemStack(ConfigItems.itemResource, 1, 1);
	public static ItemStack thaumiumingot = new ItemStack(ConfigItems.itemResource, 1, 2);
	public static ItemStack quicksilver = new ItemStack(ConfigItems.itemResource, 1, 3);
	public static ItemStack tallow = new ItemStack(ConfigItems.itemResource, 1, 4);
	public static ItemStack brain = new ItemStack(ConfigItems.itemResource, 1, 5);
	public static ItemStack amber = new ItemStack(ConfigItems.itemResource, 1, 6);
	public static ItemStack cloth = new ItemStack(ConfigItems.itemResource, 1, 7);
	public static ItemStack filter = new ItemStack(ConfigItems.itemResource, 1, 8);
	public static ItemStack fragment = new ItemStack(ConfigItems.itemResource, 1, 9);
	public static ItemStack mirror = new ItemStack(ConfigItems.itemResource, 1, 10);
	public static ItemStack slime = new ItemStack(ConfigItems.itemResource, 1, 11);
	public static ItemStack taint = new ItemStack(ConfigItems.itemResource, 1, 12);
	public static ItemStack tendril = new ItemStack(ConfigItems.itemResource, 1, 13);
	public static ItemStack label = new ItemStack(ConfigItems.itemResource, 1, 14);
	public static ItemStack dust = new ItemStack(ConfigItems.itemResource, 1, 15);
	
	public static ItemStack arcanefurance = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 0);
	public static ItemStack arcanepedestal = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 1);
	public static ItemStack wandpedestal = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5);
	public static ItemStack wandrechargefocus = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 8);
	
	public static ItemStack totem = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 0	);
	public static ItemStack tile = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1);
	
}
