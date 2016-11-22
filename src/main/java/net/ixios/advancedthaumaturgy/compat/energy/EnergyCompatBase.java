package net.ixios.advancedthaumaturgy.compat.energy;


import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.models.ModelEngine;
import net.ixios.advancedthaumaturgy.renderers.GenericRenderer;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.MinecraftForgeClient;

public abstract class EnergyCompatBase 
{
	protected static boolean ispresent = false;
	
	public abstract void register();
	
	public static boolean isPresent()
	{
		return ispresent;
	}
	
	public static void forceEnable()
	{
		ispresent = true;
	}
	
}
