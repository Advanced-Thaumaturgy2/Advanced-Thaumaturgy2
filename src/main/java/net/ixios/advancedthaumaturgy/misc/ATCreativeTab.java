package net.ixios.advancedthaumaturgy.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ATCreativeTab extends CreativeTabs 
{

    public ATCreativeTab(String label)
    {
    	super(label);
    }

    @Override
    public Item getTabIconItem() {
        return null;
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ConfigItems.itemResource, 1, 3);
    }
}
